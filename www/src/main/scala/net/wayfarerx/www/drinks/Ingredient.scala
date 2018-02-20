/*
 * Ingredient.scala
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
 * Definition of an ingredient in a cocktail.
 *
 * @param name The name of this ingredient.
 */
case class Ingredient(
  name: Name
)

/**
 * Factory and repository for all ingredients.
 */
object Ingredient {

  /** Repository of all known ingredients. */
  lazy val Ingredients: Index[Ingredient] =
    Index[Ingredient]("drinks/ingredients") { text =>
      val doc = Content.Document(text)
      Ingredient(doc.name)
    }

}