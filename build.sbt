import Dependencies._

lazy val common = Seq(
  organization := "net.wayfarerx",
  scalaVersion := "2.12.1",
  version := "0.1.0-SNAPSHOT"
)

lazy val model = (project in file("model")).
  settings(
    common,
    name := "model",
    libraryDependencies += scalaTest % Test
  )

lazy val site = (project in file("site")).
  settings(
    common,
    name := "site",
    libraryDependencies ++= Seq(
      scalaCss,
      scalaTest % Test
    )
  ).dependsOn(model)

lazy val main = (project in file("main")).
  settings(
    common,
    name := "main",
    libraryDependencies ++= Seq(
      laika,
      scalaTags,
      jettyServer,
      jettyServlet,
      scalaTest % Test
    )
  ).dependsOn(model, site)


//
//
//
/*
lazy val www = (project in file("www")).
  settings(
    common,
    name := "www",
    libraryDependencies += commonsIO,
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
    libraryDependencies += jettyServer,
    libraryDependencies += jettyServlet,
    libraryDependencies += scalaTest % Test,
    mainClass in(Compile, run) := Some("net.wayfarerx.www.generator.main.GenerateWebsite"),
    mainClass in reStart := Some("net.wayfarerx.www.generator.main.ServeWebsite")
  ) dependsOn www
*/