package controllers

import io.circe.generic.auto._
import io.circe.syntax._
import models.{Member, Members}
import play.api.libs.circe.Circe
import play.api.mvc._

class MemberController(cc: ControllerComponents) extends AbstractController(cc) with Circe {

  def findAll() = Action {
    Ok(Members.findAll().asJson)
  }

  def find(id: Int) = Action {
    Members.find(id).fold(NotFound(""))(Member => Ok(Member.asJson))
  }

  def create() = Action(circe.json[Member]) { request =>
    Members.create(request.body)
    Ok("ok")
  }
}
