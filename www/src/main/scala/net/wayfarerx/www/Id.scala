/*
 * Id.scala
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

/**
 * The unique ID used for all top-level content.
 */
final class Id private(val value: String) extends AnyVal

/**
 * Factory for content IDs.
 */
object Id {

  /**
   * Creates a new ID by normalizing the specified string.
   *
   * @param value The value to normalize and use as an ID.
   * @return The normalized ID.
   */
  def apply(value: String): Id = new Id(
    value
      .replaceAll("""[\`\'\"\(\)\[\]\{\}\<\>]+""", "")
      .trim.toLowerCase
      .replaceAll("""[^a-z0-9]+""", "-")
  )

}