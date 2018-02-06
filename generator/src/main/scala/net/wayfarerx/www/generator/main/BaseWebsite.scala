/*
 * Website.scala
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

import collection.JavaConverters._
import java.nio.file.{Files, Path}
import java.util.Properties


/**
 * Base class for the website generators.
 */
trait BaseWebsite
  extends Website
    with templates.AllTemplates
    with pages.AllPages
    with stylesheets.AllStylesheets {

  /** The root directory of the project. */
  def projectDirectory: Path

  /* Return the assets in the website indexed by location. */
  final override lazy val Assets: Map[String, Asset] = {
    val assets = projectDirectory.resolve("www/src/main/assets")
    val stream = Files.walk(assets)
    val v = try stream.iterator.asScala.filter(Files.isRegularFile(_)).map { path =>
      val location = "/" + path.subpath(assets.getNameCount, path.getNameCount).toString.replace('\\', '/')
      location -> Asset(location, Files.probeContentType(path))(() => Files.readAllBytes(path))
    }.toMap finally stream.close()
    println(v)
    v
  }

  /* Return the alt text in the website indexed by asset. */
  final override lazy val AltText: Map[Asset, String] = {
    var cache = Map[String, collection.Map[String, String]]()
    Assets.flatMap { case (location, asset) =>
      val lastSlash = location.lastIndexOf('/')
      val path = location.substring(0, lastSlash + 1) + "alt.properties"
      cache get path orElse {
        Option(getClass.getResourceAsStream(path)) map { stream =>
          val props = new Properties()
          try props.load(stream) finally stream.close()
          val map = props.asScala.asInstanceOf[collection.Map[String, String]]
          cache += path -> map
          map
        }
      } flatMap (_ get location.substring(lastSlash + 1) map (asset -> _))
    }
  }

  /* Return the pages in the website indexed by location. */
  final override lazy val Pages: Map[String, Page] = {
    @annotation.tailrec
    def search(incoming: Vector[(String, Page)], outgoing: Vector[(String, Page)]): Vector[(String, Page)] =
      if (incoming.isEmpty) outgoing else {
        val (prefix, page) = incoming.head
        val location = s"$prefix${page.name}${if (page.name.nonEmpty) "/" else ""}"
        search(incoming.tail ++ page.children.map(child => location -> child), outgoing :+ (location -> page))
      }

    search(Vector("/" -> Home), Vector()).toMap
  }

  /* Return the locations in the website indexed by page. */
  final override lazy val Locations: Map[Page, String] =
    Pages.map(e => e._2 -> e._1)

  /* Return the stylesheets in the website indexed by location. */
  final override lazy val Stylesheets: Map[String, Stylesheet] =
    Vector(
      "/css/wayfarerx.css" -> { () =>
        s"""@import url('https://fonts.googleapis.com/css?family=IM+Fell+Great+Primer|Raleway');
           |
           |@media screen {
           |  $CommonStyles
           |
           |  $WayfarerxStyles
           |}""".stripMargin
      }
    ).map { case (location, content) => location -> Stylesheet(location)(content) }.toMap


}
