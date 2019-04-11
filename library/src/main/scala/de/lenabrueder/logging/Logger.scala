package de.lenabrueder.logging

import de.lenabrueder.logging.Logger.ContextFormatter
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.slf4j.{Logger => Underlying}

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
    MDC.put("exttraceid", context.extTraceId)
    MDC.put("requesttime", context.elapsed.toMillis.toString)
    for {
      (key, value) <- context.toMap.toList.sortBy(_._1)
      if !Seq("elapsed", "traceId", "extTraceId").contains(key)
    } {
      MDC.put(key, value)
    }
    message
  }
}

/**a logger implementation that forwards to slf4j. More levels will follow when scalafmt macros work with 2.12.*/
final class Logger private (val underlying: Underlying, formatter: ContextFormatter)
    extends Serializable {

  def isWarnEnabled = underlying.isWarnEnabled
  def warn(message: => String)(implicit context: Context): Unit =
    if (underlying.isWarnEnabled()) underlying.warn(formatter(message, context))
  def warn(marker: org.slf4j.Marker, message: => String)(implicit context: Context): Unit =
    if (underlying.isWarnEnabled()) underlying.warn(marker, formatter(message, context))
  def warn(message: => String, cause: => Throwable)(implicit context: Context): Unit =
    if (underlying.isWarnEnabled()) underlying.warn(formatter(message, context), cause)
  def warn(marker: org.slf4j.Marker, message: => String, cause: => Throwable)(
      implicit context: Context
  ): Unit =
    if (underlying.isWarnEnabled()) underlying.warn(marker, formatter(message, context), cause)

  object debug
  def isDebugEnabled = underlying.isDebugEnabled
  def debug(message: => String)(implicit context: Context): Unit =
    if (underlying.isDebugEnabled()) underlying.debug(formatter(message, context))
  def debug(marker: org.slf4j.Marker, message: => String)(implicit context: Context): Unit =
    if (underlying.isDebugEnabled()) underlying.debug(marker, formatter(message, context))
  def debug(message: => String, cause: => Throwable)(implicit context: Context): Unit =
    if (underlying.isDebugEnabled()) underlying.debug(formatter(message, context), cause)
  def debug(marker: org.slf4j.Marker, message: => String, cause: => Throwable)(
      implicit context: Context
  ): Unit =
    if (underlying.isDebugEnabled()) underlying.debug(marker, formatter(message, context), cause)

  object info
  def isInfoEnabled = underlying.isInfoEnabled
  def info(message: => String)(implicit context: Context): Unit =
    if (underlying.isInfoEnabled()) underlying.info(formatter(message, context))
  def info(marker: org.slf4j.Marker, message: => String)(implicit context: Context): Unit =
    if (underlying.isInfoEnabled()) underlying.info(marker, formatter(message, context))
  def info(message: => String, cause: => Throwable)(implicit context: Context): Unit =
    if (underlying.isInfoEnabled()) underlying.info(formatter(message, context), cause)
  def info(marker: org.slf4j.Marker, message: => String, cause: => Throwable)(
      implicit context: Context
  ): Unit =
    if (underlying.isInfoEnabled()) underlying.info(marker, formatter(message, context), cause)

  object error
  def isErrorEnabled = underlying.isErrorEnabled
  def error(message: => String)(implicit context: Context): Unit =
    if (underlying.isErrorEnabled()) underlying.error(formatter(message, context))
  def error(marker: org.slf4j.Marker, message: => String)(implicit context: Context): Unit =
    if (underlying.isErrorEnabled()) underlying.error(marker, formatter(message, context))
  def error(message: => String, cause: => Throwable)(implicit context: Context): Unit =
    if (underlying.isErrorEnabled()) underlying.error(formatter(message, context), cause)
  def error(marker: org.slf4j.Marker, message: => String, cause: => Throwable)(
      implicit context: Context
  ): Unit =
    if (underlying.isErrorEnabled()) underlying.error(marker, formatter(message, context), cause)

  object trace
  def isTraceEnabled = underlying.isTraceEnabled
  def trace(message: => String)(implicit context: Context): Unit =
    if (underlying.isTraceEnabled()) underlying.trace(formatter(message, context))
  def trace(marker: org.slf4j.Marker, message: => String)(implicit context: Context): Unit =
    if (underlying.isTraceEnabled()) underlying.trace(marker, formatter(message, context))
  def trace(message: => String, cause: => Throwable)(implicit context: Context): Unit =
    if (underlying.isTraceEnabled()) underlying.trace(formatter(message, context), cause)
  def trace(marker: org.slf4j.Marker, message: => String, cause: => Throwable)(
      implicit context: Context
  ): Unit =
    if (underlying.isTraceEnabled()) underlying.trace(marker, formatter(message, context), cause)
}
