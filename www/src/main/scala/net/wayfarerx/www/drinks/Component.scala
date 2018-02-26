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
 */
sealed trait Component {

  /** The name of this component. */
  def name: Name

  /** The simple description of this component. */
  def description: Content.Paragraph

  /** The simple description of this component. */
  def sections: Vector[Content.Section]

  /** The links specified by this component. */
  def links: Vector[Content.Link]

}

/**
 * Factory and repository for all ingredients.
 */
object Component {

  /** Repository of all known ingredients. */
  lazy val All: Index[Component] =
    Drinkware.All ++ Ingredient.All ++ Tool.All

  case class Drinkware(
    name: Name,
    description: Content.Paragraph,
    sections: Vector[Content.Section],
    links: Vector[Content.Link]
  ) extends Component

  object Drinkware {

    lazy val All: Index[Drinkware] =
      Index[Drinkware](s"drinks/drinkware") { text =>
        val doc = Content.Document(text)
        Drinkware(doc.name, doc.description, doc.sections, doc.links)
      }

  }

  case class Ingredient(
    name: Name,
    description: Content.Paragraph,
    sections: Vector[Content.Section],
    links: Vector[Content.Link]
  ) extends Component

  object Ingredient {

    lazy val All: Index[Ingredient] = Seq(
      "fruits",
      "mixers",
      "spirits",
      "wines"
    ) map { name =>
      Index[Ingredient](s"drinks/$name") { text =>
        val doc = Content.Document(text)
        Ingredient(doc.name, doc.description, doc.sections, doc.links)
      }
    } reduce (_ ++ _)

  }

  case class Tool(
    name: Name,
    description: Content.Paragraph,
    sections: Vector[Content.Section],
    links: Vector[Content.Link]
  ) extends Component

  object Tool {

    lazy val All: Index[Tool] =
      Index[Tool](s"drinks/tools") { text =>
        val doc = Content.Document(text)
        Tool(doc.name, doc.description, doc.sections, doc.links)
      }

  }

}