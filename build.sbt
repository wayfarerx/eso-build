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
    libraryDependencies += circeCore,
    libraryDependencies += circeGeneric,
    libraryDependencies += circeYaml,
    libraryDependencies += laika,
    libraryDependencies += scalaTest % Test
  )

lazy val generator = (project in file("generator")).
  settings(
    common,
    name := "generator",
    libraryDependencies += scalaTags,
    libraryDependencies += scalaCss,
    libraryDependencies += catsEffect,
    libraryDependencies += fs2,
    libraryDependencies += fs2io,
    libraryDependencies += jettyServer,
    libraryDependencies += jettyServlet,
    libraryDependencies += scalaTest % Test,
    mainClass in(Compile, run) := Some("net.wayfarerx.www.generator.main.GenerateWebsite"),
    mainClass in reStart := Some("net.wayfarerx.www.generator.main.ServeWebsite")
  ) dependsOn www
