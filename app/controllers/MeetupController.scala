package controllers

import io.circe.generic.auto._
import io.circe.syntax._
import models.{Meetup, Meetups}
import play.api.libs.circe.Circe
import play.api.mvc._

class MeetupController(cc: ControllerComponents,
                       meetupService: Meetups) extends AbstractController(cc) with Circe {

  def findAll() = Action {
    Ok(meetupService.findAll().asJson)
  }

  def find(id: Int) = Action {
    meetupService.find(id).fold(NotFound(""))(meetup => Ok(meetup.asJson))
  }

  def create() = Action(circe.json[Meetup]) { request =>
    meetupService.create(request.body)
    Ok("ok")
  }
}
