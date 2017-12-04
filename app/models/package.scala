import io.circe.generic.extras._

package object models {
  implicit val jsonConfig = json.config
}
