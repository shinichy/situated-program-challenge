package json

import io.circe.generic.extras.ConfiguredJsonCodec
import models.Venue

@ConfiguredJsonCodec
case class OnlineVenueResponse(onlineVenueId: Int, venueName: String, url: String)

object OnlineVenueResponse {
  def apply(venue: Venue): OnlineVenueResponse = {
    OnlineVenueResponse(venue.id, venue.name, venue.url.get)
  }
}
