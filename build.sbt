val TapirVersion      = "1.2.10"
val ZioVersion        = "2.0.9"
val ZioConfigVersion  = "3.0.7"
val Mongo4CatsVersion = "0.6.7"

lazy val rootProject = (project in file("."))
  .enablePlugins(JavaAppPackaging, DockerPlugin, AshScriptPlugin)
  .settings(
    dockerUpdateLatest := true,
    dockerBaseImage    := "openjdk:11-jre-slim-buster",
    Seq(
      name         := "antiquities",
      version      := "0.1.0-SNAPSHOT",
      organization := "org.mehmetcc",
      scalaVersion := "2.13.10",
      libraryDependencies ++= Seq(
        "com.softwaremill.sttp.tapir"   %% "tapir-vertx-server-zio"   % TapirVersion,
        "com.softwaremill.sttp.tapir"   %% "tapir-prometheus-metrics" % TapirVersion,
        "com.softwaremill.sttp.tapir"   %% "tapir-swagger-ui-bundle"  % TapirVersion,
        "com.softwaremill.sttp.tapir"   %% "tapir-json-zio"           % TapirVersion,
        "dev.zio"                       %% "zio-config"               % ZioConfigVersion,
        "dev.zio"                       %% "zio-config-typesafe"      % ZioConfigVersion,
        "dev.zio"                       %% "zio-config-magnolia"      % ZioConfigVersion,
        "io.github.kirill5k"            %% "mongo4cats-zio"           % Mongo4CatsVersion,
        "ch.qos.logback"                 % "logback-classic"          % "1.4.5",
        "com.softwaremill.sttp.tapir"   %% "tapir-sttp-stub-server"   % TapirVersion      % Test,
        "dev.zio"                       %% "zio-test"                 % ZioVersion        % Test,
        "dev.zio"                       %% "zio-test-sbt"             % ZioVersion        % Test,
        "com.softwaremill.sttp.client3" %% "zio-json"                 % "3.8.12"          % Test,
        "io.github.kirill5k"            %% "mongo4cats-zio-embedded"  % Mongo4CatsVersion % Test
      ),
      addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
      testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
    )
  )
