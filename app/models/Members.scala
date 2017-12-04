package models

import io.circe.generic.extras._
import db.DbContext

@ConfiguredJsonCodec
case class Member(@JsonKey("member-id") id: Int = Int.MinValue,
                  firstName: String,
                  lastName: String,
                  email: String)

class Members(val ctx: DbContext) {
  import ctx._

  val members = quote(querySchema[Member]("members"))

  def findAll() = run(members)

  def find(id: Int) = run(members.filter(_.id == lift(id))).headOption

  def create(member: Member) = run(quote(members.insert(lift(member)).returning(_.id)))
}
