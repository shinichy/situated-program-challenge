import io.getquill.{PostgresJdbcContext, SnakeCase}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.{BaseOneAppPerTest, PlaySpec}
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._

class GroupControllerSpec extends PlaySpec with BaseOneAppPerTest with AppApplicationFactory with BeforeAndAfterEach {
  lazy val ctx = new PostgresJdbcContext(SnakeCase, "ctx")

  import ctx._

  override def beforeEach() {
    val q = quote {
      infix"TRUNCATE groups, groups_members, members".as[Action[Int]]
    }
    ctx.run(q)
  }

  override def afterEach() {
  }

  "POST /groups" should {
    "return OK when a new group is created" in {
      createMember("Shinichi", "Katayama", "s@test.com")
      createMember("Kenji", "Nakamura", "k@test.com")

      val json = Json.parse(
        """
          |{
          |  "group-name": "clj-nakano",
          |  "admin-member-ids": [
          |    1, 2
          |  ]
          |}
        """.stripMargin)
      val Some(result) = route(app, FakeRequest(POST, "/groups").withJsonBody(json))

      status(result) mustEqual OK
    }
  }


  def createMember(firstName: String, lastName: String, email: String): Unit = {
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
  }
}
