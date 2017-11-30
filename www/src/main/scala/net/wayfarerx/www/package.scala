/*
 * package.scala
 *
 * Copyright 2017 wayfarerx <x@wayfarerx.net> (@thewayfarerx)
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
 * Global definitions for the www package.
 */
package object www {

  /**
   * Implicit support for entities as a link to another entity.
   *
   * @param entity The entity to link to.
   * @return A link to the specified entity.
   */
  implicit def entityToContentLink(entity: Entity): Content =
    Link(entity.location, entity.title.toLowerCase)

  /**
   * Implicit support for strings as text content.
   *
   * @param content The content to represent.
   * @return The new text content object.
   */
  implicit def stringToTextContent(content: String): Text = Text(content)

  /**
   * Implicit support for strings as data values.
   *
   * @param string The string that should be treated as data.
   * @return A data value containing the specified string.
   */
  implicit def stringToValueData(string: String): Value = Value(string)

}
