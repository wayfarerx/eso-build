/*
 * Repository.scala
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

/**
 * Base type for repositories that contain website data.
 *
 * @tparam Key The type of key that identifies entries.
 * @tparam Value The type of value this repository provides.
 */
trait Repository[Key, Value] {

  /**
   * Lists the entries in this repository.
   *
   * @return The entries in this repository.
   */
  def entries: Vector[Value]

  /**
   * Attempts to return a value from this repository.
   *
   * @param key The key associated with the value to return.
   * @return The specified value from this repository if it exists.
   */
  def get(key: Key): Option[Value]

  /**
   * Returns a value from this repository.
   *
   * @param key The key associated with the value to return.
   * @return The specified value from this repository.
   */
  final def apply(key: Key): Value =
    get(key) getOrElse { throw new IllegalArgumentException(key.toString)}

}
