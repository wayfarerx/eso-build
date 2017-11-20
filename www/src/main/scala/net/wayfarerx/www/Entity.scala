/*
 * Entity.scala
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

/**
 * Base class for all data objects that have a location in the site.
 */
trait Entity {

  /** The unique ID of this entity. */
  final lazy val id: String = s"$category/$name"

  /** The name of this entity that is unique within its category. */
  final lazy val name: String = title.replaceAll("""\s+""", "-").replaceAll("""[^0-9a-zA-Z\-]+""", "").toLowerCase

  /** The location that points to this entity. */
  final lazy val location: String = s"/$id/"

  /** The location that points to this entity's image. */
  final lazy val image: Option[Image] = imageDescription map (Image(s"/images/$id.jpg", _))

  /** The name of this entity. */
  def title: String

  /** The category that contains this entity. */
  def category: String

  /** The description of this entity. */
  def description: String

  /** The description of this entity's image. */
  def imageDescription: Option[String]

}
