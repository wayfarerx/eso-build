/*
 * Component.scala
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
 * Definition of a component of a cocktail.
 *
 * @param name        The name of this component.
 * @param description The simple description of this component.
 * @param sections    The sections of this component.
 * @param links       The links specified by this component.
 */
case class Component(
  name: Name,
  description: Content.Paragraph,
  sections: Vector[Content.Section],
  links: Vector[Content.Link],
  usage: Component.Usage
)

/**
 * Repository for all components.
 */
object Component {

  /** Repository of all known ingredients. */
  lazy val All: Index[Component] = Seq(
    "drinkware" -> Drinkware,
    "equipment" -> Equipment,
    "fruits" -> Ingredient,
    "mixers" -> Ingredient,
    "spirits" -> Ingredient,
    "wines" -> Ingredient
  ) map {
    case (path, usage) => Index[Component](s"drinks/$path") { text =>
      val doc = Content.Document(text)
      Component(doc.name, doc.description, doc.sections, doc.links, usage)
    }
  } reduce (_ ++ _)

  /**
   * Base type for the usage categories.
   */
  sealed trait Usage

  /**
   * Signifies that the component is drinkware served with a drink.
   */
  case object Drinkware extends Usage

  /**
   * Signifies that the component is equipment used to construct a drink.
   */
  case object Equipment extends Usage

  /**
   * Signifies that the component is an ingredient used in a drink.
   */
  case object Ingredient extends Usage

}
