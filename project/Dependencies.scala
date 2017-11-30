import sbt._

object Dependencies {

  lazy val catsEffect = "org.typelevel" %% "cats-effect" % "0.5"
  lazy val fs2 = "co.fs2" % "fs2-core_2.12" % "0.10.0-M8"
  lazy val fs2io = "co.fs2" %% "fs2-io" % "0.10.0-M8"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1"

}
