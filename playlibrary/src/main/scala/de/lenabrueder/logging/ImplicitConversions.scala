package de.lenabrueder.logging

import play.api.libs.ws.WSRequest
import play.api.mvc.RequestHeader

import scala.language.implicitConversions

object ImplicitConversions {
  implicit class UpdateIfNotExists[K, V](val map: Map[K, V]) {
    def updatedIfNotExists(key: K, value: V): Map[K, V] =
      if (!map.contains(key)) {
        map.updated(key, value)
      } else {
        map
      }
  }

  implicit def requestHeader2Context(rh: RequestHeader): Context =
    rh.attrs.get(TraceIdFilter.RequestContext) match {
      case Some(context) => context
      case None =>
        new Context {
          override lazy val traceId: String = rh.headers
            .get(TraceIdFilter.traceId)
            .getOrElse(DefaultTraceIdGenerator.generate)
          override lazy val extTraceId: String = rh.headers
            .get(TraceIdFilter.extTraceId)
            .map(DefaultTraceIdGenerator.extend)
            .getOrElse(traceId)

          override lazy val additionalTraceHeaders: Map[String, String] =
            rh.headers.toSimpleMap
              .filterKeys(TraceIdFilter.additionalTraceHeaders.contains)
              .updatedIfNotExists(TraceIdFilter.traceId, traceId)
              .updated(TraceIdFilter.extTraceId, extTraceId)
        }
    }

  implicit class WSRequestTracer(val req: WSRequest) {
    def addTraceHeaders(implicit context: Context): WSRequest =
      req.addHttpHeaders(context.additionalTraceHeaders.toList: _*)
  }
}
