package json

import io.circe.generic.extras.ConfiguredJsonCodec

@ConfiguredJsonCodec
case class GroupRequest(groupName: String, adminMemberIds: Seq[Int])
