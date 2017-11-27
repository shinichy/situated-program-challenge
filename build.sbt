name := """situated-program"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.4"

val circeVersion = "0.8.0"

libraryDependencies ++= Seq(
  "org.postgresql" % "postgresql" % "42.1.4",
  "io.getquill" %% "quill-jdbc" % "2.3.0",
  "com.dripower" %% "play-circe" % "2608.5",
  "io.circe" %% "circe-java8" % circeVersion,
  "io.circe" %% "circe-generic-extras" % circeVersion,
  "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
)

resolvers += Resolver.sonatypeRepo("releases")
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
