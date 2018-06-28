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
package site.drinks

import util.{Success, Try}

import model._

/**
 * Base type for items that are used in cocktails and mixers.
 */
sealed trait Component {

  /** The name of this component. */
  def name: Name

  /** The description of this component. */
  def description: Markup

  /** The information about this component. */
  def information: Markup

  /** The article about this component. */
  def sections: Vector[Section]

}

/**
 * Definitions of the supported components.
 */
object Component {

  /**
   * Base type for components that are simple topics.
   */
  sealed trait TopicSupport extends Component with Topic {

    /* The name of this topic. */
    override def name: Name = document.name

    /* The description of this topic. */
    override def description: Markup = document.description

    /* The information about this topic. */
    override def information: Markup = document.content

    /* The article about this topic. */
    override def sections: Vector[Section] = document.sections

  }

  /**
   * Inedible glassware and garnish elements served with a cocktail.
   *
   * @param document The content of this topic.
   */
  case class Drinkware(document: Document) extends TopicSupport

  /**
   * Definitions associated with drinkware.
   */
  object Drinkware {

    /** The decoder for drinkware. */
    implicit val Entity: Entity[Drinkware] = Topic.entity(Drinkware(_))

  }

  /**
   * Tools used to assemble a cocktail but not served with it.
   *
   * @param document The content of this topic.
   */
  case class Equipment(document: Document) extends TopicSupport

  /**
   * Definitions associated with equipment.
   */
  object Equipment {

    /** The decoder for equipment. */
    implicit val Entity: Entity[Equipment] = Topic.entity(Equipment(_))

  }

  /**
   * Fruit and fruit products used in cocktails.
   *
   * @param document The content of this topic.
   */
  case class Fruit(document: Document) extends TopicSupport

  /**
   * Definitions associated with fruits.
   */
  object Fruit {

    /** The decoder for fruits. */
    implicit val Entity: Entity[Fruit] = Topic.entity(Fruit(_))

  }

  /**
   * Distilled spirits used in cocktails.
   *
   * @param document The content of this topic.
   */
  case class Spirit(document: Document) extends TopicSupport

  /**
   * Definitions associated with spirits.
   */
  object Spirit {

    /** The decoder for spirits. */
    implicit val Entity: Entity[Spirit] = Topic.entity(Spirit(_))

  }

  /**
   * Fermented wines used in cocktails.
   *
   * @param document The content of this topic.
   */
  case class Wine(document: Document) extends TopicSupport

  /**
   * Definitions associated with wines.
   */
  object Wine {

    /** The decoder for wines. */
    implicit val Entity: Entity[Wine] = Topic.entity(Wine(_))

  }

  /**
   *
   * @param name        The name of this mixer.
   * @param description The description of this mixer.
   * @param information The information about this mixer.
   * @param recipe      The recipe for this mixer if available.
   * @param sections    The sections pertaining to this mixer.
   */
  case class Mixer(
    name: Name,
    description: Markup,
    information: Markup,
    recipe: Option[Recipe],
    sections: Vector[Section] = Vector()
  ) extends Component

  /**
   * Definitions associated with mixers.
   */
  object Mixer {

    /** The cocktail entity. */
    implicit val Entity: Entity[Mixer] = new Entity[Mixer] {

      /* Return the description of this entity. */
      override def description(context: Context, entity: Mixer): Markup =
        entity.description

      /* Attempt to encode a document from an entity of the underlying type. */
      override def encode(context: Context, entity: Mixer): Try[Document] =
        entity.recipe.map(Recipe.encode(context, _)).getOrElse(Success(Vector.empty)) map { sections =>
          Document(entity.name, None, entity.description, entity.information, sections ++: entity.sections)
        }

      /* Attempt to decode a document into an entity of the underlying type. */
      override def decode(context: Context, document: Document): Try[Mixer] =
        Recipe.decode(document.sections) map { case (recipe, sections) =>
          Mixer(document.name, document.description, document.content, Some(recipe), sections)
        } recover { case _: Recipe.DecoderException.NotFound =>
          Mixer(document.name, document.description, document.content, None, document.sections)
        }

    }

  }

}
