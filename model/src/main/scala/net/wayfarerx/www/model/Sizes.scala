/*
 * Sizes.scala
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
 * Base type for the supported resolution factors.
 */
sealed trait Sizes

/**
 * Factory for the supported resolution factors.
 */
object Sizes {

  /** All the supported sizes. */
  val All: Vector[Sizes] = Vector(Small, Medium, Large)

  /**
   * The small size.
   */
  case object Small extends Sizes {

    /* Return the name. */
    override def toString: String = "small"

  }

  /**
   * The medium size.
   */
  case object Medium extends Sizes {

    /* Return the name. */
    override def toString: String = "medium"

  }

  /**
   * The large size.
   */
  case object Large extends Sizes {

    /* Return the name. */
    override def toString: String = "large"

  }

}
