package models

import io.circe.generic.extras._

@ConfiguredJsonCodec
case class Member(@JsonKey("member-id") id: Int = Int.MinValue,
                  firstName: String,
                  lastName: String,
                  email: String) extends JsonConfig

object Member extends JsonConfig

object Members extends PostgresContext {

  import ctx._

  implicit val MemberInsertMeta = insertMeta[Member](_.id)

  val Members = quote(querySchema[Member]("members"))

  def findAll() = run(Members)

  def find(id: Int) = run(Members.filter(_.id == lift(id))).headOption

  def create(member: Member) = run(quote(Members.insert(lift(member)).returning(_.id)))
}
