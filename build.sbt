import Dependencies._

lazy val common = Seq(
  organization := "net.wayfarerx",
  scalaVersion := "2.12.1",
  version := "0.1.0-SNAPSHOT"
)

lazy val data = (project in file(".")).
  settings(
    common,
    name := "eso-build",
    libraryDependencies += scalaTest % Test
  )
