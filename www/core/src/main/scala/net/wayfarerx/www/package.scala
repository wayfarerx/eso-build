/*
 * package.scala
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

package net.wayfarerx

import language.implicitConversions

/**
 * Package object that defines common terms in the system.
 */
package object www {

  /**
   * Implicitly treat all paths as relative locations.
   *
   * @param path The path to treat as a location.
   * @return A relative location for the specified path.
   */
  implicit def pathToRelativeLocation(path: Path): Location.Relative = Location.Relative(path)

  /**
   * Support for strings as paths.
   *
   * @param string The string to add path support to.
   */
  implicit final class StringPathSupport(val string: String) extends AnyVal {

    /**
     * Create a path from the string and append the supplied path.
     *
     * @param that The path to append.
     * @return The string as a path with the specified path appended.
     */
    def /(that: Path): Path = Path(string) / that

    /**
     * Create a path from the string and append the path created from the supplied string.
     *
     * @param that The path to append.
     * @return The string as a path with the specified path created from the supplied string appended.
     */
    def /(that: String): Path = Path(string) / that

  }

  /**
   * A utility for expressly defining an absolute location.
   */
  object / {

    /**
     * Returns an absolute location for the specified path.
     *
     * @param path The path to the absolute location.
     * @return An absolute location for the specified path.
     */
    def apply(path: Path): Location.Absolute = Location.Absolute(path)

  }

}
