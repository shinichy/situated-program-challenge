package json

import io.circe.generic.extras.ConfiguredJsonCodec
import models.{Address, Venue}

@ConfiguredJsonCodec
case class VenueResponse(venueId: Int, venueName: String, address: Address)

object VenueResponse {
  def apply(venue: Venue): VenueResponse = {
    VenueResponse(venue.id, venue.name, venue.address)
  }
}
