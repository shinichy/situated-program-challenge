package models

import db.DbContext
import io.circe.generic.extras.{ConfiguredJsonCodec, JsonKey}
import io.getquill.Embedded

@ConfiguredJsonCodec
case class Address(postalCode: String,
                   prefecture: String,
                   city: String,
                   @JsonKey("address1") street1: String,
                   @JsonKey("address2") street2: String,
                   building: String = "") extends Embedded

@ConfiguredJsonCodec
case class Venue(@JsonKey("venue-id") id: Int = Int.MinValue,
                 groupId: Int = Int.MinValue,
                 @JsonKey("venue-name") name: String,
                 address: Address)

class Venues(val ctx: DbContext) {

  import ctx._

  val venues = quote(querySchema[Venue]("venues"))

  def findAll(groupId: Int) = run(venues.filter(_.groupId == lift(groupId)))

  def find(venueId: Int) = run(venues.filter(_.id == lift(venueId))).headOption

  def create(venue: Venue): Int = {
    run(venues.insert(lift(venue)).returning(_.id))
  }
}
