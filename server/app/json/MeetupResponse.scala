package json

import java.time.ZonedDateTime

import io.circe.generic.extras.ConfiguredJsonCodec
import io.circe.java8.time._
import models.{Meetup, Member, Venue}

@ConfiguredJsonCodec
case class MeetupResponse(eventId: Int,
                          title: String,
                          startAt: ZonedDateTime,
                          endAt: ZonedDateTime,
                          venue: VenueResponse,
                          members: Seq[Member])

object MeetupResponse {
  def apply(meetup: Meetup, venue: Venue, members: Seq[Member]): MeetupResponse = {
    MeetupResponse(meetup.id, meetup.title, meetup.startAt, meetup.endAt, VenueResponse(venue), members)
  }
}
