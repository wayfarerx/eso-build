/*
 * dsl.scala
 *
 * Copyright 2017 wayfarerx <x@wayfarerx.net> (@thewayfarerx)
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

import language.implicitConversions

trait DataConversions {

  /**
   * Implicit support for strings as data values.
   *
   * @param string The string that should be treated as data.
   * @return A data value containing the specified string.
   */
  implicit def stringToValueData(string: String): Value = Value(string)

  /**
   * Implicit support for multiple strings as data collections.
   *
   * @param strings The strins that should be treated as a collection of data.
   * @return A data collection containing the specified strings.
   */
  implicit def stringsToCollectionData(strings: Iterable[String]): Collection = Collection(strings.toVector map Value)

}

trait ContentConversions {

  /**
   * Implicit support for strings as text content.
   *
   * @param content The content to represent.
   * @return The new text content object.
   */
  implicit def stringToTextContent(content: String): Text = Text(content)

  /**
   * Implicit support for entities as a link to another entity.
   *
   * @param entity The entity to link to.
   * @return A link to the specified entity.
   */
  implicit def entityToLinkContent(entity: Entity): Link = Link(entity.location, entity.displayName)

}

trait ContentDSL {

  def em(content: Content, classes: String*): Emphasis = Emphasis(content, classes: _*)

  def strong(content: Content, classes: String*): Strong = Strong(content, classes: _*)

  def p(content: Content, classes: String*): Paragraph = Paragraph(content, classes: _*)

  def a(href: String, content: Content, classes: String*): Link = Link(href, content, classes: _*)

  def div(content: Content, classes: String*): Div = Div(content, classes: _*)

  def h1(content: Content, classes: String*): Heading = Heading(1, content, classes: _*)

  def h2(content: Content, classes: String*): Heading = Heading(2, content, classes: _*)

  def h3(content: Content, classes: String*): Heading = Heading(3, content, classes: _*)

  def h4(content: Content, classes: String*): Heading = Heading(4, content, classes: _*)

  def h5(content: Content, classes: String*): Heading = Heading(5, content, classes: _*)

  def h6(content: Content, classes: String*): Heading = Heading(6, content, classes: _*)

  def hr(classes: String*): HorizontalRule = HorizontalRule(classes: _*)

  def ol(items: Item*): Ordered = Ordered(items.toVector)

  def ul(items: Item*): Unordered = Unordered(items.toVector)

  def li(content: Content, classes: String*): Item = Item(content, classes: _*)

  def section(content: Content, classes: String*): Section = Section(content, classes: _*)

  def header(content: Content, classes: String*): Header = Header(content, classes: _*)

  implicit final class StringAsId(id: String) {

    def $ (tag: Content.Tag): Content.Tag = tag.withId(Some(id))

  }

}
