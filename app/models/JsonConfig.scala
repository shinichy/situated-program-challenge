package models

import io.circe.generic.extras.Configuration

trait JsonConfig {
  val kebabCaseTransformation: String => String = _.replaceAll(
    "([A-Z]+)([A-Z][a-z])",
    "$1-$2"
  ).replaceAll("([a-z\\d])([A-Z])", "$1-$2").toLowerCase

  implicit val config = Configuration.default.copy(transformKeys = kebabCaseTransformation).withDefaults
}
