package controllers

import db.DbContext
import io.circe.generic.auto._
import io.circe.syntax._
import json.MeetupResponse
import models.{Meetup, Meetups, MeetupsMembers, Member, Venues}
import play.api.libs.circe.Circe
import play.api.mvc._

class MeetupController(cc: ControllerComponents,
                       meetupService: Meetups,
                       venueService: Venues,
                       meetupsMembersService: MeetupsMembers,
                       ctx: DbContext) extends AbstractController(cc) with Circe {

  def findAll(groupId: Int) = Action {
    ctx.transaction {
      Ok(meetupService.findAll(groupId).map(toMeetupResponse).asJson)
    }
  }


  def find(groupId: Int, eventId: Int) = Action {
    meetupService.find(eventId).fold(NotFound(""))(m => Ok(toMeetupResponse(m).asJson))
  }

  def create(groupId: Int) = Action(circe.json[Meetup]) { request =>
    val meetup: Meetup = request.body.copy(groupId = groupId)
    ctx.transaction {
      val id = meetupService.create(meetup)
      val Some(venue) = venueService.find(meetup.venueId)
      val members = meetupsMembersService.find(groupId)
      Ok(MeetupResponse(meetup.copy(id = id), venue, members).asJson)
    }
  }

  def join(eventId: Int, memberId: Int) = Action {
    meetupsMembersService.create(eventId, memberId)

    val Some(meetup) = meetupService.find(eventId)

    Ok(toMeetupResponse(meetup).asJson)
  }

  def toMeetupResponse(meetup: Meetup) = {
    val Some(venue) = venueService.find(meetup.venueId)
    val members: Seq[Member] = meetupsMembersService.find(meetup.id)
    MeetupResponse(meetup, venue, members)
  }
}
