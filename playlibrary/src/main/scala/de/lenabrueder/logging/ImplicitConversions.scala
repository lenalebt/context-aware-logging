package de.lenabrueder.logging

import play.api.mvc.RequestHeader

import scala.language.implicitConversions

object ImplicitConversions {
  implicit def requestHeader2Context(rh: RequestHeader): Context = rh.attrs.get(LoggingFilter.RequestContext) match {
    case Some(context) => context
    case None =>
      new DefaultContextSettings {
        override lazy val traceId: String = rh.headers
          .get(LoggingFilter.traceId)
          .map(DefaultTraceIdGenerator.extend) getOrElse DefaultTraceIdGenerator.generate
      }
  }
}
