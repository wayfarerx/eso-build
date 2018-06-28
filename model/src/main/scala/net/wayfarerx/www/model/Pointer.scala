/*
 * Pointer.scala
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

import reflect.ClassTag

/**
 * Base class for references within the site.
 *
 * @tparam T The type of entity to search for.
 */
sealed abstract class Pointer[T <: AnyRef : ClassTag] {

  /** Returns the targeted class. */
  final def cls: Class[_] = implicitly[ClassTag[T]].runtimeClass

  /**
   * Narrows this pointer's type.
   *
   * @tparam U The new type of pointer.
   * @return A narrowed copy of this pointer.
   */
  def narrow[U <: T : ClassTag]: Pointer[U]

}

/**
 * Definitions of the supported reference types.
 */
object Pointer {

  /**
   * Searches for the specified name.
   *
   * @tparam T The type to search for.
   * @param name The name to search for.
   * @return A search for the specified name.
   */
  def apply[T <: AnyRef : ClassTag](name: Name): Search[T] = Search(name)

  /**
   * References the specified relative path.
   *
   * @tparam T The type to reference.
   * @param path The relative path to reference.
   * @return A reference to the specified relative path.
   */
  def apply[T <: AnyRef : ClassTag](path: Path): Relative[T] = Relative(path)

  /**
   * References the specified absolute path.
   *
   * @tparam T The type to reference.
   * @param location The absolute location to reference.
   * @return A reference to the specified absolute path.
   */
  def apply[T <: AnyRef : ClassTag](location: Location): Absolute[T] = Absolute(location)

  /**
   * A reference to the closest entity with the specified name.
   *
   * @tparam T The type of entity to search for.
   * @param name The name to search for.
   */
  case class Search[T <: AnyRef : ClassTag](name: Name) extends Pointer[T] {

    /* Copy this pointer. */
    override def narrow[U <: T : ClassTag]: Pointer[U] = Search[U](name)

  }

  /**
   * A reference to an entity with a relative path.
   *
   * @tparam T The type of entity to search for.
   * @param path The path to search for.
   */
  case class Relative[T <: AnyRef : ClassTag](path: Path) extends Pointer[T] {

    /* Copy this pointer. */
    override def narrow[U <: T : ClassTag]: Pointer[U] = Relative[U](path)

  }

  /**
   * A reference to an entity with an absolute path.
   *
   * @tparam T The type of entity to search for.
   * @param location The location to search for.
   */
  case class Absolute[T <: AnyRef : ClassTag](location: Location) extends Pointer[T] {

    /* Copy this pointer. */
    override def narrow[U <: T : ClassTag]: Pointer[U] = Absolute[U](location)

  }

}
