package de.lenabrueder.logging
import javax.inject.Inject

import akka.stream.Materializer
import play.api.mvc._
import play.api.{Logger => PlayLogger}
import play.mvc.Http.HeaderNames._

import scala.collection.immutable.TreeSet
import scala.concurrent.{ExecutionContext, Future}

/**Configuration for logging*/
case class LoggingFilterConfiguration(
    logIncomingHeaders: Boolean,
    logOutgoingHeaders: Boolean
) {
  def this() = this(true, true)
}

/** logs incoming requests, and their HTTP response code when they finish.
  *
  * The filter will also log response times and a trace id when the filter is placed *after* the TraceIdFilter.
  * Time measurement starts in the TraceIdFilter in any case. Can be configured to log headers, too.
  */
class LoggingFilter @Inject()(implicit val mat: Materializer,
                              ec: ExecutionContext,
                              configuration: LoggingFilterConfiguration)
    extends Filter {
  import configuration._

  val log = Logger(this.getClass)
  val playLogger = PlayLogger(this.getClass)

  /** headers that will be displayed with some hints about the content only instead of the whole thing because they may
    * not be logged (e.g. Authorization)*/
  val filteredHeaders: Set[String] =
    TreeSet(AUTHORIZATION, "X-Forward-Authorization")(Ordering.comparatorToOrdering(String.CASE_INSENSITIVE_ORDER))

  def filterHeader(header: (String, String)): (String, String) = {
    val (key, value) = header
    if (filteredHeaders.contains(key)) {
      key -> filterHeaderValue(value)
    } else {
      key -> value
    }
  }

  def filterHeaderValue(value: String): String = List.fill(value.length)("X").mkString

  def doLog(message: String)(implicit optContext: Option[Context]) = optContext match {
    case Some(context) => log.info(message)(context)
    case None          => playLogger.info(message)
  }

  def apply(nextFilter: RequestHeader => Future[Result])(rh: RequestHeader): Future[Result] = {
    implicit val optContext = rh.attrs.get(TraceIdFilter.RequestContext)

    if (logIncomingHeaders) {
      doLog(s"${rh.method} ${rh.uri} with headers ${rh.headers.toSimpleMap.map(filterHeader).mkString(", ")}")
    } else {
      doLog(s"${rh.method} ${rh.uri}")
    }
    nextFilter(rh).map { result =>
      if (logOutgoingHeaders) {
        doLog(
          s"${rh.method} ${rh.uri} returned ${result.header.status} with headers ${rh.headers.toSimpleMap.map(filterHeader).mkString(", ")}")
      } else {
        doLog(s"${rh.method} ${rh.uri} returned ${result.header.status}")
      }
      result
    }
  }
}
