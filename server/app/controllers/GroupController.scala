package controllers

import db.DbContext
import io.circe.generic.auto._
import io.circe.syntax._
import json.{GroupCreatedResponse, GroupJoinRequest, GroupRequest, GroupResponse}
import models.{Groups, GroupsMembers, Meetups, Members, Venues}
import play.api.libs.circe.Circe
import play.api.mvc._

class GroupController(cc: ControllerComponents,
                      memberService: Members,
                      groupService: Groups,
                      groupsMembersService: GroupsMembers,
                      venueService: Venues,
                      meetupService: Meetups,
                      ctx: DbContext) extends AbstractController(cc) with Circe {

  def findAll() = Action {
    val groups = groupService.findAll() map { g => toGroupResponse(g.id, g.name) }
    Ok(groups.sortBy(_.groupId).asJson)
  }

  private def toGroupResponse(groupId: Int, groupName: String) = {
    val (admins, members) = groupsMembersService.findMembers(groupId)
    val venues = venueService.findAll(groupId)
    val meetups = meetupService.findAll(groupId)
    GroupResponse(groupId, groupName, admins, venues, meetups, members)
  }

  def create() = Action(circe.json[GroupRequest]) { request =>
    val GroupRequest(groupName, adminMemberIds) = request.body

    ctx.transaction {
      val groupId = groupService.create(groupName)

      adminMemberIds foreach {
        groupsMembersService.create(groupId, _, isAdmin = true)
      }

      val (admins, _) = groupsMembersService.findMembers(groupId)
      Ok(GroupCreatedResponse(groupId, groupName, admins).asJson)
    }
  }

  def join(groupId: Int, memberId: Int) = Action(circe.json[GroupJoinRequest]) { request =>
    val isAdmin = request.body.admin

    ctx.transaction {
      groupsMembersService.create(groupId, memberId, isAdmin = isAdmin)

      val Some(group) = groupService.find(groupId)

      Ok(toGroupResponse(groupId, group.name).asJson)
    }
  }
}
