logLevel := Level.Warn

addSbtPlugin("com.geirsson" %% "sbt-scalafmt" % "0.5.6")

//tut lib
addSbtPlugin("org.tpolecat" % "tut-plugin" % "0.4.8")

//for publishing
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "1.1")
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")
