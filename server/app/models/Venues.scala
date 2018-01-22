package models

import db.DbContext
import io.circe.generic.extras.{ConfiguredJsonCodec, JsonKey}
import io.getquill.Embedded
import models.VenueType.VenueType

object VenueType extends Enumeration {
  type VenueType = Value
  val physical, online = Value
}

@ConfiguredJsonCodec
case class Address(postalCode: String,
                   prefecture: String,
                   city: String,
                   @JsonKey("address1") street1: String,
                   @JsonKey("address2") street2: String) extends Embedded

object Venue {
  implicit val encoder = io.circe.Encoder.enumEncoder(VenueType)
  implicit val decoder = io.circe.Decoder.enumDecoder(VenueType)
}

@ConfiguredJsonCodec
case class Venue(@JsonKey("venue-id") id: Int = Int.MinValue,
                 groupId: Int = Int.MinValue,
                 @JsonKey("venue-name") name: String,
                 address: Option[Address] = None,
                 url: Option[String] = None,
                 venueType: VenueType = VenueType.physical)

class Venues(val ctx: DbContext) {

  import ctx._

  implicit val venueTypeDecoder: Decoder[VenueType] = decoder((index, row) => VenueType.withName(row.getObject(index).toString))

  implicit val venueTypeEncoder: Encoder[VenueType] = encoder(java.sql.Types.OTHER, (index, value, row) => row.setObject(index, value.toString, java.sql.Types.OTHER))

  val venues = quote(querySchema[Venue]("venues"))

  def findAll(groupId: Int) = run(venues.filter(_.groupId == lift(groupId)))

  def findAllPhysical(groupId: Int) = run(venues.filter(v => v.groupId == lift(groupId) && v.venueType == lift(VenueType.physical)))

  def findAllOnline(groupId: Int) = run(venues.filter(v => v.groupId == lift(groupId) && v.venueType == lift(VenueType.online)))

  def find(venueId: Int) = run(venues.filter(_.id == lift(venueId))).headOption

  def create(venue: Venue): Int = {
    run(venues.insert(lift(venue)).returning(_.id))
  }
}
