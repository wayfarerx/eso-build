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

import java.nio.file.{Path, Paths}

import css.WayfarerxCss

/**
 * Base type for applications that work with the website.
 */
trait Website {

  /** The path to the assets for the website. */
  final lazy val Assets: Path = Paths.get("../www/src/main/assets/")

  /** The pages that make up the website, indexed by location. */
  final lazy val Pages: Map[String, Page] = {

    @annotation.tailrec
    def search(incoming: Vector[(String, Page)], outgoing: Vector[(String, Page)]): Vector[(String, Page)] =
      if (incoming.isEmpty) outgoing else {
        val (prefix, page) = incoming.head
        val location = s"$prefix${page.name}${if (page.name.nonEmpty) "/" else ""}"
        search(incoming.tail ++ page.children.map(child => child.name -> child), outgoing :+ (location, page))
      }

    search(Vector("/" -> HomePage), Vector()).toMap
  }

  /** The style sheet for the website. */
  final lazy val Styles: () => String = WayfarerxCss.styleSheetText _

}
