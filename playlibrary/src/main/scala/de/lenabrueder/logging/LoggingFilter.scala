package de.lenabrueder.logging

import javax.inject.Inject

import akka.util.ByteString
import de.lenabrueder.logging.ImplicitConversions._
import play.api.libs.streams.Accumulator
import play.api.libs.typedmap.TypedKey
import play.api.mvc.{EssentialAction, EssentialFilter, RequestHeader, Result}

import scala.concurrent.ExecutionContext

object LoggingFilter {
  val RequestContext: TypedKey[Context] = TypedKey.apply[Context]("request-context")
  final val traceId = "X-Trace-ID"
}

/**Filter that adds a line of logging when a request has finished, as well as adding a trace id to outgoing responses*/
class LoggingFilter @Inject()(implicit ec: ExecutionContext) extends EssentialFilter {
  val log = Logger()

  override def apply(next: EssentialAction): EssentialAction = new EssentialAction {
    override def apply(rh: RequestHeader): Accumulator[ByteString, Result] = {
      implicit val context: Context = rh

      val updatedRh = rh
        .withAttrs(rh.attrs.updated(LoggingFilter.RequestContext, context))
        .withHeaders(rh.headers.add(LoggingFilter.traceId -> context.traceId))
      val accumulator: Accumulator[ByteString, Result] = next(updatedRh)

      for { result <- accumulator } yield {
        log.info(s"${updatedRh.method} ${updatedRh.uri} returned ${result.header.status}")
        result.withHeaders(LoggingFilter.traceId -> context.traceId)
      }
    }
  }
}
