/*
 * Section.scala
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
 * A section in a document.
 *
 * @param level    The level of this section.
 * @param header   The header of this section.
 * @param content  The content of this section.
 * @param sections The child sections of this section.
 * @param id       The style ID of this section.
 * @param classes  The style classes assigned to this section.
 */
case class Section(
  level: Int,
  header: Markup,
  content: Markup,
  sections: Vector[Section] = Vector.empty,
  id: Option[String] = None,
  classes: Vector[String] = Vector.empty
) extends Styled {

  /** The name of this section. */
  lazy val name: Name = Name(header.strip)

  /** The title of this section. */
  def title: String = name.display

}
