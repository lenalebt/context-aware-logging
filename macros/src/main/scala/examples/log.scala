package examples

import scala.collection.parallel.immutable
import scala.meta._

class log extends scala.annotation.StaticAnnotation {
  inline def apply(defn: Any): Any = meta {
    val q"object $funcName" = defn

    val isStrEnabled = s"is${funcName.value.capitalize}Enabled"
    val logWithCause = q"def apply(message : => String, cause: => Throwable)(implicit context:Context): Unit = if (underlying.${Term.Name(isStrEnabled)}) underlying.$funcName(formatter(message, context), cause)"
    val logWithoutCause = q"def apply(message : => String)(implicit context:Context): Unit = if (underlying.${Term.Name(isStrEnabled)}) underlying.$funcName(formatter(message, context))"


    q"""object $funcName {
       $logWithCause
       $logWithoutCause
    }"""
  }
}
