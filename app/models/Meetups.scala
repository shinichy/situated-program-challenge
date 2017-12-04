package models

import java.time.LocalDateTime

import db.DbContext
import io.circe.generic.extras._
import io.circe.java8.time._

@ConfiguredJsonCodec
case class Meetup(@JsonKey("event-id") id: Int = Int.MinValue,
                  title: String,
                  startAt: LocalDateTime,
                  endAt: LocalDateTime,
                  venueId: Int)

class Meetups(val ctx: DbContext) {
  import ctx._

  val meetups = quote(querySchema[Meetup]("meetups"))

  def findAll() = run(meetups)

  def find(id: Int) = run(meetups.filter(_.id == lift(id))).headOption

  def create(meetup: Meetup) = run(quote(meetups.insert(lift(meetup)).returning(_.id)))
}
