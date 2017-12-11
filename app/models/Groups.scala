package models

import java.time.ZonedDateTime

import db.DbContext

case class Group(id: Int, name: String, createdAt: ZonedDateTime)

class Groups(val ctx: DbContext) {

  import ctx._

  val groups = quote(querySchema[Group]("groups"))
  val groupsMembers = quote(querySchema[GroupsMember]("groups_members"))
  val members = quote(querySchema[Member]("members"))

  def findAll() = run(groups)

  def find(id: Int) = run(groups.filter(_.id == lift(id))).headOption

  def create(name: String): Int = {
    val group = Group(-1, name, ZonedDateTime.now)
    run(groups.insert(lift(group)).returning(_.id))
  }
}
