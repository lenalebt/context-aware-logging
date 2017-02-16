package de.lenabrueder.logging

import de.lenabrueder.logging._

object Main {
  val log = Logger("my-special-logger")

  case class MyRequestContext(headers: Seq[(String, String)]) extends DefaultContextSettings {
    override def toMap: Map[String, String] =
      super.toMap.updated("headers", headers.map { case (k, v) => s"$k=$v" }.mkString(","))
  }

  def main(args: Array[String]): Unit = {
    implicit val context: Context = MyRequestContext(Seq("Host" -> "example.com", "Content-Length" -> "123"))
    log.info("this is just a test")
  }
}
