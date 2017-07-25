package de.lenabrueder.logging

import spec.UnitSpec

class LoggerTest extends UnitSpec {
  "Logger" should "generate the correct name" in {
    val logger = Logger()
    logger.trace("whoops")
    logger.debug("whoops")
    logger.info("this is a test")
    logger.warn("whoops")
    logger.error("whoops")
  }
}
