import com.softwaremill.macwire._
import controllers.MeetupController
import play.api.mvc.ControllerComponents

trait MeetupModule {
  lazy val meetupController = wire[MeetupController]

  def controllerComponents: ControllerComponents
}
