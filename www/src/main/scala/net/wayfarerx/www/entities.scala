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

  /** The name of this entity that is unique within its category. */
  def name: String = title.replaceAll("""\s+""", "-").replaceAll("""[^0-9a-zA-Z\-]+""", "").toLowerCase

  /** The title of this entity. */
  def title: String

  /** The description of this entity. */
  def description: String

  /** The image for this entity. */
  def image: Image = Image(imageLocation, imageDescription)

  /** The image for this entity. */
  def banner(width: Int): Image = Image(bannerLocation(width), imageDescription)

  /** The template used for this entity. */
  def template: String

  /** The location of this entity's image. */
  protected def imageLocation: String = s"/images/${this.id}/image.jpg"

  /** The description of this entity's image. */
  protected def imageDescription: String = description

  /** The location of this entity's image. */
  protected def bannerLocation(width: Int): String = s"/images/${this.id}/banner$width.jpg"

}

/**
 * Extensions to the entity interface.
 */
object Entity {

  /**
   * Extracts the contents of an entity.
   *
   * @param entity The entity to extract from.
   * @return The contents of the specified entity.
   */
  def unapply(entity: Entity): Option[(String, String, String, Image, String)] =
    Some(entity.name, entity.title, entity.description, entity.image, entity.template)

  /**
   * The extensions to an entity instance.
   *
   * @param entity The entity to extend.
   */
  implicit final class EntityOps(val entity: Entity) extends AnyVal {

    /** The unique ID of the entity. */
    def id: String = entity match {
      case _: Landing => ""
      case other => category map (c => s"$c/${other.name}") getOrElse other.name
    }

    /** The location that points to the entity. */
    def location: String = entity match {
      case _: Landing => "/"
      case _ => s"/$id/"
    }

    /** The category that contains the entity. */
    def category: Option[String] = entity match {
      case component: Component => Some(component.parent.id)
      case _ => None
    }

  }

}

/**
 * Base class for all entities that contain components.
 */
sealed trait Composite extends Entity {

  /** The headline that accompanies this composite. */
  def headline: Option[String] = None

  /** The components contained in this composite. */
  def components: Vector[Component] = Vector()

}

/**
 * Extensions to the composite interface.
 */
object Composite {

  /**
   * Extracts the contents of a composite.
   *
   * @param composite The composite to extract from.
   * @return The contents of the specified composite.
   */
  def unapply(composite: Composite): Option[
    (String, String, String, Image, String, Option[String], Vector[Component])] =
    Some(composite.name, composite.title, composite.description, composite.image, composite.template,
      composite.headline, composite.components)

}

/**
 * Base class for all entities that are contained in composites.
 */
sealed trait Component extends Entity {

  /** The composite that contains this component. */
  def parent: Composite

}

/**
 * Extensions to the component interface.
 */
object Component {

  /**
   * Extracts the contents of a component.
   *
   * @param component The component to extract from.
   * @return The contents of the specified component.
   */
  def unapply(component: Component): Option[(String, String, String, Image, String, Composite)] =
    Some(component.name, component.title, component.description, component.image, component.template, component.parent)

}

/**
 * Base class for the root landing page.
 */
trait Landing extends Entity {

  /** The topics contained in the website. */
  def topics: Vector[Topic]

  /* Use the landing template. */
  final override def template: String = "landing"

}

/**
 * Extensions to the landing interface.
 */
object Landing {

  /**
   * Extracts the contents of a landing.
   *
   * @param landing The landing to extract from.
   * @return The contents of the specified landing.
   */
  def unapply(landing: Landing): Option[(String, String, String, Image, String, Vector[Topic])] =
    Some(landing.name, landing.title, landing.description, landing.image, landing.template, landing.topics)

}

/**
 * Base class for top-level topic pages.
 */
trait Topic extends Composite {

  /* Use the topic template. */
  final override def template: String = "topic"

}

/**
 * Extensions to the topic interface.
 */
object Topic {

  /**
   * Extracts the contents of a topic.
   *
   * @param topic The topic to extract from.
   * @return The contents of the specified topic.
   */
  def unapply(topic: Topic): Option[(String, String, String, Image, String)] =
    Some(topic.name, topic.title, topic.description, topic.image, topic.template)

}

/**
 * Base class for nested subtopic pages.
 */
trait Subtopic extends Component with Composite {

  /* Use the topic template. */
  final override def template: String = "topic"

}

/**
 * Extensions to the subtopic interface.
 */
object Subtopic {

  /**
   * Extracts the contents of a subtopic.
   *
   * @param subtopic The subtopic to extract from.
   * @return The contents of the specified subtopic.
   */
  def unapply(subtopic: Subtopic): Option[(String, String, String, Image, String)] =
    Some(subtopic.name, subtopic.title, subtopic.description, subtopic.image, subtopic.template)

}

/**
 * Base class for individual article pages.
 */
trait Article extends Component {

  /** The headline that accompanies this article. */
  def headline: Option[String]

  /** The author of this article. */
  def author: Option[String]

  /** The content of this article. */
  def content: Vector[Content]

  /** The external content related to this article. */
  def related: Vector[Content]

  /* Use the article template. */
  final override def template: String = "article"

}

/**
 * Extensions to the article interface.
 */
object Article {

  /**
   * Extracts the contents of an article.
   *
   * @param article The article to extract from.
   * @return The contents of the specified article.
   */
  def unapply(article: Article): Option[
    (String, String, String, Image, String, Option[String], Option[String], Vector[Content], Vector[Content])] =
    Some(article.name, article.title, article.description, article.image, article.template, article.headline,
      article.author, article.content, article.related)

}
