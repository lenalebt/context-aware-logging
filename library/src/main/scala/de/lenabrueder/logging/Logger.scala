package de.lenabrueder.logging

import de.lenabrueder.logging.Logger.ContextFormatter
import examples.log
import org.slf4j.{LoggerFactory, MDC, Logger => Underlying}

object Logger {

  /**
    * Create a [[Logger]] wrapping the given underlying `org.slf4j.Logger`.
    */
  def apply(underlying: Underlying): Logger = apply(underlying, Logger.extendedContextFormatter _)
  def apply(underlying: Underlying, formatter: ContextFormatter): Logger =
    new Logger(underlying, formatter)

  /**
    * Create a [[Logger]] for the given name.
    * Example:
    * {{{
    *   val logger = Logger("application")
    * }}}
    */
  def apply(name: String): Logger = apply(name, Logger.mdcContextFormatter _)
  def apply(name: String, formatter: ContextFormatter): Logger =
    new Logger(LoggerFactory.getLogger(name), formatter)

  /**
    * Create a [[Logger]] wrapping the created underlying `org.slf4j.Logger`.
    */
  def apply(clazz: Class[_]): Logger = apply(clazz, Logger.mdcContextFormatter _)
  def apply(clazz: Class[_], formatter: ContextFormatter): Logger =
    new Logger(LoggerFactory.getLogger(clazz.getName), formatter)

  /**create a logger by using compile-time information of the surrounding class*/
  def apply()(implicit sc: sourcecode.FullName): Logger = apply(Logger.mdcContextFormatter _)
  def apply(formatter: ContextFormatter)(implicit sc: sourcecode.FullName): Logger =
    new Logger(LoggerFactory.getLogger(sc.value), formatter)

  type ContextFormatter = (String, Context) => String

  def defaultContextFormatter(message: String, context: Context): String = {
    val formattedContext = (for { (key, value) <- context.toMap.toList.sortBy(_._1) } yield {
      s"$key=$value"
    }).mkString(", ")
    s"$message $formattedContext"
  }

  def extendedContextFormatter(message: String, context: Context): String = {
    val formattedContext = (for {
      (key, value) <- context.toMap.toList.sortBy(_._1) if !Seq("elapsed", "traceId").contains(key)
    } yield {
      s"$key=$value"
    }).mkString(", ") + s" ${context.traceId}/${context.elapsed.toMillis}ms"
    s"$message $formattedContext"
  }

  /**puts traceid and requesttime on the MDC before writing the log entry*/
  def mdcContextFormatter(message: String, context: Context): String = {
    MDC.put("traceid", context.traceId)
    MDC.put("requesttime", context.elapsed.toMillis.toString)
    for {
      (key, value) <- context.toMap.toList.sortBy(_._1) if !Seq("elapsed", "traceId").contains(key)
    } {
      MDC.put(key, value)
    }
    message
  }
}

/**a logger implementation that forwards to slf4j. More levels will follow when scalafmt macros work with 2.12.*/
final class Logger private (val underlying: Underlying, formatter: ContextFormatter) extends Serializable {

  @log object warn
  @log object debug
  @log object info
  @log object error
  @log object trace
}
