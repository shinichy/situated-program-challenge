import com.softwaremill.macwire._
import controllers.{MeetupController, MemberController}
import play.api.mvc.ControllerComponents

trait AppModule {
  lazy val meetupController = wire[MeetupController]
  lazy val memberController = wire[MemberController]

  def controllerComponents: ControllerComponents
}
