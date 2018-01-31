import Dependencies._

lazy val common = Seq(
  organization := "net.wayfarerx",
  scalaVersion := "2.12.1",
  version := "0.1.0-SNAPSHOT"
)

lazy val www = (project in file("www")).
  settings(
    common,
    libraryDependencies += catsEffect,
    libraryDependencies += fs2,
    libraryDependencies += fs2io,
    libraryDependencies += scalaTest % Test,
    name := "www"
  )

lazy val generator = (project in file("generator")).
  settings(
    common,
    libraryDependencies += scalaTags,
    libraryDependencies += catsEffect,
    libraryDependencies += fs2,
    libraryDependencies += fs2io,
    libraryDependencies += jettyServer,
    libraryDependencies += jettyServlet,
    libraryDependencies += scalaTest % Test,
    name := "generator"
  ) dependsOn www

run in Compile := (run in Compile in generator)
mainClass in run := Some("net.wayfarerx.www.generator.main.GenerateWebsite")

mainClass in reStart := Some("net.wayfarerx.www.generator.main.ServeWebsite")