package models

import db.DbContext

case class GroupsMember(groupId: Int, memberId: Int, admin: Boolean)

class GroupsMembers(val ctx: DbContext) {
  import ctx._

  val groupsMembers = quote(querySchema[GroupsMember]("groups_members"))
  val members = quote(querySchema[Member]("members"))

  def create(groupId: Int, memberId: Int, isAdmin: Boolean) = {
    run(groupsMembers.insert(lift(GroupsMember(groupId, memberId, isAdmin))))
  }

  def findAdmins(groupId: Int) = findMembersInternal(groupId, true)

  def findMembers(groupId: Int) = findMembersInternal(groupId, false)

  private def findMembersInternal(groupId: Int, isAdmin: Boolean) = {
    val q = quote {
      for {
        gm <- groupsMembers if gm.groupId == lift(groupId) && gm.admin == lift(isAdmin)
        m <- members if m.id == gm.memberId
      } yield m
    }

    run(q)
  }
}
