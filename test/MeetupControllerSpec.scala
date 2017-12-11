import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.{BaseOneAppPerTest, PlaySpec}
import play.api.libs.json.{JsArray, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._

class MeetupControllerSpec extends PlaySpec
  with BaseOneAppPerTest
  with AppApplicationFactory
  with BeforeAndAfterEach
  with JdbcContext
  with Util {

  import ctx._

  override def beforeEach() {
    val q = quote {
      infix"TRUNCATE groups, groups_members, members, venues, meetups, meetups_members".as[Action[Int]]
    }
    ctx.run(q)
  }

  "GET /groups/{group-id}/meetups" should {
    "return OK" in {
      val memberId = createMember("Shinichi", "Katayama", "s@test.com")
      val groupId = createGroup("clj-nakano", memberId)
      val venueId = createVenue(groupId, "ICTCO", "164-0001", "Tokyo", "Nakano", "中野4丁目", "10-1")
      createMeetup(groupId, "Situated Progarm Challenge", "2017-12-04T07:31:34.248Z", "2017-12-04T09:31:34.248Z", venueId)
      createMeetup(groupId, "Situated Progarm Challenge2", "2017-12-04T07:31:34.248Z", "2017-12-04T09:31:34.248Z", venueId)

      val Some(result) = route(app, FakeRequest(GET, s"/groups/$groupId/meetups"))
      status(result) mustEqual OK
      val responseJson = contentAsJson(result)
      responseJson.as[JsArray].value.size mustEqual 2

      (responseJson(0) \ "group-id").isEmpty mustEqual true
    }
  }

  "GET /groups/{group-id}/meetups/{event-id}" should {
    "return OK" in {
      val memberId = createMember("Shinichi", "Katayama", "s@test.com")
      val groupId = createGroup("clj-nakano", memberId)
      val venueId = createVenue(groupId, "ICTCO", "164-0001", "Tokyo", "Nakano", "中野4丁目", "10-1")

      val title = "Situated Progarm Challenge"
      val startAt = "2017-12-04T07:31:34.248Z"
      val endAt = "2017-12-04T09:31:34.248Z"
      val eventId = createMeetup(groupId, title, startAt, endAt, venueId)

      val Some(result) = route(app, FakeRequest(GET, s"/groups/$groupId/meetups/$eventId"))
      status(result) mustEqual OK
      val responseJson = contentAsJson(result)
      (responseJson \ "event-id").as[Int] mustEqual eventId
      (responseJson \ "group-id").isEmpty mustEqual true
      (responseJson \ "title").as[String] mustEqual title
      (responseJson \ "start-at").as[String] mustEqual startAt
      (responseJson \ "end-at").as[String] mustEqual endAt
      (responseJson \ "venue" \ "venue-id").as[Int] mustEqual venueId
      (responseJson \ "venue" \ "group-id").isEmpty mustEqual true
      (responseJson \ "members").as[JsArray].value.isEmpty mustEqual true
    }
  }

  "POST /groups/{group-id}/meetups" should {
    "return OK" in {
      val memberId = createMember("Shinichi", "Katayama", "s@test.com")
      val groupId = createGroup("clj-nakano", memberId)
      val venueId = createVenue(groupId, "ICTCO", "164-0001", "Tokyo", "Nakano", "中野4丁目", "10-1")

      val title = "Situated Progarm Challenge"
      val startAt = "2017-12-04T07:31:34.248Z"
      val endAt = "2017-12-04T09:31:34.248Z"

      val json = Json.parse(
        s"""
           |{
           |  "title": "$title",
           |  "start-at": "$startAt",
           |  "end-at": "$endAt",
           |  "venue-id": $venueId
           |}
      """.stripMargin)
      val Some(result) = route(app, FakeRequest(POST, s"/groups/$groupId/meetups").withJsonBody(json))
      status(result) mustEqual OK
      val responseJson = contentAsJson(result)
      (responseJson \ "event-id").as[Int] must be > 0
      (responseJson \ "group-id").isEmpty mustEqual true
      (responseJson \ "title").as[String] mustEqual title
      (responseJson \ "start-at").as[String] mustEqual startAt
      (responseJson \ "end-at").as[String] mustEqual endAt
      (responseJson \ "venue" \ "venue-id").as[Int] mustEqual venueId
      (responseJson \ "venue" \ "group-id").isEmpty mustEqual true
      (responseJson \ "members").as[JsArray].value.isEmpty mustEqual true
    }
  }

  "POST /members/{member-id}/meetups/{event-id}" should {
    "return OK" in {
      val firstName = "Shinichi"
      val lastName = "Katayama"
      val email = "s@test.com"
      val memberId = createMember(firstName, lastName, email)
      val groupId = createGroup("clj-nakano", memberId)
      val venueId = createVenue(groupId, "ICTCO", "164-0001", "Tokyo", "Nakano", "中野4丁目", "10-1")
      val eventId = createMeetup(groupId, "Situated Progarm Challenge", "2017-12-04T07:31:34.248Z", "2017-12-04T09:31:34.248Z", venueId)

      val Some(result) = route(app, FakeRequest(POST, s"/members/$memberId/meetups/$eventId"))

      status(result) mustEqual OK
      val responseJson = contentAsJson(result)
      val member = (responseJson \ "members")(0)
      (member \ "member-id").as[Int] mustEqual memberId
      (member \ "first-name").as[String] mustEqual firstName
      (member \ "last-name").as[String] mustEqual lastName
      (member \ "email").as[String] mustEqual email
    }
  }
}
