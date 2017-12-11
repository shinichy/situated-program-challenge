package json

import io.circe.generic.extras.ConfiguredJsonCodec
import models.{Meetup, Member, Venue}

@ConfiguredJsonCodec
case class GroupResponse(groupId: Int,
                         groupName: String,
                         admin: Seq[Member],
                         venues: Seq[Venue],
                         meetups: Seq[Meetup],
                         members: Seq[Member])

@ConfiguredJsonCodec
case class GroupCreatedResponse(groupId: Int,
                                groupName: String,
                                admin: Seq[Member])
