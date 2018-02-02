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

import java.nio.file.{Files, Path, Paths, StandardCopyOption}

/**
 * Application that generates the website to a destination folder.
 */
object GenerateWebsite extends Website with App {

  {
    val root = Paths.get(".").toAbsolutePath
    Assets.initialize(root)
    val target = root.resolve("target/website")
    // Deploy the assets.
    val assets = Assets.list map { source =>
      val destination = target.resolve(source.subpath(Assets.root.getNameCount, source.getNameCount))
      Files.createDirectories(destination.getParent)
      Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING)
      destination
    }
    // Deploy the pages.
    val pages = Pages.map { case (location, page) =>
      val destination = target.resolve(location.substring(1) + "index.html")
      Files.createDirectories(destination.getParent)
      Files.write(destination, page().getBytes("UTF-8"))
      destination
    }.toVector
    // Deploy the CSS.
    val styles = Styles.map { case (location, style) =>
      val destination = target.resolve(location.substring(1))
      Files.createDirectories(destination.getParent)
      Files.write(destination, style().getBytes("UTF-8"))
      destination
    }
    // TODO Make a big diff or something.
    assets ++ pages ++ styles foreach println
  }

}
