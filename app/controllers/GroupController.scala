package controllers

import db.DbContext
import io.circe.generic.auto._
import io.circe.syntax._
import json.{GroupJoinRequest, GroupRequest, GroupResponse}
import models.{Groups, GroupsMembers, Members}
import play.api.libs.circe.Circe
import play.api.mvc._

class GroupController(cc: ControllerComponents,
                      memberService: Members,
                      groupService: Groups,
                      groupsMembersService: GroupsMembers,
                      ctx: DbContext) extends AbstractController(cc) with Circe {

  def findAll() = Action {
    val groups = groupService.findAll() map { case (group, members) => GroupResponse(group.id, group.name, members) }
    Ok(groups.toList.sortBy(_.groupId).asJson)
  }

  def create() = Action(circe.json[GroupRequest]) { request =>
    val GroupRequest(groupName, adminMemberIds) = request.body

    ctx.transaction {
      val groupId = groupService.create(groupName)

      adminMemberIds foreach {
        groupsMembersService.create(groupId, _)
      }

      val adminMembers = adminMemberIds flatMap memberService.find

      Ok(GroupResponse(groupId, groupName, adminMembers).asJson)
    }
  }

  def join(groupId: Int, memberId: Int) = Action(circe.json[GroupJoinRequest]) { request =>
    val isAdmin = request.body.admin

    ctx.transaction {
      groupsMembersService.create(groupId, memberId)

      val Some(group) = groupService.find(groupId)
      val adminMembers = groupsMembersService.find(groupId)

      Ok(GroupResponse(groupId, group.name, adminMembers).asJson)
    }
  }
}
