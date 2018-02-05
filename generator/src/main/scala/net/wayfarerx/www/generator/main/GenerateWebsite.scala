/*
 * GenerateWebsite.scala
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

/**
 * Application that generates the website to a destination folder.
 */
object GenerateWebsite extends BaseWebsite with App {

  /* Use the production site location. */
  override lazy val Server: String = "https://wayfarerx.net"

  /* Use the current directory. */
  override lazy val projectDirectory: Path = Paths.get(".").toAbsolutePath

  {
    val root = Paths.get(".").toAbsolutePath
    val target = root.resolve("target/website")
    // Deploy the assets.
    val allAssets = Assets.values map { asset =>
      val destination = target.resolve(asset.location.substring(1))
      Files.createDirectories(destination.getParent)
      Files.write(destination, asset.contents())
      destination
    }
    // Deploy the pages.
    val allPages = Pages.map { case (location, page) =>
      val destination = target.resolve(location.substring(1) + "index.html")
      Files.createDirectories(destination.getParent)
      Files.write(destination, page.contents().getBytes("UTF-8"))
      destination
    }.toVector
    // Deploy the CSS.
    val allStyles = Stylesheets.values.map { stylesheet =>
      val destination = target.resolve(stylesheet.location.substring(1))
      Files.createDirectories(destination.getParent)
      Files.write(destination, stylesheet.contents().getBytes("UTF-8"))
      destination
    }
    // TODO Make a big diff or something.
    allAssets ++ allPages ++ allStyles foreach println
  }

}
