/*
 * content.scala
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
 * Base class for structured content trees.
 */
sealed trait Content {

  /** Returns the text of this content without markup. */
  def stripped: String

  /**
   * Appends that content to this content.
   *
   * @param that The content to append to this content.
   * @return A concatenation of this content and that content.
   */
  def ~(that: Content): Content

}

/**
 * Mix-ins that support the content type hierarchy.
 */
object Content {

  /**
   * Mix-in for content objects that represent a single (possibly nested) unit of content.
   */
  sealed trait Singular extends Content {

    /* Concatenate this content with that content taking note of sequences. */
    final override def ~(that: Content): Content = that match {
      case Sequence(them) => Sequence(this +: them)
      case other => Sequence(this, other)
    }

  }

  /**
   * Mix-in for content objects that represent a single HTML tag.
   */
  sealed trait Tag extends Singular {

    /** The optional ID of the HTML tag. */
    def id: Option[String]

    /** The optional class list of the HTML tag. */
    def classes: Vector[String]

  }

}

/**
 * Represents a unit of content composed of text.
 *
 * @param content The content to represent.
 */
case class Text(content: String) extends Content.Singular {

  /* Return the content. */
  override def stripped: String = content

}

/**
 * Represents a unit of content wrapped in <a></a> tags with an optional ID and class list.
 *
 * @param href    The location the link points to.
 * @param content The content to wrap with a link tag.
 * @param id      The optional ID to specify on the link tag.
 * @param classes Zero-or-more classes to specify on the link tag.
 */
case class Link(href: String, content: Content, id: Option[String], classes: Vector[String]) extends Content.Tag {

  /* Return the content. */
  override def stripped: String = content.stripped

}

/**
 * Factory for link content objects.
 */
object Link {

  /**
   * Creates a new link content object.
   *
   * @param href    The location the link points to.
   * @param content The content to wrap with a link tag.
   * @param classes Zero-or-more classes to specify on the link tag.
   * @return The new link content object.
   */
  def apply(href: String, content: Content, classes: String*): Link =
    Link(href, content, None, classes.toVector)

  /**
   * Creates a new link content object.
   *
   * @param href    The location the link points to.
   * @param content The content to wrap with a link tag.
   * @param id      The optional ID to specify on the link tag.
   * @param classes Zero-or-more classes to specify on the link tag.
   * @return The new link content object.
   */
  def apply(href: String, content: Content, id: Option[String], classes: String*): Link =
    Link(href, content, id, classes.toVector)

}

/**
 * Represents an <image> tag with an optional ID and class list.
 *
 * @param src     The URL of the target image.
 * @param alt     The text to display when the image cannot be shown.
 * @param id      The optional ID to specify on the image tag.
 * @param classes Zero-or-more classes to specify on the image tag.
 */
case class Image(src: String, alt: String, id: Option[String], classes: Vector[String]) extends Content.Tag {

  /* Return the content. */
  override def stripped: String = s"($alt)"

}

/**
 * Factory for image content objects.
 */
object Image {

  /**
   * Creates a new image content object.
   *
   * @param src     The URL of the target image.
   * @param alt     The text to display when the image cannot be shown.
   * @param classes Zero-or-more classes to specify on the image tag.
   * @return The new image content object.
   */
  def apply(src: String, alt: String, classes: String*): Image =
    Image(src, alt, None, classes.toVector)

  /**
   * Creates a new image content object.
   *
   * @param src     The URL of the target image.
   * @param alt     The text to display when the image cannot be shown.
   * @param id      The optional ID to specify on the image tag.
   * @param classes Zero-or-more classes to specify on the image tag.
   * @return The new image content object.
   */
  def apply(src: String, alt: String, id: Option[String], classes: String*): Image =
    Image(src, alt, id, classes.toVector)

}

/**
 * Represents a unit of content wrapped in <cite></cite> tags with an optional ID and class list.
 *
 * @param content The content to wrap with a cite tag.
 * @param id      The optional ID to specify on the cite tag.
 * @param classes Zero-or-more classes to specify on the cite tag.
 */
case class Cite(content: Content, id: Option[String], classes: Vector[String]) extends Content.Tag {

  /* Return the content. */
  override def stripped: String = content.stripped

}

/**
 * Factory for cite content objects.
 */
object Cite {

  /**
   * Creates a new cite content object.
   *
   * @param content The content to wrap with a cite tag.
   * @param classes Zero-or-more classes to specify on the cite tag.
   * @return The new cite content object.
   */
  def apply(content: Content, classes: String*): Cite =
    Cite(content, None, classes.toVector)

  /**
   * Creates a new cite content object.
   *
   * @param content The content to wrap with a cite tag.
   * @param id      The optional ID to specify on the cite tag.
   * @param classes Zero-or-more classes to specify on the cite tag.
   * @return The new cite content object.
   */
  def apply(content: Content, id: Option[String], classes: String*): Cite =
    Cite(content, id, classes.toVector)

}

/**
 * Represents a unit of content wrapped in <q></q> tags with an optional ID and class list.
 *
 * @param cite    The URL where the content originated.
 * @param content The content to wrap with a quote tag.
 * @param id      The optional ID to specify on the quote tag.
 * @param classes Zero-or-more classes to specify on the quote tag.
 */
case class Quote(cite: Option[String], content: Content, id: Option[String], classes: Vector[String])
  extends Content.Tag {

  /* Return the content. */
  override def stripped: String = content.stripped

}

/**
 * Factory for quote content objects.
 */
object Quote {

  /**
   * Creates a new quote content object.
   *
   * @param cite    The URL where the content originated.
   * @param content The content to wrap with a quote tag.
   * @param classes Zero-or-more classes to specify on the quote tag.
   * @return The new quote content object.
   */
  def apply(cite: Option[String], content: Content, classes: String*): Quote =
    Quote(cite, content, None, classes.toVector)

  /**
   * Creates a new quote content object.
   *
   * @param cite    The URL where the content originated.
   * @param content The content to wrap with a quote tag.
   * @param id      The optional ID to specify on the quote tag.
   * @param classes Zero-or-more classes to specify on the quote tag.
   * @return The new quote content object.
   */
  def apply(cite: Option[String], content: Content, id: Option[String], classes: String*): Quote =
    Quote(cite, content, id, classes.toVector)

}

/**
 * Represents a unit of content wrapped in <blockquote></blockquote> tags with an optional ID and class list.
 *
 * @param cite    The URL where the content originated.
 * @param content The content to wrap with a quote tag.
 * @param id      The optional ID to specify on the quote tag.
 * @param classes Zero-or-more classes to specify on the quote tag.
 */
case class Blockquote(cite: Option[String], content: Content, id: Option[String], classes: Vector[String])
  extends Content.Tag {

  /* Return the content. */
  override def stripped: String = content.stripped

}

/**
 * Factory for block quote content objects.
 */
object Blockquote {

  /**
   * Creates a new blockquote content object.
   *
   * @param cite    The URL where the content originated.
   * @param content The content to wrap with a block quote tag.
   * @param classes Zero-or-more classes to specify on the block quote tag.
   * @return The new block quote content object.
   */
  def apply(cite: Option[String], content: Content, classes: String*): Blockquote =
    Blockquote(cite, content, None, classes.toVector)

  /**
   * Creates a new quote content object.
   *
   * @param cite    The URL where the content originated.
   * @param content The content to wrap with a block quote tag.
   * @param id      The optional ID to specify on the block quote tag.
   * @param classes Zero-or-more classes to specify on the block quote tag.
   * @return The new block quote content object.
   */
  def apply(cite: Option[String], content: Content, id: Option[String], classes: String*): Blockquote =
    Blockquote(cite, content, id, classes.toVector)

}

/**
 * Represents a unit of content wrapped in <span></span> tags with an optional ID and class list.
 *
 * @param content The content to wrap with a span tag.
 * @param id      The optional ID to specify on the span tag.
 * @param classes Zero-or-more classes to specify on the span tag.
 */
case class Span(content: Content, id: Option[String], classes: Vector[String]) extends Content.Tag {

  /* Return the content. */
  override def stripped: String = content.stripped

}

/**
 * Factory for span content objects.
 */
object Span {

  /**
   * Creates a new span content object.
   *
   * @param content The content to wrap with a span tag.
   * @param classes Zero-or-more classes to specify on the span tag.
   * @return The new span content object.
   */
  def apply(content: Content, classes: String*): Span =
    Span(content, None, classes.toVector)

  /**
   * Creates a new span content object.
   *
   * @param content The content to wrap with a span tag.
   * @param id      The optional ID to specify on the span tag.
   * @param classes Zero-or-more classes to specify on the span tag.
   * @return The new span content object.
   */
  def apply(content: Content, id: Option[String], classes: String*): Span =
    Span(content, id, classes.toVector)

}

/**
 * Represents an <ol> tag with an optional ID and class list.
 *
 * @param content The content to wrap with an ordered tag.
 * @param id      The optional ID to specify on the ordered tag.
 * @param classes Zero-or-more classes to specify on the ordered tag.
 */
case class Ordered(content: Vector[Item], id: Option[String], classes: Vector[String]) extends Content.Tag {

  /* Return the content. */
  override def stripped: String = ": " + content.zipWithIndex.map {
    case (item, index) => s"${index + 1} - ${item.stripped}"
  }.mkString(", ")

}

/**
 * Factory for ordered content objects.
 */
object Ordered {

  /**
   * Creates a new ordered content object.
   *
   * @param content The content to wrap with an ordered tag.
   * @param classes Zero-or-more classes to specify on the ordered tag.
   * @return The new ordered content object.
   */
  def apply(content: Vector[Item], classes: String*): Ordered =
    Ordered(content, None, classes.toVector)

  /**
   * Creates a new ordered content object.
   *
   * @param content The content to wrap with an ordered tag.
   * @param id      The optional ID to specify on the ordered tag.
   * @param classes Zero-or-more classes to specify on the ordered tag.
   * @return The new ordered content object.
   */
  def apply(content: Vector[Item], id: Option[String], classes: String*): Ordered =
    Ordered(content, id, classes.toVector)

}

/**
 * Represents an <ul> tag with an optional ID and class list.
 *
 * @param content The content to wrap with an unordered tag.
 * @param id      The optional ID to specify on the unordered tag.
 * @param classes Zero-or-more classes to specify on the unordered tag.
 */
case class Unordered(content: Vector[Item], id: Option[String], classes: Vector[String]) extends Content.Tag {

  /* Return the content. */
  override def stripped: String =
    ": " + content.map(_.stripped).mkString(", ")

}

/**
 * Factory for unordered content objects.
 */
object Unordered {

  /**
   * Creates a new unordered content object.
   *
   * @param content The content to wrap with an unordered tag.
   * @param classes Zero-or-more classes to specify on the unordered tag.
   * @return The new unordered content object.
   */
  def apply(content: Vector[Item], classes: String*): Unordered =
    Unordered(content, None, classes.toVector)

  /**
   * Creates a new unordered content object.
   *
   * @param content The content to wrap with an unordered tag.
   * @param id      The optional ID to specify on the unordered tag.
   * @param classes Zero-or-more classes to specify on the unordered tag.
   * @return The new unordered content object.
   */
  def apply(content: Vector[Item], id: Option[String], classes: String*): Unordered =
    Unordered(content, id, classes.toVector)

}

/**
 * Represents a <li> tag with an optional ID and class list.
 *
 * @param content The content to wrap with an item tag.
 * @param id      The optional ID to specify on the item tag.
 * @param classes Zero-or-more classes to specify on the item tag.
 */
case class Item(content: Content, id: Option[String], classes: Vector[String]) extends Content.Tag {

  /* Return the content. */
  override def stripped: String = content.stripped

}

/**
 * Factory for item content objects.
 */
object Item {

  /**
   * Creates a new item content object.
   *
   * @param content The content to wrap with an item tag.
   * @param classes Zero-or-more classes to specify on the item tag.
   * @return The new item content object.
   */
  def apply(content: Content, classes: String*): Item =
    Item(content, None, classes.toVector)

  /**
   * Creates a new item content object.
   *
   * @param content The content to wrap with an item tag.
   * @param id      The optional ID to specify on the item tag.
   * @param classes Zero-or-more classes to specify on the item tag.
   * @return The new item content object.
   */
  def apply(content: Content, id: Option[String], classes: String*): Item =
    Item(content, id, classes.toVector)

}

/**
 * Represents a sequence of discrete content objects that have been concatenated together.
 *
 * @param content The sequence of content objects to concatenate.
 */
case class Sequence(content: Vector[Content]) extends Content {

  /* Return the content. */
  override def stripped: String = content map (_.stripped) mkString " "

  /* Concatenate this content with that content taking note of sequences. */
  override def ~(that: Content): Content = that match {
    case Sequence(them) => Sequence(content ++ them)
    case other => Sequence(content :+ other)
  }

}

/**
 * Factory for sequence content objects.
 */
object Sequence {

  /**
   * Creates a new sequence content object.
   *
   * @param content The sequence of content objects to concatenate.
   * @return The new sequence content object.
   */
  def apply(content: Content*): Sequence =
    Sequence(content.toVector)

}
