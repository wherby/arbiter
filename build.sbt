lazy val akkaHttpVersion = "10.1.7"
lazy val akkaVersion    = "2.5.22"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "io.github.wherby",
      scalaVersion    := "2.12.7",
    )),
    name := "arbiter",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"                % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json"     % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-actor"               % akkaVersion,
      "com.typesafe.akka" %% "akka-stream"              % akkaVersion,
      "ch.qos.logback"    % "logback-classic"           % "1.2.3",
      "com.typesafe.akka" %% "akka-cluster"             % akkaVersion,
      "com.typesafe.akka" %% "akka-http-testkit"        % akkaHttpVersion % Test,
      "org.scalatest"     %% "scalatest"                % "3.0.8"         % Test
    )
  )
