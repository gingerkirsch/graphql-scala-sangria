package com.howtographql.scala.sangria.models

import sangria.execution.FieldTag

case class AuthProviderEmail(email: String, password: String)

case class AuthProviderSignupData(email: AuthProviderEmail)
case object Authorized extends FieldTag