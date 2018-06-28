/*
 * Main.scala
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

package net.wayfarerx.www
package main

import java.io.OutputStream
import java.nio.file.{Files, Paths}
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

import util.{Failure, Success, Try}

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{ServletHandler, ServletHolder}

import model._

object Main extends App {

  /** The path to the source documents. */
  private val source = Paths.get("site", "src", "main", "resources")

  /** The path to the destination documents. */
  private val destination = Paths.get("target", "www")

  /** The extra assets to use. */
  private val extraAssets = Set[(Location, String)](
    // FIXME Location.Root -> "favicon.ico"
  )

  /** The port to serve the website on. */
  private val port = 4000

  /**
   * Deploys the complete site.
   *
   * @param remaining The nodes still awaiting deployment.
   * @param assets    The assets awaiting deployment.
   * @return The set of all assets.
   */
  @annotation.tailrec
  private def deploy(
    renderer: Renderer,
    remaining: Vector[Node],
    assets: Set[(Location, String)]
  ): Try[Set[(Location, String)]] =
    if (remaining.isEmpty) Success(assets) else {
      val node = remaining.head
      renderer.render(node, bytes => Try {
        val directory = node.location match {
          case Location.Root => destination
          case nested => destination.resolve(nested.path.toString)
        }
        Files.createDirectories(directory)
        Files.write(directory.resolve("index.html"), bytes)
      }) flatMap (a => node.children map (_ -> a)) match {
        case Success((children, newAssets)) => deploy(renderer, children ++ remaining.tail, assets ++ newAssets)
        case Failure(thrown) => Failure(thrown)
      }
    }

  /** The root node. */
  {
    args filterNot (_ startsWith "-") match {
      case Array(className) =>
        Try(Node(source, getClass.getClassLoader.loadClass(className).newInstance().asInstanceOf[Site[_ <: AnyRef]]))
      case Array() =>
        Failure(new IllegalArgumentException("No site class name specified."))
      case _ =>
        Failure(new IllegalArgumentException("Please specify one sie class name."))
    }
  } flatMap { root =>
    val renderer = new Renderer(root.site, source, destination)
    // Create and initialize the site before selecting an execution style.
    Try(root.index(Location.Root, classOf[AnyRef])) flatMap { _ =>
      if (args contains "-serve") {
        // Serve all pages and assets in the site.
        Try {
          val server = new Server(port)
          val handler = new ServletHandler
          server.setHandler(handler)
          handler.addServletWithMapping(new ServletHolder(new WebsiteServlet(root, renderer)), "/*")
          server.start()
          println(s"Server running on port $port.")
          server.join()
        }
      } else {
        // Deploy all pages and assets in the site.
        println(s"Generating in directory $destination.")
        deploy(renderer, Vector(root), extraAssets) flatMap renderer.deploy map { result =>
          val css = destination.resolve("css")
          Files.createDirectories(css)
          Files.write(css.resolve("stylesheet.css"), root.site.stylesheet.getBytes("UTF-8"))
          result
        }
      }
    }
  } match {
    case Success(_) =>
      System.exit(0)
    case Failure(t) =>
      t.printStackTrace()
      System.exit(1)
  }

  /**
   * The servlet that bridges between Jetty and our internal implementation.
   */
  final class WebsiteServlet(root: Node, renderer: Renderer) extends HttpServlet {
    override protected def doGet(request: HttpServletRequest, response: HttpServletResponse): Unit = {
      val (path, file) = {
        val pathInfo = request.getPathInfo substring 1 split '/' filterNot (_.isEmpty)
        if (pathInfo.isEmpty || request.getPathInfo.endsWith("/")) Path(pathInfo: _*) -> None
        else (if (pathInfo.length > 1) Path(pathInfo.init: _*) else Path.empty) -> Some(pathInfo.last)
      }
      (file match {
        case Some("stylesheet.css") if path.toString == "css" =>
          Some("text/css" -> ((o: OutputStream) => Try(o.write(root.site.stylesheet.getBytes("UTF-8")))))
        case Some(filename) =>
          val target =
            if (path.isEmpty) source.resolve(filename)
            else source.resolve(Paths.get(path.toString)).resolve(filename)
          if (!Files.isRegularFile(target)) None
          else Some(Files.probeContentType(target) -> ((o: OutputStream) => Try(o.write(Files.readAllBytes(target)))))
        case None =>
          Location(path) flatMap (root.index(_, classOf[AnyRef])) map { node =>
            "text/html" -> ((o: OutputStream) => renderer.render(node, bytes => Try(o.write(bytes))))
          }
      }) match {
        case Some((mime, render)) =>
          response.setContentType(mime)
          val output = response.getOutputStream
          try render(output).get finally output.close()
        case None =>
          response.setContentType("text/html")
          response.setStatus(HttpServletResponse.SC_NOT_FOUND)
          val output = response.getWriter
          try response.getWriter.print(
            s"""<html>
               |  <head>
               |    <title>404 Not Found</title>
               |  </head>
               |  <body>
               |    Not Found: ${request.getPathInfo}
               |  </body>
               |</html>"""
              .stripMargin)
          finally output.close()
      }
    }
  }

}
