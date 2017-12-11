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
      val firstName1 = "Shinichi"
      val lastName1 = "Katayama"
      val email1 = "s@test.com"
      val id1 = createMember(firstName1, lastName1, email1)
      
      val firstName2 = "Kenji"
      val lastName2 = "Nakamura"
      val email2 = "k@test.com"
      val id2 = createMember(firstName2, lastName2, email2)

      val groupName = "clj-nakano"
      val json = Json.parse(
        s"""
           |{
           |  "group-name": "$groupName",
           |  "admin-member-ids": [
           |    $id1, $id2
           |  ]
           |}
        """.stripMargin)
      val Some(result) = route(app, FakeRequest(POST, "/groups").withJsonBody(json))
      val responseJson = contentAsJson(result)
      (responseJson \ "group-id").as[Int] must be > 0
      (responseJson \ "group-name").as[String] mustEqual groupName
      val admin1 = (responseJson \ "admin")(0)
      (admin1 \ "member-id").as[Int] mustEqual id1
      (admin1 \ "first-name").as[String] mustEqual firstName1
      (admin1 \ "last-name").as[String] mustEqual lastName1
      (admin1 \ "email").as[String] mustEqual email1
      
      val admin2 = (responseJson \ "admin")(1)
      (admin2 \ "member-id").as[Int] mustEqual id2
      (admin2 \ "first-name").as[String] mustEqual firstName2
      (admin2 \ "last-name").as[String] mustEqual lastName2
      (admin2 \ "email").as[String] mustEqual email2
    }
  }

  "POST /members/{member-id}/groups/{group-id}" should {
    "return OK" in {
      val memberId = createMember("Shinichi", "Katayama", "s@test.com")
      val memberId2 = createMember("Kenji", "Nakamura", "k@test.com")
      val groupId = createGroup("clj-nakano", memberId)

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

  "GET /groups" should {
    "return OK" in {
      val firstName1 = "Shinichi"
      val lastName1 = "Katayama"
      val email1 = "s@test.com"
      val id1 = createMember(firstName1, lastName1, email1)

      val firstName2 = "Kenji"
      val lastName2 = "Nakamura"
      val email2 = "k@test.com"
      val id2 = createMember(firstName2, lastName2, email2)

      val groupName1 = "group1"
      val groupName2 = "group2"
      val groupId1 = createGroup(groupName1, id1)
      val groupId2 = createGroup(groupName2, id2)
      joinGroup(groupId1, id2)

      val Some(result) = route(app, FakeRequest(GET, "/groups"))
      val responseJson = contentAsJson(result)
      val group1 = responseJson(0)
      (group1 \ "group-id").as[Int] mustEqual groupId1
      (group1 \ "group-name").as[String] mustEqual groupName1
      val admin1 = (group1 \ "admin")(0)
      (admin1 \ "member-id").as[Int] mustEqual id1
      (admin1 \ "first-name").as[String] mustEqual firstName1
      (admin1 \ "last-name").as[String] mustEqual lastName1
      (admin1 \ "email").as[String] mustEqual email1
      
      val member1 = (group1 \ "members")(0)
      (member1 \ "member-id").as[Int] mustEqual id2
      (member1 \ "first-name").as[String] mustEqual firstName2
      (member1 \ "last-name").as[String] mustEqual lastName2
      (member1 \ "email").as[String] mustEqual email2

      val group2 = responseJson(1)
      (group2 \ "group-id").as[Int] mustEqual groupId2
      (group2 \ "group-name").as[String] mustEqual groupName2
      val admin2 = (group2 \ "admin")(0)
      (admin2 \ "member-id").as[Int] mustEqual id2
      (admin2 \ "first-name").as[String] mustEqual firstName2
      (admin2 \ "last-name").as[String] mustEqual lastName2
      (admin2 \ "email").as[String] mustEqual email2
    }
  }
}
