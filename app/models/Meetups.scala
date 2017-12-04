package models

import java.time.ZonedDateTime

import db.DbContext
import io.circe.generic.extras._
import io.circe.java8.time._

@ConfiguredJsonCodec
case class Meetup(@JsonKey("event-id") id: Int = Int.MinValue,
                  groupId: Int = Int.MinValue,
                  title: String,
                  startAt: ZonedDateTime,
                  endAt: ZonedDateTime,
                  venueId: Int)

class Meetups(val ctx: DbContext) {
  import ctx._

  val meetups = quote(querySchema[Meetup]("meetups"))

  def findAll(groupId: Int) = run(meetups.filter(_.groupId == lift(groupId)))

  def find(eventId: Int) = run(meetups.filter(m => m.id == lift(eventId))).headOption

  def create(meetup: Meetup) = run(quote(meetups.insert(lift(meetup)).returning(_.id)))
}
