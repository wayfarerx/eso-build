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

    /**
     * Returns a copy of this tag with the specified ID.
     *
     * @param id The ID of the resulting tag.
     * @return A copy of this tag with the specified ID.
     */
    def withId(id: Option[String]): Tag

  }

}

/**
 * Represents a unit of content composed of text.
 *
 * @param content The content to represent.
 */
case class Text(content: String) extends Content.Singular

/**
 * Represents a unit of content wrapped in <em></em> tags with an optional ID and class list.
 *
 * @param content The content to wrap with an emphasis tag.
 * @param id      The optional ID to specify on the emphasis tag.
 * @param classes Zero-or-more classes to specify on the emphasis tag.
 */
case class Emphasis(content: Content, id: Option[String], classes: Vector[String]) extends Content.Tag {
  def withId(id: Option[String]): Emphasis = copy(id = id)
}

/**
 * Factory for emphasis content objects.
 */
object Emphasis {

  /**
   * Creates a new emphasis content object.
   *
   * @param content The content to wrap with a emphasis tag.
   * @param classes Zero-or-more classes to specify on the emphasis tag.
   * @return The new emphasis content object.
   */
  def apply(content: Content, classes: String*): Emphasis =
    Emphasis(content, None, classes.toVector)

  /**
   * Creates a new emphasis content object.
   *
   * @param content The content to wrap with an emphasis tag.
   * @param id      The optional ID to specify on the emphasis tag.
   * @param classes Zero-or-more classes to specify on the emphasis tag.
   * @return The new emphasis content object.
   */
  def apply(content: Content, id: Option[String], classes: String*): Emphasis =
    Emphasis(content, id, classes.toVector)

}

/**
 * Represents a unit of content wrapped in <strong></strong> tags with an optional ID and class list.
 *
 * @param content The content to wrap with a strong tag.
 * @param id      The optional ID to specify on the strong tag.
 * @param classes Zero-or-more classes to specify on the strong tag.
 */
case class Strong(content: Content, id: Option[String], classes: Vector[String]) extends Content.Tag {
  def withId(id: Option[String]): Strong = copy(id = id)
}

/**
 * Factory for strong content objects.
 */
object Strong {

  /**
   * Creates a new strong content object.
   *
   * @param content The content to wrap with a strong tag.
   * @param classes Zero-or-more classes to specify on the strong tag.
   * @return The new strong content object.
   */
  def apply(content: Content, classes: String*): Strong =
    Strong(content, None, classes.toVector)

  /**
   * Creates a new emphasis content object.
   *
   * @param content The content to wrap with a emphasis tag.
   * @param id      The optional ID to specify on the emphasis tag.
   * @param classes Zero-or-more classes to specify on the emphasis tag.
   * @return The new emphasis content object.
   */
  def apply(content: Content, id: Option[String], classes: String*): Emphasis =
    Emphasis(content, id, classes.toVector)

}

/**
 * Represents a unit of content wrapped in <p></p> tags with an optional ID and class list.
 *
 * @param content The content to wrap with an paragraph tag.
 * @param id      The optional ID to specify on the paragraph tag.
 * @param classes Zero-or-more classes to specify on the paragraph tag.
 */
case class Paragraph(content: Content, id: Option[String], classes: Vector[String]) extends Content.Tag {
  def withId(id: Option[String]): Paragraph = copy(id = id)
}

/**
 * Factory for paragraph content objects.
 */
object Paragraph {

  /**
   * Creates a new paragraph content object.
   *
   * @param content The content to wrap with a paragraph tag.
   * @param classes Zero-or-more classes to specify on the paragraph tag.
   * @return The new paragraph content object.
   */
  def apply(content: Content, classes: String*): Paragraph =
    Paragraph(content, None, classes.toVector)

  /**
   * Creates a new paragraph content object.
   *
   * @param content The content to wrap with a paragraph tag.
   * @param id      The optional ID to specify on the paragraph tag.
   * @param classes Zero-or-more classes to specify on the paragraph tag.
   * @return The new paragraph content object.
   */
  def apply(content: Content, id: Option[String], classes: String*): Paragraph =
    Paragraph(content, id, classes.toVector)

}

/**
 * Represents a unit of content wrapped in <div></div> tags with an optional ID and class list.
 *
 * @param content The content to wrap with a div tag.
 * @param id      The optional ID to specify on the div tag.
 * @param classes Zero-or-more classes to specify on the div tag.
 */
case class Div(content: Content, id: Option[String], classes: Vector[String]) extends Content.Tag {
  def withId(id: Option[String]): Div = copy(id = id)
}

/**
 * Factory for div content objects.
 */
object Div {

  /**
   * Creates a new div content object.
   *
   * @param content The content to wrap with a div tag.
   * @param classes Zero-or-more classes to specify on the div tag.
   * @return The new div content object.
   */
  def apply(content: Content, classes: String*): Div =
    Div(content, None, classes.toVector)

  /**
   * Creates a new div content object.
   *
   * @param content The content to wrap with a div tag.
   * @param id      The optional ID to specify on the div tag.
   * @param classes Zero-or-more classes to specify on the div tag.
   * @return The new div content object.
   */
  def apply(content: Content, id: Option[String], classes: String*): Div =
    Div(content, id, classes.toVector)

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
  def withId(id: Option[String]): Link = copy(id = id)
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
  def withId(id: Option[String]): Image = copy(id = id)
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
 * Represents a <hr> tag with an optional ID and class list.
 *
 * @param id      The optional ID to specify on the hr tag.
 * @param classes Zero-or-more classes to specify on the hr tag.
 */
case class HorizontalRule(id: Option[String], classes: Vector[String]) extends Content.Tag {
  def withId(id: Option[String]): HorizontalRule = copy(id = id)
}

/**
 * Factory for hr content objects.
 */
object HorizontalRule {

  /**
   * Creates a new hr content object.
   *
   * @param classes Zero-or-more classes to specify on the hr tag.
   * @return The new hr content object.
   */
  def apply(classes: String*): HorizontalRule =
    HorizontalRule(None, classes.toVector)

  /**
   * Creates a new hr content object.
   *
   * @param id      The optional ID to specify on the hr tag.
   * @param classes Zero-or-more classes to specify on the hr tag.
   * @return The new hr content object.
   */
  def apply(id: Option[String], classes: String*): HorizontalRule =
    HorizontalRule(id, classes.toVector)

}

/**
 * Represents a unit of content wrapped in <cite></cite> tags with an optional ID and class list.
 *
 * @param content The content to wrap with a cite tag.
 * @param id      The optional ID to specify on the cite tag.
 * @param classes Zero-or-more classes to specify on the cite tag.
 */
case class Cite(content: Content, id: Option[String], classes: Vector[String]) extends Content.Tag {
  def withId(id: Option[String]): Cite = copy(id = id)
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
  def withId(id: Option[String]): Quote = copy(id = id)
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
  def withId(id: Option[String]): Blockquote = copy(id = id)
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
  def withId(id: Option[String]): Span = copy(id = id)
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
  def withId(id: Option[String]): Ordered = copy(id = id)
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
  def withId(id: Option[String]): Unordered = copy(id = id)
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
  def withId(id: Option[String]): Item = copy(id = id)
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

/**
 * Represents a unit of content wrapped in <section></section> tags with an optional ID and class list.
 *
 * @param content The content to wrap with a section tag.
 * @param id      The optional ID to specify on the section tag.
 * @param classes Zero-or-more classes to specify on the section tag.
 */
case class Section(content: Content, id: Option[String], classes: Vector[String]) extends Content.Tag {
  def withId(id: Option[String]): Section = copy(id = id)
}

/**
 * Factory for section content objects.
 */
object Section {

  /**
   * Creates a new section content object.
   *
   * @param content The content to wrap with a section tag.
   * @param classes Zero-or-more classes to specify on the section tag.
   * @return The new section content object.
   */
  def apply(content: Content, classes: String*): Section =
    Section(content, None, classes.toVector)

  /**
   * Creates a new section content object.
   *
   * @param content The content to wrap with a section tag.
   * @param id      The optional ID to specify on the section tag.
   * @param classes Zero-or-more classes to specify on the section tag.
   * @return The new section content object.
   */
  def apply(content: Content, id: Option[String], classes: String*): Section =
    Section(content, id, classes.toVector)

}

/**
 * Represents a unit of content wrapped in <header></header> tags with an optional ID and class list.
 *
 * @param content The content to wrap with a header tag.
 * @param id      The optional ID to specify on the header tag.
 * @param classes Zero-or-more classes to specify on the header tag.
 */
case class Header(content: Content, id: Option[String], classes: Vector[String]) extends Content.Tag {
  def withId(id: Option[String]): Header = copy(id = id)
}

/**
 * Factory for header content objects.
 */
object Header {

  /**
   * Creates a new header content object.
   *
   * @param content The content to wrap with a header tag.
   * @param classes Zero-or-more classes to specify on the header tag.
   * @return The new header content object.
   */
  def apply(content: Content, classes: String*): Header =
    Header(content, None, classes.toVector)

  /**
   * Creates a new header content object.
   *
   * @param content The content to wrap with a header tag.
   * @param id      The optional ID to specify on the header tag.
   * @param classes Zero-or-more classes to specify on the header tag.
   * @return The new header content object.
   */
  def apply(content: Content, id: Option[String], classes: String*): Header =
    Header(content, id, classes.toVector)

}

/**
 * Represents a unit of content wrapped in <footer></footer> tags with an optional ID and class list.
 *
 * @param content The content to wrap with a footer tag.
 * @param id      The optional ID to specify on the footer tag.
 * @param classes Zero-or-more classes to specify on the footer tag.
 */
case class Footer(content: Content, id: Option[String], classes: Vector[String]) extends Content.Tag {
  def withId(id: Option[String]): Footer = copy(id = id)
}

/**
 * Factory for footer content objects.
 */
object Footer {

  /**
   * Creates a new footer content object.
   *
   * @param content The content to wrap with a footer tag.
   * @param classes Zero-or-more classes to specify on the footer tag.
   * @return The new footer content object.
   */
  def apply(content: Content, classes: String*): Footer =
    Footer(content, None, classes.toVector)

  /**
   * Creates a new footer content object.
   *
   * @param content The content to wrap with a footer tag.
   * @param id      The optional ID to specify on the footer tag.
   * @param classes Zero-or-more classes to specify on the footer tag.
   * @return The new footer content object.
   */
  def apply(content: Content, id: Option[String], classes: String*): Footer =
    Footer(content, id, classes.toVector)

}

/**
 * Represents a unit of content wrapped in <h*></h*> tags with an optional ID and class list.
 *
 * @param level The heading level to use.
 * @param content The content to wrap with a heading tag.
 * @param id      The optional ID to specify on the heading tag.
 * @param classes Zero-or-more classes to specify on the heading tag.
 */
case class Heading(level: Int, content: Content, id: Option[String], classes: Vector[String]) extends Content.Tag {
  def withId(id: Option[String]): Heading = copy(id = id)
}

/**
 * Factory for heading content objects.
 */
object Heading {

  /**
   * Creates a new heading content object.
   *
   * @param level The heading level to use.
   * @param content The content to wrap with a heading tag.
   * @param classes Zero-or-more classes to specify on the heading tag.
   * @return The new heading content object.
   */
  def apply(level: Int, content: Content, classes: String*): Heading =
    Heading(level, content, None, classes.toVector)

  /**
   * Creates a new heading content object.
   *
   * @param level The heading level to use.
   * @param content The content to wrap with a heading tag.
   * @param id      The optional ID to specify on the heading tag.
   * @param classes Zero-or-more classes to specify on the heading tag.
   * @return The new heading content object.
   */
  def apply(level: Int, content: Content, id: Option[String], classes: String*): Heading =
    Heading(level, content, id, classes.toVector)

}
