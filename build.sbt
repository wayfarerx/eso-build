import Dependencies._

lazy val common = Seq(
  organization := "net.wayfarerx",
  scalaVersion := "2.12.1",
  version := "0.1.0-SNAPSHOT"
)

lazy val www = (project in file("www")).
  settings(
    common,
    name := "www",
    libraryDependencies += scalaTest % Test
  )
