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
 * @param name                 The name of this component.
 * @param description          The description of this component.
 * @param usage                The way in which this component is used.
 * @param lead                 The lead of this component if one is defined.
 * @param settings             Any settings defined on this component.
 * @param content              The markup content of this component.
 * @param links                The links specified by this component.
 * @param gallery              The gallery of images associated with this component.
 * @param staticSubtopics      The static subtopics indexed by their ID.
 * @param dynamicSubtopicIds   A function that produces the IDs of the dynamic subtopics.
 * @param dynamicSubtopicValue A function that attempts to produce a dynamic subtopic with the specified ID.
 */
case class Component(
  name: Name,
  description: Content.Inline,
  usage: Component.Usage,
  override val lead: Option[Content.Inline] = None,
  override val settings: Map[String, String] = Map.empty,
  override val content: Vector[Content.Section] = Vector.empty,
  override val links: Vector[Content.Link] = Vector.empty,
  override val gallery: Gallery = Gallery.empty
)(
  override protected val staticSubtopics: Map[Id, Topic] = Map.empty,
  override protected val dynamicSubtopicIds: () => Vector[Id] = () => Vector.empty,
  override protected val dynamicSubtopicValue: Id => Option[Topic] = _ => None
) extends Topic

/**
 * Repository for all components.
 */
object Component {

  /* Repository of all known ingredients.
  lazy val All: Category[Component] = Seq(
    "drinkware" -> Drinkware,
    "equipment" -> Equipment,
    "fruits" -> Ingredient,
    "mixers" -> Ingredient,
    "spirits" -> Ingredient,
    "wines" -> Ingredient
  ) map {
    case (path, usage) => Category[Component](Asset(s"drinks/$path")) { text =>
      val doc = Content.Document(text)
      Component(doc.name, doc.description, doc.sections, doc.links, usage)
    }
  } reduce (_ ++ _)*/

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
