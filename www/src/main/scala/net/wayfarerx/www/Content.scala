/*
 * Content.scala
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

import java.net.URI

import laika.api._
import laika.parse.markdown._
import laika.tree._

/**
 * Base type for all items in the content tree.
 */
sealed trait Content {

  /** Layout type discriminator. */
  type Layout <: Content

}

/**
 * Definitions of the items in the content tree.
 */
object Content {

  //
  // Base types and categories.
  //

  /**
   * Base type for all inline items in the content tree.
   */
  sealed trait Inline extends Content {

    /* Set the layout discriminator to inline. */
    final override type Layout = Inline

  }

  /**
   * Factory for inline content.
   */
  object Inline {

    /**
     * Attempts to create inline content from the specified markdown.
     *
     * @param markdown The markdown to parse.
     * @return The parsed inline content.
     */
    private[Content] def apply(markdown: Elements.Span): Inline =
      markdown match {
        case t@Elements.Text(_, _) => Text(t)
        case wat =>
          println("WAT? --> " + wat)
          ???
      }

  }

  /**
   * Base type for all block items in the content tree.
   */
  sealed trait Block extends Content {

    /* Set the layout discriminator to block. */
    final override type Layout = Block

  }

  /**
   * Factory for block content.
   */
  object Block {

    /**
     * Attempts to create block content from the specified markdown.
     *
     * @param markdown The markdown to parse.
     * @return The parsed block content.
     */
    private[Content] def apply(markdown: Elements.Block): Block =
      markdown match {
        case Elements.Header(level, content, _) =>
          Header(level, content.toVector map Inline.apply)
        case Elements.Paragraph(content, _) =>
          Paragraph(content.toVector map Inline.apply)
        case wat =>
          println("WAT? --> " + wat)
          Paragraph(Vector())
      }

  }

  //
  // Inline implementations.
  //

  /**
   * Raw text in the document.
   *
   * @param content The raw text.
   */
  case class Text(content: String) extends Inline

  /**
   * Factory for text content.
   */
  object Text {

    /**
     * Creates text content from text markdown.
     *
     * @param markdown The markdown to convert.
     * @return Text content from text markdown.
     */
    private[Content] def apply(markdown: Elements.Text): Text =
      Text(markdown.content)

  }

  /**
   * Base type for all links.
   */
  sealed trait Link extends Inline {

    /** The type of target this link points to. */
    type Target

    /** The target this link points to. */
    def target: Target

    /** The content that defines the link. */
    def content: Vector[Inline]

  }

  /**
   * Definitions of the types of links.
   */
  object Link {

    /** The pattern that identifies external links. */
    private val LocalPattern =
      """^\#(.+)""".r

    /** The pattern that identifies external links. */
    private val ExternalPattern =
      """^([a-z-A-Z0-9\-\_]+)\:\/\/""".r

    /**
     * Creates a link from the specified target and content.
     *
     * @param target The target of the resulting link.
     * @param content The content of the resulting link.
     * @return A link from the specified target and content.
     */
    def apply(target: String, content: Vector[Inline]): Link = target match {
      case LocalPattern(id) => Local(Id(id substring 1), content)
      case ExternalPattern(uri) => External(new URI(uri), content)
      case internal => Internal(Id(internal), content)
    }

    private[Content] def apply(markdown: Elements.Link): Link = {


      ???
    }

    /**
     * A link to another location in the page.
     *
     * @param target The ID of the target within this page.
     */
    case class Local(target: Id, content: Vector[Inline]) extends Link {

      /* Target an ID. */
      override type Target = Id

    }

    /**
     * A link to another page in this site.
     *
     * @param target The ID of the target page.
     */
    case class Internal(target: Id, content: Vector[Inline]) extends Link {

      /* Target an ID. */
      override type Target = Id

    }

    /**
     * A link to a page on another site.
     *
     * @param target The URI of the target page.
     */
    case class External(target: URI, content: Vector[Inline]) extends Link {

      /* Target a URI. */
      override type Target = URI

    }

  }

  //
  // Block implementations.
  //

  /**
   * Represents a header in the document.
   *
   * @param level   The level of this header (1-6).
   * @param content The content of this header.
   */
  case class Header(level: Int, content: Vector[Inline]) extends Block

  /**
   * Represents a section in the document.
   *
   * @param header  The header of this section.
   * @param content The content of this section.
   */
  case class Section(header: Header, content: Vector[Block]) extends Block

  /**
   * Represents a paragraph in the document.
   *
   * @param content The content of this paragraph.
   */
  case class Paragraph(content: Vector[Inline]) extends Block

  //
  // Document implementation.
  //

  /**
   * The root of the content tree.
   *
   * @param name        The name of the document.
   * @param description The description of the document.
   * @param sections    The content of the document.
   * @param links       The links associated with this document.
   */
  case class Document(
    name: Name,
    description: Paragraph,
    sections: Vector[Section],
    links: Vector[Link.External]
  ) extends Content

  /**
   * Factory and parser for documents.
   */
  object Document extends {

    /**
     * Creates a document by parsing the supplied markdown.
     *
     * @param markdown The markdown to parse into a document.
     * @return The document resulting from parsing the supplied markdown.
     */
    def apply(markdown: String): Document = {
      val astName +: astDescription +: astSections = (Parse as Markdown fromString markdown).content.content.toVector
      val name = astName match {
        case Elements.Title(Seq(Elements.Text(name, _)), _) => Name(name)
        case _ => ??? : Name
      }
      val description = astDescription match {
        case Elements.Paragraph(spans, _) => Paragraph(spans.toVector map convertInline)
        case _ => ??? : Paragraph
      }
      val (links, sections) = astSections map {
        case Elements.Section(Elements.Header(level, header, _), content, _) =>
          Section(Header(level, header.toVector map convertInline), content.toVector map convertBlock)
        case _ => ??? : Section
      } partition {
        case Section(Header(2, Vector(Text("Links"))), _) => true
        case _ => false
      }
      println(links)
      Document(name, description, sections, ???)
    }

    private def convertBlock(block: Elements.Block): Block =
      Block(block)

    private def convertInline(span: Elements.Span): Inline =
      Inline(span)

  }

}