package controllers

import io.circe.generic.auto._
import io.circe.syntax._
import models.Meetups
import play.api.libs.circe.Circe
import play.api.mvc._

case class JsonMeetup(title: String)

class MeetupController(cc: ControllerComponents) extends AbstractController(cc) with Circe {

  def findAll() = Action {
    Ok(Meetups.findAll().asJson)
  }

  def find(id: Int) = Action {
    Meetups.find(id).fold(NotFound(""))(meetup => Ok(meetup.asJson))
  }

  def create() = Action(circe.json[JsonMeetup]) { request =>
    Meetups.create(request.body.title)
    Ok("ok")
  }
}
