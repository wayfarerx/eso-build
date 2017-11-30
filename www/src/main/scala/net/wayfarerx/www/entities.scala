/*
 * entities.scala
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
sealed trait Entity {

  /** The unique ID of this entity. */
  final lazy val _id: String = category map (c => s"$c/$name") getOrElse name

  /** The name of this entity that is unique within its category. */
  final lazy val _name: String = title.replaceAll("""\s+""", "-").replaceAll("""[^0-9a-zA-Z\-]+""", "").toLowerCase

  /** The location that points to this entity. */
  final lazy val _location: String = s"/$id/"

  /** The image for this entity. */
  final lazy val _image: Option[Image] = imageDescription map (Image(s"/images/$id.jpg", _))

  /** The unique ID of this entity. */
  def id: String = _id

  /** The name of this entity that is unique within its category. */
  def name: String = _name

  /** The location that points to this entity. */
  def location: String = _location

  /** The image for this entity. */
  def image: Option[Image] = _image

  /** The title of this entity. */
  def title: String

  /** The description of this entity. */
  def description: Content

  /** The description of this entity's image. */
  def imageDescription: Option[String]

  /** The category that contains this component. */
  def category: Option[String]

}

sealed trait Composite extends Entity {

  def components: Vector[Component] = Vector()

}

sealed abstract class Component(parent: Composite) extends Entity {

  /** The category that contains this component. */
  final override def category: Option[String] = Some(parent.id)

}

abstract class Landing extends Composite {

  /** The category that contains this component. */
  final override def category: Option[String] = None

}

abstract class Index(parent: Composite) extends Component(parent) with Composite {

}

abstract class Article(parent: Composite) extends Component(parent) {

  def headline: Option[String]

  def author: Option[String]

  def content: Vector[Content]

  def related: Vector[Content]

}