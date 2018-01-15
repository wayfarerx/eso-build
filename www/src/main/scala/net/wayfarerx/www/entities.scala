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
 * Base class for all objects that have a location in the site.
 */
sealed trait Entity {

  import Entity._

  /** The layout used for this entity. */
  def layout: String

  /** The style of this entity. */
  def style: Option[String] = None

  /** The name of this entity that is unique within its category. */
  def name: String = displayName.replaceAll("""\s+""", "-").replaceAll("""[^0-9a-zA-Z\-]+""", "").toLowerCase

  /** The title of this entity. */
  def displayName: String

  /** The title of this entity. */
  def title: String

  /** The description of this entity. */
  def description: String

  /** The image for this entity. */
  def image: Image =
    Image(s"/images${this.location}image.jpg", imageDescription)

  /** The image for this entity. */
  def banner(banner: Banner): Image =
    Image(s"/images${this.location}banner-${banner.toString.toLowerCase()}.jpg", bannerDescription)

  /** The initial content of this entity. */
  def lead: Content = Paragraph(description)

  /** The footer that accompanies this composite. */
  def footer: Option[Content] = None

  /** The description for this entity's image. */
  protected def imageDescription: String = description

  /** The description for this entity's banner. */
  protected def bannerDescription: String = description

  override def toString: String = displayName

}

/**
 * Extensions to the entity interface.
 */
object Entity {

  /**
   * The extensions to an entity instance.
   *
   * @param entity The entity to extend.
   */
  implicit final class EntityOps(val entity: Entity) extends AnyVal {

    /** The unique ID of the entity. */
    def path: String = entity match {
      case _: Home => ""
      case other => category map (c => s"$c/${other.name}") getOrElse other.name
    }

    /** The location that points to the entity. */
    def location: String = entity match {
      case _: Home => "/"
      case _ => s"/$path/"
    }

    /** The category that contains the entity. */
    def category: Option[String] = entity match {
      case component: Component => component.parent map (_.path)
      case _ => None
    }

  }

  /**
   * Base class for the different banner resolutions.
   */
  sealed trait Banner

  /**
   * Banner implementations.
   */
  object Banner {

    /** Returns all the banners from smallest to largest. */
    def all: Vector[Banner] = Vector(Large, Medium, Small)

    /** The small banner. */
    case object Small extends Banner

    /** The medium banner. */
    case object Medium extends Banner

    /** The large banner. */
    case object Large extends Banner

  }

}

/**
 * Base class for all entities that are contained in composites.
 */
sealed trait Component extends Entity {

  /** The composite that contains this component. */
  def parent: Option[Composite] = None

}

/**
 * Base class for all entities that contain components.
 */
sealed trait Composite extends Entity {

  /** The components contained in this composite. */
  def children: Vector[Component] = Vector()

}

/**
 * Base class for the root home page.
 */
trait Home extends Composite {

  /* Use the home template. */
  final override def layout: String = "home"

}

/**
 * Base class for top-level topic pages.
 */
trait Topic extends Component with Composite {

  /* Use the topic template. */
  final override def layout: String = "topic"

}

/**
 * Base class for individual article pages.
 */
trait Article extends Component {

  /** The headline that accompanies this article. */
  def headline: Option[String] = None

  /** The author of this article. */
  def author: Option[String] = None

  /** The content of this article. */
  def content: Vector[Content]

  /* Use the article template. */
  final override def layout: String = "article"

}
