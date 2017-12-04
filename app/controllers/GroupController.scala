package controllers

import db.DbContext
import io.circe.generic.auto._
import io.circe.syntax._
import json.{GroupRequest, GroupResponse}
import models.{Groups, GroupsMembers, Members}
import play.api.libs.circe.Circe
import play.api.mvc._

class GroupController(cc: ControllerComponents,
                      memberService: Members,
                      groupService: Groups,
                      groupsMemerService: GroupsMembers,
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
        groupsMemerService.create(groupId, _)
      }

      val adminMembers = adminMemberIds flatMap memberService.find

      Ok(GroupResponse(groupId, groupName, adminMembers).asJson)
    }

  }
}
