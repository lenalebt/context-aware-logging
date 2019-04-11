import sbt.Keys.{developers, libraryDependencies, publishMavenStyle}
import sbt.url

organization in ThisBuild := "de.lenabrueder"

scalaVersion in ThisBuild := "2.12.8"

crossScalaVersions := Seq(scalaVersion.value, "2.11.12")

developers in ThisBuild := List(
  Developer(
    id = "lbrueder",
    name = "Lena Brueder",
    email = "oss@lena-brueder.de",
    url = url("http://github.com/lenalebt")
  )
)

scmInfo in ThisBuild := Some(
  ScmInfo(
    url("https://github.com/lenalebt/context-aware-logging"),
    "scm:git@github.com:lenalebt/context-aware-logging.git"
  )
)

homepage in ThisBuild := Some(url("https://github.com/lenalebt/context-aware-logging"))

licenses in ThisBuild += ("MIT", url("https://opensource.org/licenses/MIT"))

publishMavenStyle in ThisBuild := true
publishTo in ThisBuild := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

useGpg := true

val libVersion = "0.5-SNAPSHOT"
/**version of our play library, not the play version we use ourselves*/
val playLibVersion = "2.7.0-SNAPSHOT"
/**scala play library we are using*/
val scalaPlayLibraryVersion = "2.7.0"

// Use macros in this project.
lazy val library = project
  .settings(
    Seq(
      name := "context-aware-logging",
      version := libVersion,
      scalacOptions ++= List(
        "-unchecked",
        "-deprecation",
        "-language:_",
        "-encoding",
        "UTF-8"
      ),
      //Library dependencies
      libraryDependencies ++= Seq(
        "com.lihaoyi" %% "sourcecode" % "0.1.6",
        "org.slf4j" % "slf4j-api" % "1.7.25"
      ),
      // Test dependencies
      libraryDependencies ++= Seq(
        "org.scalatest" %% "scalatest" % "3.0.5",
        "com.github.alexarchambault" %% "scalacheck-shapeless_1.13" % "1.1.3",
        "org.scalacheck" %% "scalacheck" % "1.14.0",
        "ch.qos.logback" % "logback-classic" % "1.2.3"
      ).map(_ % "test"),
      //pom extra info
      publishArtifact in Test := false
    )
  )

lazy val playlibrary = project
  .settings(
    Seq(
      name := "context-aware-logging-play",
      version := playLibVersion,
      scalacOptions ++= List(
        "-unchecked",
        "-deprecation",
        "-language:_",
        "-encoding",
        "UTF-8"
      ),
      //Library dependencies
      libraryDependencies ++= Seq(
        "com.typesafe.play" %% "play" % scalaPlayLibraryVersion % "provided",
        "com.typesafe.play" %% "play-ahc-ws" % scalaPlayLibraryVersion % "provided",
        "de.lenabrueder" %% "context-aware-logging" % libVersion
      ),
      // Test dependencies
      libraryDependencies ++= Seq(
        "org.scalatest" %% "scalatest" % "3.0.5",
        "com.github.alexarchambault" %% "scalacheck-shapeless_1.13" % "1.1.3",
        "org.scalacheck" %% "scalacheck" % "1.14.0",
        "ch.qos.logback" % "logback-classic" % "1.2.3"
      ).map(_ % "test"),
      //pom extra info
      publishMavenStyle := true
    ))
  .dependsOn(library)

enablePlugins(TutPlugin)
