package de.lenabrueder.logging

import de.lenabrueder.logging.Logger.ContextFormatter
import org.slf4j.{LoggerFactory, Logger => Underlying}

object Logger {

  /**
    * Create a [[Logger]] wrapping the given underlying `org.slf4j.Logger`.
    */
  def apply(underlying: Underlying): Logger =
    new Logger(underlying)

  /**
    * Create a [[Logger]] for the given name.
    * Example:
    * {{{
    *   val logger = Logger("application")
    * }}}
    */
  def apply(name: String): Logger =
    new Logger(LoggerFactory.getLogger(name))

  /**
    * Create a [[Logger]] wrapping the created underlying `org.slf4j.Logger`.
    */
  def apply(clazz: Class[_]): Logger =
    new Logger(LoggerFactory.getLogger(clazz.getName))

  /**create a logger by using compile-time information of the surrounding class*/
  def apply()(implicit sc: sourcecode.FullName): Logger =
    new Logger(LoggerFactory.getLogger(sc.value))

  type ContextFormatter = (String, Context) => String

  def defaultContextFormatter(message: String, context: Context): String = {
    val formattedContext = (for { (key, value) <- context.toMap.toList.sortBy(_._1) } yield {
      s"$key=$value"
    }).mkString(", ")
    s"$message $formattedContext"
  }

  def extendedContextFormatter(message: String, context: Context): String = {
    val formattedContext = (for {
      (key, value) <- context.toMap.toList.sortBy(_._1) if !Seq("elapsed", "flowId").contains(key)
    } yield {
      s"$key=$value"
    }).mkString(", ") + s" ${context.flowId}/${context.elapsed.toMillis}ms"
    s"$message $formattedContext"
  }
}

/**a logger implementation that forwards to slf4j. More levels will follow when scalafmt macros work with 2.12.*/
final class Logger private (val underlying: Underlying, formatter: ContextFormatter = Logger.extendedContextFormatter)
    extends Serializable {
  def info(message: => String)(implicit context: Context): Unit = {
    if (underlying.isInfoEnabled) {
      underlying.info(formatter(message, context))
    }
  }

  def info(message: => String, cause: => Throwable)(implicit context: Context): Unit = {
    if (underlying.isInfoEnabled) {
      underlying.info(formatter(message, context), cause)
    }
  }

  def error(message: => String)(implicit context: Context): Unit = {
    if (underlying.isErrorEnabled) {
      underlying.error(formatter(message, context))
    }
  }

  def error(message: => String, cause: => Throwable)(implicit context: Context): Unit = {
    if (underlying.isErrorEnabled) {
      underlying.error(formatter(message, context), cause)
    }
  }

  def debug(message: => String)(implicit context: Context): Unit = {
    if (underlying.isDebugEnabled) {
      underlying.debug(formatter(message, context))
    }
  }

  def debug(message: => String, cause: => Throwable)(implicit context: Context): Unit = {
    if (underlying.isDebugEnabled) {
      underlying.debug(formatter(message, context), cause)
    }
  }
}
