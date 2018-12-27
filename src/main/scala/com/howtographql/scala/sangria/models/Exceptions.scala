package com.howtographql.scala.sangria.models

import sangria.validation.Violation

case object DateTimeCoerceViolation extends Violation {
  override def errorMessage: String = "Error during parsing DateTime"
}

case class AuthenticationException(message: String) extends Exception(message)
case class AuthorizationException(message: String) extends Exception(message)