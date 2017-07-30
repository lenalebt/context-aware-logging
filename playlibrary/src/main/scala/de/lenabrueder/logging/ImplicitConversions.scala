package de.lenabrueder.logging

import play.api.mvc.RequestHeader

import scala.language.implicitConversions

object ImplicitConversions {
  implicit def requestHeader2Context(rh: RequestHeader): Context = rh.attrs.get(TraceIdFilter.RequestContext) match {
    case Some(context) => context
    case None =>
      new Context {
        override lazy val traceId: String = rh.headers
          .get(TraceIdFilter.traceId)
          .map(DefaultTraceIdGenerator.extend) getOrElse DefaultTraceIdGenerator.generate
      }
  }
}
