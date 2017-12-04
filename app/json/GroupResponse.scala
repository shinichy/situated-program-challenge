package json

import io.circe.generic.extras.ConfiguredJsonCodec
import models.Member

@ConfiguredJsonCodec
case class GroupResponse(groupId: Int, groupName: String, admin: Seq[Member])
