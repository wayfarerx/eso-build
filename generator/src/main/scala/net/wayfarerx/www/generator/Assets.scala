/*
 * Assets.scala
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

import collection.JavaConverters._

import java.nio.file.{Files, Path}

/**
 * Directory of the assets that are available.
 */
object Assets {

  /** The root of the asset tree. */
  @volatile
  private var _root: Option[Path] = None

  /** Returns the root of the asset tree. */
  def root: Path = _root getOrElse { throw new IllegalStateException }

  /** Lists all the available assets. */
  def list: Vector[Path] = _root map { assets =>
    val stream = Files.walk(assets)
    try stream.iterator.asScala.filter(Files.isRegularFile(_)).toVector finally stream.close()
  } getOrElse Vector()

  /**
   * Returns the path to the specified asset.
   *
   * @param location The location of the asset to return.
   * @return The path to the specified asset if it exists.
   */
  def get(location: String): Option[Path] =
    _root map (_.resolve(if (location.startsWith("/")) location.drop(1) else location)) filter (Files.isRegularFile(_))

  /**
   * Initializes the asset manager with the root directory of the project.
   *
   * @param projectDirectory The root directory of the project.
   */
  private[generator] def initialize(projectDirectory: Path): Unit = {
    val root = Some(projectDirectory.resolve("www/src/main/assets/"))
    if (_root.isDefined) throw new IllegalStateException else synchronized {
      if (_root.isDefined) throw new IllegalStateException else _root = root
    }
  }

}
