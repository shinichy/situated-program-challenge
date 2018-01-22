package controllers

import io.circe.generic.auto._
import io.circe.syntax._
import json.VenueResponse
import models.{Venue, Venues}
import play.api.libs.circe.Circe
import play.api.mvc._

class VenueController(cc: ControllerComponents,
                      venueService: Venues) extends AbstractController(cc) with Circe {

  def findAll(groupId: Int) = Action {
    Ok(venueService.findAllPhysical(groupId).map(VenueResponse(_)).asJson)
  }

  def create(groupId: Int) = Action(circe.json[Venue]) { request =>
    val venue: Venue = request.body.copy(groupId = groupId)
    val id = venueService.create(venue)
    Ok(VenueResponse(id, venue.name, venue.address.get).asJson)
  }
}
