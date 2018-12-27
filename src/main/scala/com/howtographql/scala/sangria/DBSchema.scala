package com.howtographql.scala.sangria

import java.sql.Timestamp

import akka.http.scaladsl.model.DateTime
import com.howtographql.scala.sangria.models.{Link, User, Vote}
import slick.jdbc.H2Profile.api._

import scala.concurrent.duration._
import scala.concurrent.Await
import scala.language.postfixOps


object DBSchema {
  implicit val dateTimeColumnType = MappedColumnType.base[DateTime, Timestamp](
    dt => new Timestamp(dt.clicks),
    ts => DateTime(ts.getTime)
  )

  //1
  class LinksTable(tag: Tag) extends Table[Link](tag, "LINKS"){

    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def url = column[String]("URL")
    def description = column[String]("DESCRIPTION")
    def postedBy = column[Int]("USER_ID")
    def createdAt = column[DateTime]("CREATED_AT")

    def postedByFK = foreignKey("postedBy_FK", postedBy, Users)(_.id)

    def * = (id, url, description, postedBy, createdAt).mapTo[Link]

  }

  class UserTable(tag: Tag) extends Table[User](tag, "USERS"){
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("NAME")
    def email = column[String]("EMAIL")
    def password = column[String]("PASSWORD")
    def createdAt = column[DateTime]("CREATED_AT")

    def * = (id, name, email, password, createdAt).mapTo[User]
  }

  class VoteTable(tag: Tag) extends Table[Vote](tag, "VOTES"){
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def userId = column[Int]("USER_ID")
    def linkId = column[Int]("LINK_ID")
    def createdAt= column[DateTime]("CREATED_AT")

    def userFK = foreignKey("user_FK", userId, Users)(_.id)
    def linkFK = foreignKey("link_FK", linkId, Links)(_.id)

    def * = (id, userId, linkId, createdAt).mapTo[Vote]
  }

  //2
  val Links = TableQuery[LinksTable]
  val Users = TableQuery[UserTable]
  val Votes = TableQuery[VoteTable]

  //3
  /**
   * Load schema and populate sample data withing this Sequence od DBActions
   */
  val databaseSetup = DBIO.seq(
    Users.schema.create,
    Links.schema.create,
    Votes.schema.create,

    Users forceInsertAll Seq(
      User(1, "mario", "mario@example.com", "s3cr3t"),
      User(2, "Fred", "fred@flinstones.com", "wilmalove")
    ),

    Links forceInsertAll Seq(
      Link(1, "http://howtographql.com", "Awesome community driven GraphQL tutorial",1, DateTime(2017,9,12)),
      Link(2, "http://graphql.org", "Official GraphQL web page",1, DateTime(2017,10,1)),
      Link(3, "https://facebook.github.io/graphql/", "GraphQL specification",2, DateTime(2017,10,2))
    ),

    Votes forceInsertAll Seq(
      Vote(1, 1, 1),
      Vote(2, 1, 2),
      Vote(3, 1, 3),
      Vote(4, 2, 2),
    )
  )

  def createDatabase: DAO = {
    val db = Database.forConfig("h2mem")

    Await.result(db.run(databaseSetup), 10 seconds)

    new DAO(db)

  }
}
