import org.scalatestplus.play.BaseOneAppPerTest
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._

trait Util { self: BaseOneAppPerTest =>

  def createMember(firstName: String, lastName: String, email: String): Int = {
    val json = Json.parse(
      s"""
         |{
         |  "first-name": "$firstName",
         |  "last-name": "$lastName",
         |  "email": "$email"
         |}
      """.stripMargin)
    val Some(result) = route(app, FakeRequest(POST, "/members").withJsonBody(json))
    val responseJson = contentAsJson(result)
    (responseJson \ "member-id").as[Int]
  }

  def createGroup(ids: Int*): Int = {
    val idsStr = ids.toSeq.map(_.toString).mkString(",")
    val json = Json.parse(
      s"""
         |{
         |  "group-name": "clj-nakano",
         |  "admin-member-ids": [
         |    $idsStr
         |  ]
         |}
        """.stripMargin)
    val Some(result) = route(app, FakeRequest(POST, "/groups").withJsonBody(json))
    val responseJson = contentAsJson(result)
    (responseJson \ "group-id").as[Int]
  }

  def createVenue(groupId: Int, name: String, postalCode: String, prefecture: String, city: String, address1: String, address2: String, building: String) = {
    val json = Json.parse(
      s"""
         |{
         |  "venue-name": "$name",
         |  "address": {
         |    "postal-code": "$postalCode",
         |    "prefecture": "$prefecture",
         |    "city": "$city",
         |    "address1": "$address1",
         |    "address2": "$address2",
         |    "building": "$building"
         |  }
         |}
      """.stripMargin)
    val Some(result) = route(app, FakeRequest(POST, s"/groups/$groupId/venues").withJsonBody(json))
    val responseJson = contentAsJson(result)
    (responseJson \ "venue-id").as[Int]
  }
}
