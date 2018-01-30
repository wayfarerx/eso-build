/*
 * Location.scala
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

package net.wayfarerx.www.api

/**
 * Base type for concrete asset locations.
 */
sealed trait Location

/**
 * Concrete asset location implementations.
 */
object Location {

  /**
   * Base type for assets that are part of the local deployment.
   */
  sealed trait Local extends Location {

    /** The path of the location. */
    def path: Path

    /** True if this location is absolute. */
    final def isAbsolute: Boolean = !isRelative

    /** True if this location is relative. */
    def isRelative: Boolean

    /** True if this location points to a directory. */
    final def isDirectory: Boolean = !isFile

    /** True if this location points to a file. */
    final def isFile: Boolean = {

      @annotation.tailrec
      def pointsToFile(search: Path): Boolean = search match {
        case leaf@Path.Leaf(name) if !leaf.isCurrent && !leaf.isParent => name contains '.'
        case Path.Branch(_, next) => pointsToFile(next)
        case _ => false
      }

      pointsToFile(path)
    }

  }

  /**
   * A local location referenced by a relative path.
   *
   * @param path The relative path of the location.
   */
  case class Relative(path: Path) extends Local {

    /* Always relative. */
    override def isRelative: Boolean = true

    /* Encode the relative path. */
    override def toString: String = path match {
      case Path.Empty => Path.Current + Path.Separator
      case directory if isDirectory => directory.toString + Path.Separator
      case file => file.toString
    }

  }

  /**
   * A local location referenced by an absolute path.
   *
   * @param path The absolute path of the location.
   */
  case class Absolute(path: Path) extends Local {

    /* Never relative. */
    override def isRelative: Boolean = false

    /* Encode the absolute path. */
    override def toString: String = path match {
      case Path.Empty => Path.Separator
      case directory if isDirectory => Path.Separator + directory.toString + Path.Separator
      case file => Path.Separator + file.toString
    }

  }

  /**
   * An external location referenced by a URL.
   *
   * @param url The url of the location.
   */
  case class External(url: String) extends Location {

    /* Return the URL. */
    override def toString: String = url

  }

}
