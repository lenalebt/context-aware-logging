package de.lenabrueder.logging

import javax.inject.Inject

import akka.util.ByteString
import de.lenabrueder.logging.ImplicitConversions._
import play.api.libs.streams.Accumulator
import play.api.libs.typedmap.TypedKey
import play.api.mvc.{EssentialAction, EssentialFilter, RequestHeader, Result}

import scala.concurrent.ExecutionContext

object TraceIdFilter {
  val RequestContext: TypedKey[Context] = TypedKey.apply[Context]("request-context")

  /**the headers zipkin uses. forwarding them in an istio-based network will give you tracing via zipkin automatically.*/
  final val additionalTraceHeaders = List("x-trace-id",
                                          "x-b3-traceid",
                                          "x-request-id",
                                          "x-b3-spanid",
                                          "x-b3-parentspanid",
                                          "x-b3-sampled",
                                          "x-b3-flags",
                                          "x-ot-span-context")
  final val traceId = "x-b3-traceid"
  final val extTraceId = "X-Trace-ID"
}

/**Filter that adds the context to the attrs, as well as adding a trace id to outgoing responses*/
class TraceIdFilter @Inject()(implicit ec: ExecutionContext) extends EssentialFilter {
  val log = Logger()

  override def apply(next: EssentialAction): EssentialAction = new EssentialAction {
    override def apply(rh: RequestHeader): Accumulator[ByteString, Result] = {
      implicit val context: Context = rh

      val updatedRh = rh
        .withAttrs(rh.attrs.updated(TraceIdFilter.RequestContext, context))
      val accumulator: Accumulator[ByteString, Result] = next(updatedRh)

      for { result <- accumulator } yield {
        result.withHeaders(context.additionalTraceHeaders.toList: _*)
      }
    }
  }
}
