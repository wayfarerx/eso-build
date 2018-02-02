/*
 * ServeWebsite.scala
 *
 * Copyright 2018 wayfarerx <x@wayfarerx.net> (@thewayfarerx)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.wayfarerx.www.generator
package main

import java.nio.file.{Files, Path, Paths}
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

import cats.effect.IO
import fs2.{Stream, io, text}
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletHandler

/**
 * Application that serves the website using an embedded jetty container.
 */
object ServeWebsite extends Website with App {

  /* Return the parent directory. */
  override lazy val projectDirectory: Path = Paths.get("..").toAbsolutePath

  {
    Assets.initialize(projectDirectory)
    val port = 4000
    val server = new Server(port)
    val handler = new ServletHandler
    server.setHandler(handler)
    handler.addServletWithMapping(classOf[WebsiteServlet], "/*")
    server.start()
    println(s"Server running on port $port.")
    server.join()
  }

  /**
   * Attempts to serve the main CSS file.
   *
   * @param location The path that must match the main CSS file path.
   * @return The mime type and contents of the main CSS file if the required path is supplied.
   */
  def readCss(location: String): Option[(String, Stream[IO, Byte])] =
    if (location != "/css/wayfarerx.css") None else Some("text/css" -> Stream.emit(Styles()).through(text.utf8Encode))

  /**
   * Attempts to serve a page.
   *
   * @param location The path to the page.
   * @return The mime type and contents of the requested page it it exists.
   */
  def readPage(location: String): Option[(String, Stream[IO, Byte])] =
    Pages get location map (page => "text/html" -> Stream.emit(page()).through(text.utf8Encode))

  /**
   * Attempts to serve a file directly from the assets directory.
   *
   * @param location The path of the file inside the assets directory.
   * @return The mime type and contents of the requested file if it exists.
   */
  def readAsset(location: String): Option[(String, Stream[IO, Byte])] =
    Assets get location map (asset => Files.probeContentType(asset) -> io.file.readAll[IO](asset, 2048))

  /**
   * The servlet that bridges between Jetty and our internal implementation.
   */
  final class WebsiteServlet extends HttpServlet {
    override protected def doGet(request: HttpServletRequest, response: HttpServletResponse): Unit = {
      readCss(request.getPathInfo) orElse
        readPage(request.getPathInfo) orElse
        readAsset(request.getPathInfo) match {
        case Some((mime, stream)) =>
          response.setContentType(mime)
          response.setStatus(HttpServletResponse.SC_OK)
          stream.through(io.writeOutputStream[IO](IO(response.getOutputStream))).drain.run.unsafeRunSync()
        case None =>
          response.setContentType("text/html")
          response.setStatus(HttpServletResponse.SC_NOT_FOUND)
          response.getWriter.println(
            s"""<html>
               |  <head>
               |    <title>404 Not Found</title>
               |  </head>
               |  <body>
               |    Not Found: ${request.getPathInfo}
               |  </body>
               |</html>"""
              .stripMargin)
      }
    }
  }


}
