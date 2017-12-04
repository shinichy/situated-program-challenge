package controllers

import db.DbContext
import io.circe.generic.auto._
import io.circe.syntax._
import json.MeetupResponse
import models.{GroupsMembers, Meetup, Meetups, Venues}
import play.api.libs.circe.Circe
import play.api.mvc._

class MeetupController(cc: ControllerComponents,
                       meetupService: Meetups,
                       venueService: Venues,
                       groupsMemberService: GroupsMembers,
                       ctx: DbContext) extends AbstractController(cc) with Circe {

  def findAll(groupId: Int) = Action {
    ctx.transaction {
      Ok(meetupService.findAll(groupId).map(toMeetupResponse).asJson)
    }
  }


  def find(groupId: Int, eventId: Int) = Action {
    meetupService.find(groupId, eventId).fold(NotFound(""))(m => Ok(toMeetupResponse(m).asJson))
  }

  def create(groupId: Int) = Action(circe.json[Meetup]) { request =>
    val meetup: Meetup = request.body.copy(groupId = groupId)
    ctx.transaction {
      val id = meetupService.create(meetup)
      val Some(venue) = venueService.find(meetup.venueId)
      val members = groupsMemberService.find(groupId)
      Ok(MeetupResponse(meetup.copy(id = id), venue, members).asJson)
    }
  }

  def toMeetupResponse(meetup: Meetup) = {
    val Some(venue) = venueService.find(meetup.venueId)
    val members = groupsMemberService.find(meetup.groupId)
    MeetupResponse(meetup, venue, members)
  }
}
