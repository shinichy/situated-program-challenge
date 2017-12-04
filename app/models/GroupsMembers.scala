package models

import db.DbContext

case class GroupsMember(groupId: Int, memberId: Int)

class GroupsMembers(val ctx: DbContext) {
  import ctx._

  val groupsMembers = quote(querySchema[GroupsMember]("groups_members"))

  def create(groupId: Int, memberId: Int) = {
    run(groupsMembers.insert(lift(GroupsMember(groupId, memberId))))
  }
}
