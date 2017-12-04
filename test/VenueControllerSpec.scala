import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.{BaseOneAppPerTest, PlaySpec}
import play.api.libs.json.{JsArray, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._

class VenueControllerSpec extends PlaySpec
  with BaseOneAppPerTest
  with AppApplicationFactory
  with BeforeAndAfterEach
  with JdbcContext
  with Util {

  import ctx._

  override def beforeEach() {
    val q = quote {
      infix"TRUNCATE groups, groups_members, members, venues".as[Action[Int]]
    }
    ctx.run(q)
  }

  "GET /groups/{group-id}/venues" should {
    "return OK" in {
      val memberId = createMember("Shinichi", "Katayama", "s@test.com")
      val groupId = createGroup(memberId)
      createVenue(groupId, "ICTCO", "164-0001", "Tokyo", "Nakano", "中野4丁目", "10-1", "NAKANO CENTRAL PARK EAST")
      createVenue(groupId, "ICTCO", "164-0001", "Tokyo", "Nakano", "中野4丁目", "10-1", "NAKANO CENTRAL PARK EAST")

      val Some(result) = route(app, FakeRequest(GET, s"/groups/$groupId/venues"))
      status(result) mustEqual OK
      val responseJson = contentAsJson(result)
      responseJson.as[JsArray].value.size mustEqual 2

      (responseJson(0) \ "group-id").isEmpty mustEqual true
    }
  }

  "POST /groups/{group-id}/venues" should {
    "return OK" in {
      val memberId = createMember("Shinichi", "Katayama", "s@test.com")
      val groupId = createGroup(memberId)

      val name = "ICTCO"
      val postalCode = "164-0001"
      val prefecture = "Tokyo"
      val city = "Nakano"
      val address1 = "中野4丁目"
      val address2 = "10-1"
      val building = "NAKANO CENTRAL PARK EAST"

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
      status(result) mustEqual OK
      val responseJson = contentAsJson(result)
      (responseJson \ "venue-id").as[Int] must be > 0
      (responseJson \ "group-id").isEmpty mustEqual true
      (responseJson \ "venue-name").as[String] mustEqual name
      (responseJson \ "address" \ "postal-code").as[String] mustEqual postalCode
      (responseJson \ "address" \ "prefecture").as[String] mustEqual prefecture
      (responseJson \ "address" \ "city").as[String] mustEqual city
      (responseJson \ "address" \ "address1").as[String] mustEqual address1
      (responseJson \ "address" \ "address2").as[String] mustEqual address2
      (responseJson \ "address" \ "building").as[String] mustEqual building
    }
  }
}
