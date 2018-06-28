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

package net.wayfarerx.www
package model

/**
 * Base type for IDs in the site.
 */
sealed trait Location {

  /** The absolute path of this location. */
  def path: Path

  /** The parent of this path. */
  def parentLocation: Option[Location]

  /**
   * Calculates the longest prefix that is present in both this location and that location.
   *
   * @param that The location to find a common prefix with.
   * @return The longest prefix that is present in both this location and that location.
   */
  def commonPrefix(that: Location): Location = {

    @annotation.tailrec
    def extract(first: Path, second: Path, results: Vector[String]): Vector[String] = {
      if (first.isEmpty || second.isEmpty || first(0) != second(0)) results
      else extract(first(1, first.length), second(1, second.length), results :+ first(0).toString)
    }

    ((Location.Root: Location) /: extract(path, that.path, Vector.empty)) { (parent, token) =>
      Location.Nested(parent, Name(token))
    }
  }

}

/**
 * Definitions of the supported IDs.
 */
object Location {

  /**
   * Creates a new location.
   *
   * @param names The names that form the path to this location.
   * @return A new location.
   */
  def apply(names: Name*): Location =
    ((Root: Location) /: names)((l, n) => Nested(l, n))

  /**
   * Attempts to create a location from the specified path.
   *
   * @param path The path to create a location from.
   * @return The location created from the specified path.
   */
  def apply(path: Path): Option[Location] =
    path.normalize map (apply(_: _*))

  /**
   * The ID that represents the root of the website.
   */
  case object Root extends Location {

    /* Return an empty path. */
    override def path: Path = Path.empty

    /* Return none. */
    override def parentLocation: Option[Location] = None

    /* The absolute path of this location. */
    override def toString: String = "/"

  }

  /**
   * An ID that is nested somewhere within the root of the website.
   *
   * @param parent The parent of this ID.
   * @param name   The name that identifies this ID within its parent.
   */
  case class Nested(parent: Location, name: Name) extends Location {

    /* Return the absolute path to this location. */
    override lazy val path: Path = parent.path :+ name

    /* Return the parent. */
    override def parentLocation: Option[Location] = Some(parent)

    /* The absolute path of this location. */
    override def toString: String = s"/$path/"

  }

}
