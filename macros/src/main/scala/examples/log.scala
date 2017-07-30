package examples

import scala.collection.parallel.immutable
import scala.meta._

class log extends scala.annotation.StaticAnnotation {
  inline def apply(defn: Any): Any = meta {
    val q"object $funcName" = defn

    val isStrEnabled = s"is${funcName.value.capitalize}Enabled"
    val logWithoutCause = q"def apply(message : => String)(implicit context:Context): Unit = if (underlying.${Term.Name(isStrEnabled)}) underlying.$funcName(formatter(message, context))"
    val logWithMarkerButWithoutCause = q"def apply(marker: org.slf4j.Marker, message : => String)(implicit context:Context): Unit = if (underlying.${Term.Name(isStrEnabled)}) underlying.$funcName(marker, formatter(message, context))"
    val logWithCause = q"def apply(message : => String, cause: => Throwable)(implicit context:Context): Unit = if (underlying.${Term.Name(isStrEnabled)}) underlying.$funcName(formatter(message, context), cause)"
    val logWithMarkerAndCause = q"def apply(marker: org.slf4j.Marker, message : => String, cause: => Throwable)(implicit context:Context): Unit = if (underlying.${Term.Name(isStrEnabled)}) underlying.$funcName(marker, formatter(message, context), cause)"
    val withReturnLog = q"def returning[A](body: => A)(message : A => String)(implicit context:Context): A = {val result = body;info(message(result));result}"


    q"""object $funcName {
       $logWithoutCause
       $logWithMarkerButWithoutCause
       $logWithCause
       $logWithMarkerAndCause
       $withReturnLog
    }"""
  }
}
