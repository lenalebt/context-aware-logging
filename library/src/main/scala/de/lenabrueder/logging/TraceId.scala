package de.lenabrueder.logging

import java.util.UUID

import scala.util.Random

trait TraceIdGenerator[A] {

  /**generate a new trace id from scratch*/
  def generate: A

  /**extend a trace id that has been received somehow, to further specify a new logical start of a trace*/
  def extend(other: A): A
}

object DefaultTraceIdGenerator extends TraceIdGenerator[String] {

  /** generate a new trace id from scratch */
  override def generate: String = UUID.randomUUID().toString

  /** extend a trace id that has been received somehow, to further specify a new logical start of a trace */
  override def extend(other: String): String = s"$other#${Random.nextInt(999)}"
}
