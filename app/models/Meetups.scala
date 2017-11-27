package models

import java.time.LocalDateTime

import io.circe.generic.extras._
import io.getquill._
import io.circe.java8.time._

@ConfiguredJsonCodec case class Meetup(@JsonKey("event-id") id: Int, title: String, startAt: LocalDateTime, endAt: LocalDateTime, venueId: Int)

object Meetup {
  val kebabCaseTransformation: String => String = _.replaceAll("([a-z\\d])([A-Z])", "$1-$2").toLowerCase

  implicit val config = Configuration.default.copy(transformKeys = kebabCaseTransformation)
}

object Meetups {
  val ctx = new PostgresJdbcContext(SnakeCase, "ctx")

  import ctx._

  val meetups = quote(querySchema[Meetup]("meetups"))

  def findAll() = run(meetups)

  def find(id: Int) = run(meetups.filter(_.id == lift(id))).headOption

  def create(title: String) = run(quote(meetups.insert(_.title -> lift(title)).returning(_.id)))
}
