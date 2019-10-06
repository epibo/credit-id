name := "credit-id"

version := "1.0"

scalaVersion := "2.12.10"
parallelExecution in Test := false

addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.10.3")
addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.0")
addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.1" cross CrossVersion.full)

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  "-Ypartial-unification",
  "-Xfatal-warnings",
)


libraryDependencies ++= typelevel ++ auxiliary

lazy val typelevel = cats ++ shapeless ++ monix ++ http4s ++ fetch ++ circe

lazy val auxiliary = logs ++ enums ++ args ++ validation ++ config

lazy val http4s = {
  val Http4sVersion = "0.20.11"
  Seq(
    "org.http4s"      %% "http4s-prometheus-metrics" % Http4sVersion,
    "org.http4s"      %% "http4s-dropwizard-metrics" % Http4sVersion,
    "org.http4s"      %% "http4s-blaze-server" % Http4sVersion,
    "org.http4s"      %% "http4s-blaze-client" % Http4sVersion,
    "org.http4s"      %% "http4s-circe"        % Http4sVersion,
    "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
  )
}

lazy val fetch = {
  Seq("com.47deg" %% "fetch" % "1.2.0")
}
lazy val cats = {
  Seq(
    "org.typelevel" %% "kittens",
    "org.typelevel" %% "cats-effect",
    "org.typelevel" %% "cats-core",
    "org.typelevel" %% "cats-free"
  ).map(_ % "2.0.0")
}



lazy val shapeless = {
  Seq(
    "com.chuusai" %% "shapeless" % "2.3.3",
    "com.github.alexarchambault" %% "scalacheck-shapeless_1.14" % "1.2.3"
  )
}

lazy val monix = {
  Seq("io.monix" %% "monix" % "3.0.0")
}

lazy val validation = {
  Seq(
    "org.scalacheck" %% "scalacheck" % "1.14.0",
    "org.scalatest" %% "scalatest" % "3.0.8",
    "org.scalamock" %% "scalamock" % "4.4.0",
  ).map(_ % Test)
}


lazy val logs = {
  Seq(
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
  )
}

lazy val args = {
  val version = "3.5.1"
  Seq(
    "org.backuity.clist" %% "clist-core" % version,
    "org.backuity.clist" %% "clist-macros" % version % "provided"
  )
}

lazy val enums = {
  Seq(
    "com.beachape" %% "enumeratum" % "1.5.13",
    "com.beachape" %% "enumeratum-circe" % "1.5.21",
    "com.beachape" %% "enumeratum-cats" % "1.5.16"
  )
}

lazy val config = {
  Seq("com.github.pureconfig" %% "pureconfig" % "0.12.1")
}


lazy val circe = {
  val circeVersion = "0.12.1"
  Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser",
  ).map(_ % circeVersion)
}
