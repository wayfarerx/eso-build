/*
 * Styled.scala
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
 * Base type for objects that can be styled.
 */
trait Styled {

  /** The ID of this object if it has one. */
  def id: Option[String]

  /** The classes assigned to this object. */
  def classes: Vector[String]

}

/**
 * Definitions associated with styled objects.
 */
object Styled {

  /** The empty styled object. */
  val empty: Styled = new Styled {
    override def id: Option[String] = None

    override def classes: Vector[String] = Vector.empty
  }

}
