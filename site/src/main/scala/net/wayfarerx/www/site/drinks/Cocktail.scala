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
package site.drinks

import model._

import scala.util.Try

/**
 * Represents a cocktail recipe and information.
 *
 * @param name        The name of this cocktail.
 * @param description The description of this cocktail.
 * @param information The information about this cocktail.
 * @param recipe      The recipe for this cocktail.
 * @param sections    The sections pertaining to this cocktail.
 */
case class Cocktail(
  name: Name,
  description: Markup,
  information: Markup,
  recipe: Recipe,
  sections: Vector[Section] = Vector()
)

/**
 * Definitions associated with cocktails.
 */
object Cocktail {

  /** The cocktail entity. */
  implicit val Entity: Entity[Cocktail] = new Entity[Cocktail] {

    /* Return the description of this entity. */
    override def description(context: Context, entity: Cocktail): Markup =
      entity.description

    /* Attempt to encode a document from an entity of the underlying type. */
    override def encode(context: Context, entity: Cocktail): Try[Document] =
      Recipe.encode(context, entity.recipe) map { sections =>
        Document(entity.name, None, entity.description, entity.information, sections ++: entity.sections)
      }

    /* Attempt to decode a document into an entity of the underlying type. */
    override def decode(context: Context, document: Document): Try[Cocktail] =
      Recipe.decode(document.sections) map { case (recipe, article) =>
        Cocktail(document.name, document.description, document.content, recipe, article)
      }

  }

}
