import java.io.Closeable
import javax.sql.DataSource

import com.softwaremill.macwire._
import controllers.{GroupController, MeetupController, MemberController, VenueController}
import db.DbContext
import io.getquill.{PostgresJdbcContext, SnakeCase}
import models.{Groups, GroupsMembers, Meetups, MeetupsMembers, Members, Venues}
import play.api.db.{DBComponents, HikariCPComponents}
import play.api.mvc.ControllerComponents

trait AppModule extends DBComponents with HikariCPComponents {

  //  todo: find a better way to remove this
  class A()

  object DB {
    def create(a: A): DbContext = new PostgresJdbcContext(SnakeCase, dbApi.database("default").dataSource.asInstanceOf[DataSource with Closeable])
  }

  lazy val a = wire[A]
  lazy val context = wireWith(DB.create _)
  lazy val groups = wire[Groups]
  lazy val groupsMembers = wire[GroupsMembers]
  lazy val meetups = wire[Meetups]
  lazy val meetupsMembers = wire[MeetupsMembers]
  lazy val members = wire[Members]
  lazy val venues = wire[Venues]
  lazy val groupController = wire[GroupController]
  lazy val meetupController = wire[MeetupController]
  lazy val memberController = wire[MemberController]
  lazy val venueController = wire[VenueController]

  def controllerComponents: ControllerComponents
}
