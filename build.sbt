import sbt.Keys.{developers, libraryDependencies, publishMavenStyle}
import sbt.url

organization in ThisBuild := "de.lenabrueder"

scalaVersion in ThisBuild := "2.12.2"

crossScalaVersions := Seq(scalaVersion.value, "2.11.11")

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

val libVersion = "0.2-SNAPSHOT"

lazy val metaMacroSettings: Seq[Def.Setting[_]] = Seq(
  // New-style macro annotations are under active development.  As a result, in
  // this build we'll be referring to snapshot versions of both scala.meta and
  // macro paradise.
  resolvers += Resolver.sonatypeRepo("releases"),
  resolvers += Resolver.bintrayRepo("scalameta", "maven"),
  // A dependency on macro paradise 3.x is required to both write and expand
  // new-style macros.  This is similar to how it works for old-style macro
  // annotations and a dependency on macro paradise 2.x.
  addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M9" cross CrossVersion.full),
  scalacOptions += "-Xplugin-require:macroparadise",
  // temporary workaround for https://github.com/scalameta/paradise/issues/10
  scalacOptions in (Compile, console) := Seq() // macroparadise plugin doesn't work in repl yet.
)

// Define macros in this project.
lazy val macros = project.settings(
  metaMacroSettings ++ Seq(
    name := "context-aware-logging-macros"
  ),
  // A dependency on scala.meta is required to write new-style macros, but not
  // to expand such macros.  This is similar to how it works for old-style
  // macros and a dependency on scala.reflect.
  libraryDependencies += "org.scalameta" %% "scalameta" % "1.8.0"
)

// Use macros in this project.
lazy val library = project
  .settings(
    metaMacroSettings ++ Seq(
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
        "com.lihaoyi" %% "sourcecode" % "0.1.4",
        "org.slf4j" % "slf4j-api" % "1.7.25"
      ),
      // Test dependencies
      libraryDependencies ++= Seq(
        "org.scalatest" %% "scalatest" % "3.0.1",
        "com.github.alexarchambault" %% "scalacheck-shapeless_1.13" % "1.1.3",
        "org.scalacheck" %% "scalacheck" % "1.13.4",
        "ch.qos.logback" % "logback-classic" % "1.2.3"
      ).map(_ % "test"),
      //pom extra info
      publishArtifact in Test := false
    )
  )
  .dependsOn(macros)

lazy val playlibrary = project
  .settings(
    metaMacroSettings ++ Seq(
      name := "context-aware-logging-play",
      version := "2.6.0-SNAPSHOT",
      scalacOptions ++= List(
        "-unchecked",
        "-deprecation",
        "-language:_",
        "-encoding",
        "UTF-8"
      ),
      //Library dependencies
      libraryDependencies ++= Seq(
        "com.typesafe.play" %% "play" % "2.6.2" % "provided",
        "de.lenabrueder" %% "context-aware-logging" % "0.1"
      ),
      // Test dependencies
      libraryDependencies ++= Seq(
        "org.scalatest" %% "scalatest" % "3.0.1",
        "com.github.alexarchambault" %% "scalacheck-shapeless_1.13" % "1.1.3",
        "org.scalacheck" %% "scalacheck" % "1.13.4",
        "ch.qos.logback" % "logback-classic" % "1.2.3"
      ).map(_ % "test"),
      //pom extra info
      publishMavenStyle := true
    ))
  .dependsOn(library)

//settings to compile readme
tutSettings
tutSourceDirectory := baseDirectory.value / "tut"
tutTargetDirectory := baseDirectory.value
