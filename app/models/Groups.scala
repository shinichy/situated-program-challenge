package models

import java.time.ZonedDateTime

import db.DbContext

case class Group(id: Int, name: String, createdAt: ZonedDateTime)

class Groups(val ctx: DbContext) {

  import ctx._

  val groups = quote(querySchema[Group]("groups"))
  val groupsMembers = quote(querySchema[GroupsMember]("groups_members"))
  val members = quote(querySchema[Member]("members"))

  def findAll(): Map[Group, List[Member]] = {
    val q = quote {
      for {
        g <- groups
        gm <- groupsMembers if gm.groupId == g.id
        m <- members if m.id == gm.memberId
      } yield (g, m)
    }

    run(q).groupBy(_._1).mapValues(_ map {_._2})
  }

  def find(id: Int) = run(groups.filter(_.id == lift(id))).headOption

  def create(name: String): Int = {
    val group = Group(-1, name, ZonedDateTime.now)
    run(groups.insert(lift(group)).returning(_.id))
  }
}
