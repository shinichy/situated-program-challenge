val circeVersion = "0.8.0"
val playWsStandaloneVersion = "1.1.3"

lazy val commonSettings = Seq(
  organization := "jp.starttoday",
  version := "1.0-SNAPSHOT",
  scalaVersion := "2.12.4"
)

lazy val root = (project in file("."))
  .aggregate(server, client)

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
    resolvers += Resolver.sonatypeRepo("releases"),
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
    javaOptions in Compile += "-Dquill.macro.log=false",
    javaOptions in Test += "-Dconfig.resource=test/application.conf",
    javaOptions in Test += "-Dlogger.resource=test/logback.xml"
  )
  .enablePlugins(PlayScala)

lazy val client = project
  .settings(commonSettings: _*)
  .settings(
    name := "client",
    cancelable in Global := true,
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "com.typesafe.play" %% "play-ahc-ws-standalone" % playWsStandaloneVersion
    )
  )
