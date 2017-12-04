import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.{BaseOneAppPerTest, PlaySpec}
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._

class GroupControllerSpec extends PlaySpec
  with BaseOneAppPerTest
  with AppApplicationFactory
  with BeforeAndAfterEach
  with JdbcContext
  with Util {
  import ctx._

  override def beforeEach() {
    val q = quote {
      infix"TRUNCATE groups, groups_members, members".as[Action[Int]]
    }
    ctx.run(q)
  }

  "POST /groups" should {
    "return OK when a new group is created" in {
      val id1 = createMember("Shinichi", "Katayama", "s@test.com")
      val id2 = createMember("Kenji", "Nakamura", "k@test.com")

      val json = Json.parse(
        s"""
           |{
           |  "group-name": "clj-nakano",
           |  "admin-member-ids": [
           |    $id1, $id2
           |  ]
           |}
        """.stripMargin)
      val Some(result) = route(app, FakeRequest(POST, "/groups").withJsonBody(json))
      val responseJson = contentAsJson(result)
      (responseJson \ "group-id").as[Int]
    }
  }

  "POST /members/{member-id}/groups/{group-id}" should {
    "return OK" in {
      val memberId = createMember("Shinichi", "Katayama", "s@test.com")
      val memberId2 = createMember("Kenji", "Nakamura", "k@test.com")
      val groupId = createGroup(memberId)

      val json = Json.parse(
        s"""
          |{
          |  "admin": true
          |}
        """.stripMargin)
      val Some(result) = route(app, FakeRequest(POST, s"/members/$memberId2/groups/$groupId").withJsonBody(json))

      status(result) mustEqual OK
    }
  }
}
