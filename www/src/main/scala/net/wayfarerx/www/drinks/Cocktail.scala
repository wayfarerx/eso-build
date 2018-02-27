/*
 * Cocktail.scala
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
package drinks

/**
 * Definition of a cocktail.
 *
 * @param name         The name of this cocktail.
 * @param description  The simple description of this cocktail.
 * @param requirements The requirements for creating this cocktail.
 * @param instructions The instructions for creating this cocktail.
 * @param sections     The sections of this cocktail.
 * @param links        The links specified by this cocktail.
 */
case class Cocktail(
  name: Name,
  description: Content.Paragraph,
  requirements: Vector[Cocktail.Requirement],
  instructions: Vector[Content.Block],
  sections: Vector[Content.Section],
  links: Vector[Content.Link]
)

/**
 * Repository for all cocktails.
 */
object Cocktail {

  lazy val All: Index[Cocktail] = Index[Cocktail]("drinks/cocktails") { text =>
    val doc = Content.Document(text)
    val headings = Set("requirements", "instructions")
    val (named, sections) = doc.sections partition {
      case Content.Section(Content.Header(2, heading), _) if headings(heading.stripped.toLowerCase) => true
      case _ => false
    }
    val requirements = named find (_.header.content.stripped.toLowerCase == "requirements") collect {
      case Content.Section(_, Content.List(items)) => items flatMap {
        case Content.Link.Internal(id, _, _) =>
          Vector((Quantity(1, Quantity.Pieces), id, None))
        case Content.Inline.Group(Vector(Content.Text(qt), Content.Link.Internal(id, _, _))) =>
          Quantity(qt).map((_, id, None)).toVector
        case Content.Inline.Group(Vector(Content.Link.Internal(id, _, _), ql: Content.Text))
          if ql.content startsWith "," =>
          Vector((Quantity(1, Quantity.Pieces), id, Some(ql.copy(content = ql.content.substring(1).trim))))
        case Content.Inline.Group(Vector(Content.Text(qt), Content.Link.Internal(id, _, _), ql: Content.Text))
          if ql.content startsWith "," =>
          Quantity(qt).map((_, id, Some(ql.copy(content = ql.content.substring(1).trim)))).toVector
      } flatMap {
        case (quantity, id, qualifier) => Component.All.find(id) map (Requirement(_, quantity, qualifier))
      }
    } getOrElse Vector()
    val instructions = named find (_.header.content.stripped.toLowerCase == "instructions") map
      (s => Vector(s.content)) getOrElse Vector()
    Cocktail(doc.name, doc.description, requirements, instructions, sections, doc.links)
  }

  /**
   * A single requirement for a cocktail.
   *
   * @param component The required component.
   * @param quantity  The quantity of the component that is required.
   * @param qualifier The optional qualifying text.
   */
  case class Requirement(
    component: Component,
    quantity: Quantity,
    qualifier: Option[Content.Inline]
  )

}