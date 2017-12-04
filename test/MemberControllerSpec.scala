import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.{BaseOneAppPerTest, PlaySpec}
import play.api.libs.json.{JsArray, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._

class MemberControllerSpec extends PlaySpec
  with BaseOneAppPerTest
  with AppApplicationFactory
  with BeforeAndAfterEach
  with JdbcContext
  with Util {

  import ctx._

  override def beforeEach() {
    val q = quote {
      infix"TRUNCATE members".as[Action[Int]]
    }
    ctx.run(q)
  }

  "GET /members" should {
    "return OK" in {
      createMember("Shinichi", "Katayama", "test@test.cccom")
      createMember("Kenji", "Nakamura", "k2n@test.cccom")

      val Some(result) = route(app, FakeRequest(GET, "/members"))
      status(result) mustEqual OK
      val responseJson = contentAsJson(result)
      responseJson.as[JsArray].value.size mustEqual 2
    }
  }

  "GET /members/{member-id}" should {
    "return OK" in {
      val firstName = "Shinichi"
      val lastName = "Katayama"
      val email = "test@test.com"
      val id = createMember(firstName, lastName, email)

      val Some(result) = route(app, FakeRequest(GET, s"/members/$id"))
      status(result) mustEqual OK
      val responseJson = contentAsJson(result)
      (responseJson \ "member-id").as[Int] mustEqual id
      (responseJson \ "first-name").as[String] mustEqual firstName
      (responseJson \ "last-name").as[String] mustEqual lastName
      (responseJson \ "email").as[String] mustEqual email
    }
  }

  "POST /members" should {
    "return OK" in {
      val firstName = "Shinichi"
      val lastName = "Katayama"
      val email = "test@test.com"

      val json = Json.parse(
        s"""
           |{
           |  "first-name": "$firstName",
           |  "last-name": "$lastName",
           |  "email": "$email"
           |}
      """.stripMargin)
      val Some(result) = route(app, FakeRequest(POST, "/members").withJsonBody(json))
      status(result) mustEqual OK
      val responseJson = contentAsJson(result)
      (responseJson \ "member-id").as[Int] must be > 0
      (responseJson \ "first-name").as[String] mustEqual firstName
      (responseJson \ "last-name").as[String] mustEqual lastName
      (responseJson \ "email").as[String] mustEqual email
    }
  }
}
