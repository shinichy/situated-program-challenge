import io.circe.generic.extras.Configuration

package object json {
    private val kebabCaseTransformation: String => String = _.replaceAll(
      "([A-Z]+)([A-Z][a-z])",
      "$1-$2"
    ).replaceAll("([a-z\\d])([A-Z])", "$1-$2").toLowerCase

    implicit val config = Configuration.default.copy(transformKeys = kebabCaseTransformation).withDefaults
}
