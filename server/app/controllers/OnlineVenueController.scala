package controllers

import io.circe.generic.auto._
import io.circe.syntax._
import json.OnlineVenueResponse
import models.{Venue, VenueType, Venues}
import play.api.libs.circe.Circe
import play.api.mvc._

class OnlineVenueController(cc: ControllerComponents, venueService: Venues) extends AbstractController(cc) with Circe {

  def findAll(groupId: Int) = Action {
    Ok(venueService.findAllOnline(groupId).map(OnlineVenueResponse(_)).asJson)
  }

  def create(groupId: Int) = Action(circe.json[Venue]) { request =>
    val venue: Venue = request.body.copy(groupId = groupId, venueType = VenueType.online)
    val id = venueService.create(venue)
    Ok(OnlineVenueResponse(id, venue.name, venue.url.get).asJson)
  }
}
