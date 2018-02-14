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

sealed trait Ingredient {

  type IngredientType >: this.type <: Ingredient

  def name: Name

}

object Ingredient {

  trait Item extends Ingredient {

    final override type IngredientType = Item

  }

  trait Liquid extends Ingredient {

    final override type IngredientType = Liquid

  }

  trait Solid extends Ingredient {

    final override type IngredientType = Solid

  }

}