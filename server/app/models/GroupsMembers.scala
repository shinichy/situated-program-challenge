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

  def findMembers(groupId: Int) = {
    val q = quote {
      for {
        gm <- groupsMembers if gm.groupId == lift(groupId)
        m <- members if m.id == gm.memberId
      } yield (gm.admin, m)
    }

    val membersMap = run(q).groupBy(_._1).mapValues(_ map (_._2))
    (membersMap.getOrElse(true, Nil), membersMap.getOrElse(false, Nil))
  }
}
