package com.howtographql.scala.sangria.models
import akka.http.scaladsl.model.DateTime

//replace current case class
case class Link(id: Int, url: String, description: String, postedBy: Int, createdAt: DateTime = DateTime.now) extends Identifiable
