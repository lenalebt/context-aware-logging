package de.lenabrueder.logging

import play.api.mvc.RequestHeader

object ImplicitConversions {
  implicit def requestHeader2Context(rh: RequestHeader): Context = new DefaultContextSettings {
    override lazy val flowId: String = rh.headers
      .get("X-Flow-ID")
      .map(DefaultFlowIdGenerator.extend) getOrElse DefaultFlowIdGenerator.generate
  }
}
