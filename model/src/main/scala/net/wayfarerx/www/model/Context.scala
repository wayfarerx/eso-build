/*
 * Context.scala
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

import util.Try

/**
 * A context for resolving pointers.
 */
trait Context {

  /**
   * Attempts to locate the absolute path of the entity referenced by the specified pointer.
   *
   * @tparam T The type of the pointer to locate the absolute path of.
   * @param pointer The pointer to the desired entity.
   * @return The result of attempting to locate the  absolute path of entity referenced by the specified pointer.
   */
  def locate[T <: AnyRef](pointer: Pointer[T]): Try[String]

  /**
   * Attempts to load the title of the entity referenced by the specified pointer.
   *
   * @tparam T The type of the pointer to load the title of.
   * @param pointer The pointer to the desired entity.
   * @return The result of attempting to load the title of the entity referenced by the specified pointer.
   */
  def loadTitle[T <: AnyRef](pointer: Pointer[T]): Try[String]

  /**
   * Attempts to load the description of the entity referenced by the specified pointer.
   *
   * @tparam T The type of the pointer to load the description of.
   * @param pointer The pointer to the desired entity.
   * @return The result of attempting to load the description of the entity referenced by the specified pointer.
   */
  def loadDescription[T <: AnyRef](pointer: Pointer[T]): Try[Markup]

}
