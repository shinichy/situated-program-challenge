val circeVersion = "0.8.0"

lazy val commonSettings = Seq(
  organization := "jp.starttoday",
  version := "1.0-SNAPSHOT",
  scalaVersion := "2.12.4",
  resolvers += Resolver.sonatypeRepo("releases"),
  addCompilerPlugin ("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
)

lazy val root = (project in file("."))
  .aggregate(common, server, client)

lazy val common = project
  .settings(commonSettings: _*)
  .settings(
    name := "common"
  )

lazy val server = project
  .settings(commonSettings: _*)
  .settings(
    name := "server",
    libraryDependencies ++= Seq(
      jdbc,
      "org.postgresql" % "postgresql" % "42.1.4",
      "io.getquill" %% "quill-jdbc" % "2.3.1",
      "com.dripower" %% "play-circe" % "2608.5",
      "io.circe" %% "circe-java8" % circeVersion,
      "io.circe" %% "circe-generic-extras" % circeVersion,
      "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided",
      "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % "test"
    ),
    javaOptions in Compile += "-Dquill.macro.log=false",
    javaOptions in Test += "-Dconfig.resource=test/application.conf",
    javaOptions in Test += "-Dlogger.resource=test/logback.xml"
  )
  .enablePlugins(PlayScala)
  .dependsOn(common)

lazy val client = project
  .settings(commonSettings: _*)
  .settings(
    name := "client",
    libraryDependencies ++= Seq(
      "com.dripower" %% "play-circe" % "2608.5",
      "io.circe" %% "circe-java8" % circeVersion,
      "io.circe" %% "circe-generic-extras" % circeVersion
    )
  )
  .dependsOn(common)
