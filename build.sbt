import Dependencies._

lazy val common = Seq(
  organization := "net.wayfarerx",
  scalaVersion := "2.12.1",
  version := "0.1.0-SNAPSHOT"
)

lazy val old = (project in file("old")).
  settings(
    common,
    name := "old",
    libraryDependencies += catsEffect,
    libraryDependencies += fs2,
    libraryDependencies += fs2io,
    libraryDependencies += scalaTest % Test
  )

lazy val www_core = (project in file("www/core")).
  settings(
    common,
    name := "www2",
    libraryDependencies += catsEffect,
    libraryDependencies += fs2,
    libraryDependencies += fs2io,
    libraryDependencies += scalaTest % Test
  )

run in Compile <<= (run in Compile in www_core)
