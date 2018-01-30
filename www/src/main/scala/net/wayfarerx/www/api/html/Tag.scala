/*
 * Tag.scala
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
package html

/**
 * Base type for classes that represent HTML tags.
 */
trait Tag {

  /** The concrete type of this tag. */
  type This <: Tag

  /**  The ID of this tag. */
  def id: Option[String]

  /**  The classes specified for this tag. */
  def classes: Vector[String]

  /**
   * Returns a copy of this tag with the specified ID and classes.
   *
   * @param id The ID of the new tag (defaults to this tag's ID).
   * @param classes The classes of the new tag (defaults to this tag's classes).
   * @return A copy of this tag with the specified ID and classes.
   */
  def copyTag(id: Option[String] = id, classes: Vector[String] = classes): This

}
