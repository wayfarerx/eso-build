/*
 * Ingredient.scala
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
package drinks

abstract class Ingredient(parent: Composite) extends Article(parent) {

  type Measure <: Amount

  override def imageDescription: Option[String] = None

  override def author: Option[String] = None

  override def headline: Option[String] = None

  override def content: Vector[Content] = Vector(description)

  override def related: Vector[Content] = Vector()

}

object Ingredient {

}

abstract class Ingredients(parent: Composite) extends Index(parent) {

  override def imageDescription: Option[String] = None

}

object Ingredients {

}