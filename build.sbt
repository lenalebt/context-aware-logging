name := "context-aware-logging"
organization := "de.lenabrueder"

version := "0.1"

scalaVersion := "2.12.1"

crossScalaVersions := Seq(scalaVersion.value, "2.11.8")

scalacOptions ++= List(
  "-unchecked",
  "-deprecation",
  "-language:_",
  "-encoding",
  "UTF-8"
)

//Library dependencies
libraryDependencies ++= Seq(
  "com.lihaoyi" %% "sourcecode" % "0.1.3",
  "org.slf4j" % "slf4j-api" % "1.7.23",
  //for testing
  "ch.qos.logback" % "logback-classic" % "1.2.1"
)

// Test dependencies
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.1",
  "com.github.alexarchambault" %% "scalacheck-shapeless_1.13" % "1.1.3",
  "org.scalacheck" %% "scalacheck" % "1.13.4",
  "ch.qos.logback" % "logback-classic" % "1.2.1"
).map(_ % "test")

//pom extra info
publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomExtra := (<scm>
  <url>git@github.com:lenalebt/context-aware-logging.git</url>
  <developerConnection>scm:git:git@github.com:lenalebt/context-aware-logging.git</developerConnection>
  <connection>scm:git:https://github.com/lenalebt/context-aware-logging</connection>
</scm>
  <developers>
    <developer>
      <name>Lena Brueder</name>
      <email>oss@lena-brueder.de</email>
      <url>https://github.com/lenalebt</url>
    </developer>
  </developers>)

homepage := Some(url("https://github.com/lenalebt/context-aware-logging"))

//settings to compile readme
tutSettings
tutSourceDirectory := baseDirectory.value / "tut"
tutTargetDirectory := baseDirectory.value
