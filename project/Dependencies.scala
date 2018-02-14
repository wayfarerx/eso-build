import sbt._

object Dependencies {

  lazy val flexmark = "com.vladsch.flexmark" % "flexmark-all" % "0.30.0"
  lazy val scalaTags = "com.lihaoyi" %% "scalatags" % "0.6.7"
  lazy val scalaCss = "com.github.japgolly.scalacss" %% "ext-scalatags" % "0.5.3"
  lazy val catsEffect = "org.typelevel" %% "cats-effect" % "0.5"
  lazy val fs2 = "co.fs2" % "fs2-core_2.12" % "0.10.0-M8"
  lazy val fs2io = "co.fs2" %% "fs2-io" % "0.10.0-M8"
  lazy val jettyServer = "org.eclipse.jetty" % "jetty-servlet" % "9.3.12.v20160915"
  lazy val jettyServlet = "org.eclipse.jetty" % "jetty-server" % "9.3.12.v20160915"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1"

}
