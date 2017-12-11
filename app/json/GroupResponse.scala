package json

import io.circe.generic.extras.ConfiguredJsonCodec
import models.Member

@ConfiguredJsonCodec
// todo: venues, meetups
case class GroupResponse(groupId: Int, groupName: String, admin: Seq[Member], members: Seq[Member])
