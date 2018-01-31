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

import collection.JavaConverters._
import java.nio.file.{Files, Path, Paths}

/**
 * Application that generates the website to a destination folder.
 */
object GenerateWebsite extends Website with App {

  // TODO

  def findAssets2(target: Path, incoming: Vector[Path], outgoing: Vector[(Path, Path)]): Vector[(Path, Path)] =
    if (incoming.isEmpty) outgoing else incoming.head match {
      case directory if Files.isDirectory(directory) =>
        target.resolve()
        ???
      case file if Files.isRegularFile(file) =>
        ???
      case _ =>
        ???
  }

  def findAssets(prefix: String, cursor: Path): Vector[(String, Path)] =
    cursor match {
      case directory if Files.isDirectory(directory) =>
        val name = ""
        val location = s"$prefix$name${if (name.nonEmpty) "/" else ""}"
        Files.list(directory).toArray.map(p => findAssets(location, p.asInstanceOf[Path])).toVector.flatten
      case file if Files.isRegularFile(file) =>
        Vector(prefix -> cursor)
      case _ =>
        Vector()
    }

}


