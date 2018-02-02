/*
 * Stylesheet.scala
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
 * Data that can be written to a CSS file.
 */
trait Stylesheet {

  /** The location of the stylesheet in the website. */
  def location: String

  /** Returns the text of the stylesheet. */
  def apply(): String

  /* Return the text of the stylesheet. */
  final override def toString: String = apply()

}

/**
 * Factory for stylesheets.
 */
object Stylesheet {

  /**
   * Creates a stylesheet.
   *
   * @param location The location of the stylesheet.
   * @param f The function that produces the contents of the stylesheet.
   * @return A new stylesheet.
   */
  def apply(location: String)(f: => String): Stylesheet = {
    val _location = location
    new Stylesheet {

      override def location: String = _location

      override def apply(): String = f

    }
  }

}
