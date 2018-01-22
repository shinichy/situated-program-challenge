import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.{BaseOneAppPerTest, PlaySpec}
import play.api.libs.json.{JsArray, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._

class OnlineVenueControllerSpec extends PlaySpec
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

  "GET /groups/{group-id}/online-venues" should {
    "return OK" in {
      val memberId = createMember("Shinichi", "Katayama", "s@test.com")
      val groupId = createGroup("clj-nakano", memberId)
      createOnlineVenue(groupId, "online1", "https://online1.com")
      createOnlineVenue(groupId, "online2", "https://online2.com")

      val Some(result) = route(app, FakeRequest(GET, s"/groups/$groupId/online-venues"))
      status(result) mustEqual OK
      val responseJson = contentAsJson(result)
      responseJson.as[JsArray].value.size mustEqual 2

      (responseJson(0) \ "group-id").isEmpty mustEqual true
    }
  }

  "POST /groups/{group-id}/online-venues" should {
    "return OK" in {
      val memberId = createMember("Shinichi", "Katayama", "s@test.com")
      val groupId = createGroup("clj-nakano", memberId)

      val name = "Online"
      val url = "http://online1.com"

      val json = Json.parse(
        s"""
           |{
           |  "venue-name": "$name",
           |  "url": "$url"
           |}
      """.stripMargin)
      val Some(result) = route(app, FakeRequest(POST, s"/groups/$groupId/online-venues").withJsonBody(json))
      status(result) mustEqual OK
      val responseJson = contentAsJson(result)
      (responseJson \ "online-venue-id").as[Int] must be > 0
      (responseJson \ "group-id").isEmpty mustEqual true
      (responseJson \ "venue-name").as[String] mustEqual name
      (responseJson \ "url").as[String] mustEqual url
    }
  }
}
