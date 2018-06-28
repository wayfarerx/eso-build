/*
 * Markup.scala
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
package model

import language.implicitConversions

/**
 * Base type for representations of paragraph-level marked-up text.
 */
sealed trait Markup extends Styled {

  /** The underlying text stripped up markup. */
  def strip: String

}

/**
 * Definitions of the supported markup elements.
 */
object Markup {

  /** The empty markup. */
  val empty: Markup = Sequence(Vector.empty)

  /** Implicitly support strings as text. */
  implicit def stringAsMarkup(str: String): Markup = apply(str)

  /**
   * Creates text markup from the specified string.
   *
   * @param text The string to use as text.
   * @return The text markup.
   */
  def apply(text: String): Markup =
    if (text.isEmpty) empty else Text(text)

  /**
   * Creates a composite markup from the specified markup.
   *
   * @param markup The markup to compose.
   * @return The composite markup.
   */
  def apply(markup: Markup*): Markup =
    markup.toVector filterNot (_ == empty) match {
      case nested if nested.nonEmpty => Sequence(nested flatMap {
        case s@Single() => Vector(s)
        case Sequence(m) => m
      })
      case _ => empty
    }

  /**
   * Base type for singular markup items.
   */
  sealed trait Single extends Markup

  object Single {

    def unapply(single: Single): Boolean = true

  }

  /**
   * Basic text.
   *
   * @param text The text.
   */
  case class Text private[Markup](
    text: String
  ) extends Single {

    /* Sequences have no ID. */
    override def id: Option[String] = None

    /* Sequences have no classes. */
    override def classes: Vector[String] = Vector.empty

    /* Return the content. */
    override def strip: String = text

  }

  /**
   * Wrapper around inline text.
   *
   * @param nested  The content that is wrapped.
   * @param title  The title of this span.
   * @param id      The style ID of this markup.
   * @param classes The style classes assigned to this markup.
   */
  case class Span(
    nested: Markup,
    title: Option[String] = None,
    id: Option[String] = None,
    classes: Vector[String] = Vector.empty
  ) extends Single {

    /* Strip the content. */
    override def strip: String = nested.strip

  }

  /**
   * Makes text appear italic.
   *
   * @param nested  The content that is italic.
   * @param id      The style ID of this markup.
   * @param classes The style classes assigned to this markup.
   */
  case class Emphasis(
    nested: Markup,
    id: Option[String] = None,
    classes: Vector[String] = Vector.empty
  ) extends Single {

    /* Strip the content. */
    override def strip: String = nested.strip

  }

  /**
   * Makes text appear bold.
   *
   * @param nested  The content that is bold.
   * @param id      The style ID of this markup.
   * @param classes The style classes assigned to this markup.
   */
  case class Strong(
    nested: Markup,
    id: Option[String] = None,
    classes: Vector[String] = Vector.empty
  ) extends Single {

    /* Strip the content. */
    override def strip: String = nested.strip

  }

  /**
   * A delineated block of markup.
   *
   * @param nested  The content that is delineated.
   * @param id      The style ID of this markup.
   * @param classes The style classes assigned to this markup.
   */
  case class Paragraph(
    nested: Markup,
    id: Option[String] = None,
    classes: Vector[String] = Vector.empty
  ) extends Single {

    /* Strip the content. */
    override def strip: String = nested.strip

  }

  /**
   * A figure with an optional caption.
   *
   * @param image   The image to display.
   * @param caption The caption for the figure.
   * @param id      The style ID of this markup.
   * @param classes The style classes assigned to this markup.
   */
  case class Figure(
    image: Asset.Image.Single,
    caption: Markup,
    id: Option[String] = None,
    classes: Vector[String] = Vector.empty
  ) extends Single {

    /* Strip the content. */
    override def strip: String = caption.strip

  }

  /**
   * Base type for links.
   */
  sealed trait Link extends Single {

    /** The title of this link. */
    def title: Option[String]

    /** The content of this link. */
    def nested: Markup

    /* Strip the content. */
    final override def strip: String = nested.strip

  }

  /**
   * Definitions of the supported link types.
   */
  object Link {

    /**
     * Links text to somewhere inside the page.
     *
     * @param name    The name targeted by this link.
     * @param title   The title of this link.
     * @param nested  The content of this link.
     * @param id      The style ID of this markup.
     * @param classes The style classes assigned to this markup.
     */
    case class Local(
      name: Name,
      title: Option[String],
      nested: Markup,
      id: Option[String] = None,
      classes: Vector[String] = Vector.empty
    ) extends Link

    /**
     * Links text to somewhere inside the site.
     *
     * @tparam T The type of referenced page.
     * @param pointer  The pointer that links to the referenced page.
     * @param fragment The fragment at the end of this link.
     * @param title    The title of this link.
     * @param nested   The content of this link.
     * @param id       The style ID of this markup.
     * @param classes  The style classes assigned to this markup.
     */
    case class Internal[T <: AnyRef](
      pointer: Pointer[T],
      fragment: Option[Name],
      title: Option[String],
      nested: Markup,
      id: Option[String] = None,
      classes: Vector[String] = Vector.empty
    ) extends Link

    /**
     * Links text to a fully resolved location.
     *
     * @param href    The target to link to.
     * @param title   The title of this link.
     * @param nested  The content of this link.
     * @param id      The style ID of this markup.
     * @param classes The style classes assigned to this markup.
     */
    case class Resolved(
      href: String,
      title: Option[String],
      nested: Markup,
      id: Option[String] = None,
      classes: Vector[String] = Vector.empty
    ) extends Link

  }

  /**
   * Base type for lists.
   */
  sealed trait List extends Single {

    /** The items in this list. */
    def items: Vector[List.Item]

    /* Strip the content. */
    final override def strip: String = items map (_.strip) match {
      case Vector() => ""
      case Vector(single) => single
      case init :+ tail => init.mkString(", ") + s" and $tail"
    }

  }

  /**
   * Definitions associated with links.
   */
  object List {

    /**
     * Extracts a list.
     *
     * @param list The list to extract.
     * @return The contained items.
     */
    def unapply(list: List): Option[Vector[Item]] =
      Some(list.items)

    /**
     * An ordered list of items.
     *
     * @param items   The items in this list.
     * @param id      The style ID of this markup.
     * @param classes The style classes assigned to this markup.
     */
    case class Ordered(
      items: Vector[Item],
      id: Option[String] = None,
      classes: Vector[String] = Vector.empty
    ) extends List

    /**
     * An unordered list of items.
     *
     * @param items   The items in this list.
     * @param id      The style ID of this markup.
     * @param classes The style classes assigned to this markup.
     */
    case class Unordered(
      items: Vector[Item],
      id: Option[String] = None,
      classes: Vector[String] = Vector.empty
    ) extends List

    /**
     * An item in a list.
     *
     * @param nested  The content of this item.
     * @param id      The style ID of this markup.
     * @param classes The style classes assigned to this markup.
     */
    case class Item(
      nested: Markup,
      id: Option[String] = None,
      classes: Vector[String] = Vector.empty
    ) extends Single {

      /* Strip the content. */
      final override def strip: String = nested.strip

    }

  }

  /**
   * A sequence of markup.
   *
   * @param markup The sequenced markup.
   */
  case class Sequence private[Markup](markup: Vector[Single]) extends Markup {

    /* Sequences have no ID. */
    override def id: Option[String] = None

    /* Sequences have no classes. */
    override def classes: Vector[String] = Vector.empty

    /* Strip and join the content. */
    override def strip: String = markup.map(_.strip).mkString

  }

}
