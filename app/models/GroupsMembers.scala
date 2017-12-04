package models

import db.DbContext

case class GroupsMember(groupId: Int, memberId: Int)

class GroupsMembers(val ctx: DbContext) {
  import ctx._

  val groupsMembers = quote(querySchema[GroupsMember]("groups_members"))
  val members = quote(querySchema[Member]("members"))

  def create(groupId: Int, memberId: Int) = {
    run(groupsMembers.insert(lift(GroupsMember(groupId, memberId))))
  }

  def find(id: Int) = {
    val q = quote {
      for {
        gm <- groupsMembers if gm.groupId == lift(id)
        m <- members if m.id == gm.memberId
      } yield m
    }

    run(q)
  }
}
