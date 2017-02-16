package de.lenabrueder.logging

import spec.UnitSpec

class LoggerTest extends UnitSpec {
  "Logger" should "generate the correct name" in {
    val logger = Logger()
    logger.info("this is a test")
  }
}
