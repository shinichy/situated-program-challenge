import org.scalatestplus.play.BaseOneAppPerTest
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

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

  def createGroup(name: String, ids: Int*): Int = {
    val idsStr = ids.toSeq.map(_.toString).mkString(",")
    val json = Json.parse(
      s"""
         |{
         |  "group-name": "$name",
         |  "admin-member-ids": [
         |    $idsStr
         |  ]
         |}
        """.stripMargin)
    val Some(result) = route(app, FakeRequest(POST, "/groups").withJsonBody(json))
    val responseJson = contentAsJson(result)
    (responseJson \ "group-id").as[Int]
  }

  def createVenue(groupId: Int, name: String, postalCode: String, prefecture: String, city: String, address1: String, address2: String) = {
    val json = Json.parse(
      s"""
         |{
         |  "venue-name": "$name",
         |  "address": {
         |    "postal-code": "$postalCode",
         |    "prefecture": "$prefecture",
         |    "city": "$city",
         |    "address1": "$address1",
         |    "address2": "$address2"
         |  }
         |}
      """.stripMargin)
    val Some(result) = route(app, FakeRequest(POST, s"/groups/$groupId/venues").withJsonBody(json))
    val responseJson = contentAsJson(result)
    (responseJson \ "venue-id").as[Int]
  }

  def createOnlineVenue(groupId: Int, name: String, url: String) = {
    val json = Json.parse(
      s"""
         |{
         |  "venue-name": "$name",
         |  "url": "$url"
         |}
      """.stripMargin)
    val Some(result) = route(app, FakeRequest(POST, s"/groups/$groupId/online-venues").withJsonBody(json))
    val responseJson = contentAsJson(result)
    (responseJson \ "online-venue-id").as[Int]
  }

  def createMeetup(groupId: Int, title: String, startAt: String, endAt: String, venueId: Int) = {
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
    val responseJson = contentAsJson(result)
    (responseJson \ "event-id").as[Int]
  }

  def joinGroup(groupId: Int, memberId: Int) = {
    val json = Json.parse(
      s"""
         |{
         |  "admin": false
         |}
        """.stripMargin)
    val Some(result) = route(app, FakeRequest(POST, s"/members/$memberId/groups/$groupId").withJsonBody(json))
    Await.ready(result, Duration.Inf)
  }
}
