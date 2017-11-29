package controllers

import io.circe.generic.auto._
import io.circe.syntax._
import models.{Meetup, Meetups}
import play.api.libs.circe.Circe
import play.api.mvc._

class MeetupController(cc: ControllerComponents) extends AbstractController(cc) with Circe {

  def findAll() = Action {
    Ok(Meetups.findAll().asJson)
  }

  def find(id: Int) = Action {
    Meetups.find(id).fold(NotFound(""))(meetup => Ok(meetup.asJson))
  }

  def create() = Action(circe.json[Meetup]) { request =>
    Meetups.create(request.body)
    Ok("ok")
  }
}
