package de.lenabrueder.logging

import java.time.LocalDateTime

/**a context that can be used for logging, which can contain various extra info*/
trait Context {

  /** The ID of the flow that triggered this log entry.
    * Should be globally unique for something you consider to be a "flow"
    */
  def traceId: String
  def extTraceId: String

  /**additional tracing headers that are related, but not equal to the trace id header*/
  def additionalTraceHeaders: Map[String, String]

  /**contains all extra info that should potentially end up in log entries*/
  def toMap: Map[String, String] =
    Map("traceId" -> traceId, "extTraceId" -> extTraceId, "elapsed" -> elapsed.toMillis.toString) ++ additionalTraceHeaders

  /**captures when the flow has been started/initialized*/
  val startTime: LocalDateTime = LocalDateTime.now

  /**captures how long the flow has been running so far*/
  def elapsed: java.time.Duration = java.time.Duration.between(startTime, LocalDateTime.now)
}

trait DefaultContextSettings extends Context {
  override lazy val traceId: String = DefaultTraceIdGenerator.generate
  override lazy val extTraceId: String = traceId
  override lazy val additionalTraceHeaders: Map[String, String] = Map.empty
}

object Context {

  /**used for cases where you do not have a context at hand (e.g. for testing)*/
  object NoContextAvailable extends DefaultContextSettings
}

/**default implementation for Jobs that can have a name*/
case class JobContext(name: String) extends DefaultContextSettings {
  override def toMap: Map[String, String] = super.toMap.updated("name", name)
}
