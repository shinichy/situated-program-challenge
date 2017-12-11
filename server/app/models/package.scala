import java.time.{ZoneOffset, ZonedDateTime}
import java.util.Date

import io.getquill.MappedEncoding

package object models {
  implicit val jsonConfig = json.config
  implicit val encodeZonedDateTime = MappedEncoding[ZonedDateTime, Date](z => Date.from(z.toInstant))
  implicit val decodeZonedDateTime = MappedEncoding[Date, ZonedDateTime]( d => d.toInstant.atZone(ZoneOffset.UTC))
}
