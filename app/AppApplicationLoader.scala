import com.softwaremill.macwire._
import play.api.ApplicationLoader.Context
import play.api._
import play.api.routing.Router
import play.filters.HttpFiltersComponents
import router.Routes

class AppApplicationLoader extends ApplicationLoader {
  def load(context: Context) = new AppComponents(context).application
}

class AppComponents(context: Context) extends BuiltInComponentsFromContext(context)
  with MeetupModule
  with HttpFiltersComponents {

  LoggerConfigurator(context.environment.classLoader).foreach {
    _.configure(context.environment)
  }

  lazy val router: Router = wire[Routes]
}
