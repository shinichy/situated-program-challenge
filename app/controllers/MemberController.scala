package controllers

import io.circe.generic.auto._
import io.circe.syntax._
import models.{Member, Members}
import play.api.libs.circe.Circe
import play.api.mvc._

class MemberController(cc: ControllerComponents,
                       memberService: Members) extends AbstractController(cc) with Circe {

  def findAll() = Action {
    Ok(memberService.findAll().asJson)
  }

  def find(id: Int) = Action {
    memberService.find(id).fold(NotFound(""))(Member => Ok(Member.asJson))
  }

  def create() = Action(circe.json[Member]) { request =>
    val member = request.body
    val id = memberService.create(member)
    Ok(member.copy(id = id).asJson)
  }
}
