/*
 * Name.scala
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

import language.implicitConversions

/**
 * Base type for names that can be transformed into normalized, context-sensitive IDs.
 */
final class Name private(val value: String, val display: String) {

  /* Ignore the display name in equality checks. */
  override def equals(obj: Any): Boolean = obj match {
    case that: Name => that.value == value
    case _ => false
  }

  /* Ignore the display name in equality checks. */
  override def hashCode(): Int = Name.hashCode ^ value.hashCode

  /* Return the normalized name. */
  override def toString: String = value

}

/**
 * Definitions of the supported name types.
 */
object Name {

  /** The empty name. */
  val empty: Name = new Name("", "")

  /**
   * Creates a name.
   *
   * @param display The display name to normalize.
   * @return A new name.
   */
  def apply(display: String): Name =
    new Name(display.map {
      case c if c.isLetterOrDigit => c.toLower.toString
      case '\'' | '"' | '`' => ""
      case _ => " "
    }.mkString.trim.replaceAll("""\s+""", "-"), display)

}
