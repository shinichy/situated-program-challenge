package models

import db.DbContext

case class MeetupsMember(meetupId: Int, memberId: Int)

class MeetupsMembers(val ctx: DbContext) {
  import ctx._

  val meetupsMembers = quote(querySchema[MeetupsMember]("meetups_members"))
  val members = quote(querySchema[Member]("members"))

  def create(eventId: Int, memberId: Int) = {
    run(meetupsMembers.insert(lift(MeetupsMember(eventId, memberId))))
  }

  def find(eventId: Int) = {
    val q = quote {
      for {
        mm <- meetupsMembers if mm.meetupId == lift(eventId)
        m <- members if m.id == mm.memberId
      } yield m
    }

    run(q)
  }
}
