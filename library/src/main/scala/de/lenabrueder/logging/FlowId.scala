package de.lenabrueder.logging

import java.util.UUID

import scala.util.Random

trait FlowIdGenerator[A] {

  /**generate a new flow id from scratch*/
  def generate: A

  /**extend a flow id that has been received somehow, to further specify a new logical start of a flow*/
  def extend(other: A): A
}

object DefaultFlowIdGenerator extends FlowIdGenerator[String] {

  /** generate a new flow id from scratch */
  override def generate: String = UUID.randomUUID().toString

  /** extend a flow id that has been received somehow, to further specify a new logical start of a flow */
  override def extend(other: String): String = s"$other#${Random.nextInt(999)}"
}
