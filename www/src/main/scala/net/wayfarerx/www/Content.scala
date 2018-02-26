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

  /**
   * Returns the words contained in this content.
   *
   * @return The words contained in this content.
   */
  def strip: Vector[String]

  /* Strip the formatting and convert this content into text. */
  final override def toString: String = strip map (_.trim) filterNot (_.isEmpty) mkString Content.Space

}

/**
 * Definitions of the items in the content tree.
 */
object Content {

  /** The new line token. */
  private val Space = " "

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
        case text@Elements.Text(_, _) => Text(text)
        case link: Elements.Link => Link(link)
        case wat =>
          println("WAT? --> " + wat + new Exception().getStackTrace)
          Text()
      }

    /**
     * Represents a group of inline content.
     *
     * @param members The members of this group.
     */
    case class Group(members: Vector[Inline]) extends Inline {

      /* Strip all members. */
      override def strip: Vector[String] = members flatMap (_.strip)

    }

    /**
     * Factory for inline groups.
     */
    object Group {

      /** The empty group. */
      val Empty: Group = Group(Vector.empty)

      /**
       * Creates an inline group with the specified members.
       *
       * @param members The members of the resulting group.
       * @return An inline with the specified members.
       */
      def apply(members: Inline*): Inline = {

        def flatten(member: Inline): Vector[Inline] = member match {
          case Group(next) => next flatMap flatten
          case single => Vector(single)
        }

        members.toVector flatMap flatten match {
          case Vector() => Empty
          case Vector(single) => single
          case multiple => Group(multiple)
        }
      }

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
        case header@Elements.Header(_, _, _) => Header(header)
        case paragraph@Elements.Paragraph(_, _) => Paragraph(paragraph)
        case list@Elements.BulletList(_, _, _) => List(list)
        case wat =>
          println("WAT? --> " + wat)
          Paragraph(Inline.Group())
      }

    /**
     * Represents a group of block content.
     *
     * @param members The members of this group.
     */
    case class Group(members: Vector[Block]) extends Block {

      /* Strip all members. */
      override def strip: Vector[String] = members flatMap (_.strip)

    }

    /**
     * Factory for block groups.
     */
    object Group {

      /** The empty group. */
      val Empty: Group = Group(Vector.empty)

      /**
       * Creates a block group with the specified members.
       *
       * @param members The members of the resulting group.
       * @return A block with the specified members.
       */
      def apply(members: Block*): Block = {

        def flatten(member: Block): Vector[Block] = member match {
          case Group(next) => next flatMap flatten
          case single => Vector(single)
        }

        members.toVector flatMap flatten match {
          case Vector() => Empty
          case Vector(single) => single
          case multiple => Group(multiple)
        }
      }

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
  case class Text(content: String = "") extends Inline {

    /* Return this. */
    override def strip: Vector[String] = content
      .replaceAll("""[^\p{Alnum}\s]+""", "")
      .trim.split("""[\s]+""")
      .filterNot(_.isEmpty).toVector
  }

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

    /** The optional title of this link. */
    def title: Option[String]

    /** The content that defines the link. */
    def content: Inline

    /* Strip all members. */
    final override def strip: Vector[String] = content.strip

  }

  /**
   * Factory for and definitions of the types of links.
   */
  object Link {

    /** The pattern that identifies local links. */
    private val LocalPattern =
      """^\#(.+)""".r

    /** The pattern that identifies external links. */
    private val ExternalPattern =
      """^([a-z-A-Z0-9\-\_]+\:\/\/.+)""".r

    /**
     * Creates a link from the specified target and content.
     *
     * @param target  The target of the resulting link.
     * @param content The content of the resulting link.
     * @return A link from the specified target and content.
     */
    def apply(target: String, content: Inline*): Link =
      apply(target, None, content: _*)

    /**
     * Creates a link from the specified target, title and content.
     *
     * @param target  The target of the resulting link.
     * @param title   The title of resulting link.
     * @param content The content of the resulting link.
     * @return A link from the specified target and content.
     */
    def apply(target: String, title: String, content: Inline*): Link =
      apply(target, Some(title), content: _*)

    /**
     * Creates a link from the specified target, title and content.
     *
     * @param target  The target of the resulting link.
     * @param title   The optional title of resulting link.
     * @param content The content of the resulting link.
     * @return A link from the specified target and content.
     */
    def apply(target: String, title: Option[String], content: Inline*): Link = target match {
      case LocalPattern(id) => Local(Id(id), title, content: _*)
      case ExternalPattern(uri) => External(new URI(uri), title, Inline.Group(content: _*))
      case internal => Internal(Id(internal), title, Inline.Group(content: _*))
    }

    /**
     * Creates link content from link markdown.
     *
     * @param markdown The markdown to convert.
     * @return Link content from link markdown.
     */
    private[Content] def apply(markdown: Elements.Link): Link =
      markdown match {
        case Elements.ExternalLink(content, target, title, _) =>
          Link(target, title, content map (Inline(_)): _*)
        case invalid =>
          throw new IllegalArgumentException(s"Link element was not an external link: $invalid")
      }

    /**
     * Extracts a link's component parts.
     *
     * @param link The link to extract.
     * @return The component parts of the specified link.
     */
    def unapply(link: Link): Option[(link.Target, Option[String], Inline)] = link match {
      case Local(_, title, content) => Some((link.target, title, content))
      case Internal(_, title, content) => Some((link.target, title, content))
      case External(_, title, content) => Some((link.target, title, content))
    }

    /**
     * A link to another location in the same page.
     *
     * @param target  The ID of the target within this page.
     * @param title   The optional title of this link.
     * @param content The content of this link.
     */
    case class Local(target: Id, title: Option[String], content: Inline) extends Link {

      /* Target an ID. */
      override type Target = Id

    }

    /**
     * Factory for local links.
     */
    object Local {

      /**
       * Creates a local link from the specified target and content.
       *
       * @param target  The target of the resulting link.
       * @param content The content of the resulting link.
       * @return A local link from the specified target and content.
       */
      def apply(target: Id, content: Inline*): Local =
        Local(target, None, Inline.Group(content: _*))

      /**
       * Creates a local link from the specified target, title and content.
       *
       * @param target  The target of the resulting link.
       * @param title   The title of resulting link.
       * @param content The content of the resulting link.
       * @return A local link from the specified target and content.
       */
      def apply(target: Id, title: String, content: Inline*): Local =
        Local(target, Some(title), Inline.Group(content: _*))

      /**
       * Creates a local link from the specified target, title and content.
       *
       * @param target  The target of the resulting link.
       * @param title   The optional title of resulting link.
       * @param content The content of the resulting link.
       * @return A local link from the specified target and content.
       */
      def apply(target: Id, title: Option[String], content: Inline*): Local =
        Local(target, title, Inline.Group(content: _*))

    }

    /**
     * A link to another page in this site.
     *
     * @param target  The ID of the target page.
     * @param title   The title of this link.
     * @param content The content of this link.
     */
    case class Internal(target: Id, title: Option[String], content: Inline) extends Link {

      /* Target an ID. */
      override type Target = Id

    }

    /**
     * Factory for internal links.
     */
    object Internal {

      /**
       * Creates an internal link from the specified target and content.
       *
       * @param target  The target of the resulting link.
       * @param content The content of the resulting link.
       * @return An internal link from the specified target and content.
       */
      def apply(target: Id, content: Inline*): Internal =
        Internal(target, None, Inline.Group(content: _*))

      /**
       * Creates an internal link from the specified target, title and content.
       *
       * @param target  The target of the resulting link.
       * @param title   The title of resulting link.
       * @param content The content of the resulting link.
       * @return An internal link from the specified target and content.
       */
      def apply(target: Id, title: String, content: Inline*): Internal =
        Internal(target, Some(title), Inline.Group(content: _*))

      /**
       * Creates an internal link from the specified target, title and content.
       *
       * @param target  The target of the resulting link.
       * @param title   The optional title of resulting link.
       * @param content The content of the resulting link.
       * @return An internal link from the specified target and content.
       */
      def apply(target: Id, title: Option[String], content: Inline*): Internal =
        Internal(target, title, Inline.Group(content: _*))

    }

    /**
     * A link to a page on another site.
     *
     * @param target  The URI of the target page.
     * @param title   The title of this link.
     * @param content The content of this link.
     */
    case class External(target: URI, title: Option[String], content: Inline) extends Link {

      /* Target a URI. */
      override type Target = URI

    }

    /**
     * Factory for external links.
     */
    object External {

      /**
       * Creates an external link from the specified target and content.
       *
       * @param target  The target of the resulting link.
       * @param content The content of the resulting link.
       * @return An external link from the specified target and content.
       */
      def apply(target: URI, content: Inline*): External =
        External(target, None, Inline.Group(content: _*))

      /**
       * Creates an external link from the specified target, title and content.
       *
       * @param target  The target of the resulting link.
       * @param title   The title of resulting link.
       * @param content The content of the resulting link.
       * @return An external link from the specified target and content.
       */
      def apply(target: URI, title: String, content: Inline*): External =
        External(target, Some(title), Inline.Group(content: _*))

      /**
       * Creates an external link from the specified target, title and content.
       *
       * @param target  The target of the resulting link.
       * @param title   The optional title of resulting link.
       * @param content The content of the resulting link.
       * @return An external link from the specified target and content.
       */
      def apply(target: URI, title: Option[String], content: Inline*): External =
        External(target, title, Inline.Group(content: _*))

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
  case class Header(level: Int, content: Inline) extends Block {

    /* Strip the content. */
    override def strip: Vector[String] = content.strip

  }

  /**
   * Factory for headers.
   */
  object Header {

    /**
     * Creates a new header.
     *
     * @param level   The level of the header.
     * @param content The content of the header.
     * @return A new header.
     */
    def apply(level: Int, content: Inline*): Header =
      Header(level, Inline.Group(content: _*))

    /**
     * Creates header content from header markdown.
     *
     * @param markdown The markdown to convert.
     * @return Header content from header markdown.
     */
    private[Content] def apply(markdown: Elements.Header): Header =
      Header(markdown.level, markdown.content map (Inline(_)): _*)

  }

  /**
   * Represents a paragraph in the document.
   *
   * @param content The content of this paragraph.
   */
  case class Paragraph(content: Inline) extends Block {

    /* Strip the content. */
    override def strip: Vector[String] = content.strip

  }

  /**
   * Factory for paragraphs.
   */
  object Paragraph {

    /**
     * Creates a new paragraph.
     *
     * @param content The content of the paragraph.
     * @return A new paragraph.
     */
    def apply(content: Inline*): Paragraph =
      Paragraph(Inline.Group(content: _*))

    /**
     * Creates paragraph content from paragraph markdown.
     *
     * @param markdown The markdown to convert.
     * @return Paragraph content from paragraph markdown.
     */
    private[Content] def apply(markdown: Elements.Paragraph): Paragraph =
      Paragraph(markdown.content map (Inline(_)): _*)

  }

  /**
   * Represents a list of items.
   *
   * @param items The items in this list.
   */
  case class List(items: Vector[Content]) extends Block {

    /* Strip the content. */
    override def strip: Vector[String] = items flatMap (_.strip)

  }

  /**
   * Factory for lists.
   */
  object List {

    /**
     * Creates a list containing the specified items.
     *
     * @param items The items contained in the list.
     * @return A list containing the specified items.
     */
    def apply(items: Content*): List =
      List(items.toVector)

    /**
     * Creates list content from list markdown.
     *
     * @param markdown The markdown to convert.
     * @return List content from list markdown.
     */
    private[Content] def apply(markdown: Elements.BulletList): List =
      List(markdown.content.collect {
        case Elements.BulletListItem(blocks, _, _) =>
          Block.Group(blocks map (Block(_)): _*) match {
            case Paragraph(content) => content
            case other => other
          }
      }.toVector)

  }

  /**
   * Represents a section in the document.
   *
   * @param header  The header of this section.
   * @param content The content of this section.
   */
  case class Section(header: Header, content: Block) extends Block {

    /* Strip the content. */
    override def strip: Vector[String] = header.strip ++ content.strip

  }

  /**
   * Factory for sections.
   */
  object Section {

    def apply(header: Header, content: Block*): Section =
      Section(header, Block.Group(content: _*))

  }

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
    links: Vector[Link]
  ) extends Content {

    /* Strip the content. */
    override def strip: Vector[String] =
      Vector(name.singular, name.plural) ++
        description.strip ++
        sections.flatMap(_.strip) ++
        links.flatMap(_.strip)

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
        val name: Name = astName match {
          case Elements.Title(Seq(Elements.Text(n, _)), _) => Name(n)
          case invalid => throw new IllegalArgumentException(s"Initial element was not a title: $invalid")
        }
        val description: Paragraph = astDescription match {
          case Elements.Paragraph(spans, _) => Paragraph(Inline.Group(spans map (Inline(_)): _*))
          case invalid => throw new IllegalArgumentException(s"Secondary element was not a paragraph: $invalid")
        }
        val (linksSections, sections): (Vector[Section], Vector[Section]) = astSections map {
          case Elements.Section(Elements.Header(level, header, _), content, _) =>
            Section(Header(level, Inline.Group(header map (Inline(_)): _*)), content map (Block(_)): _*)
          case invalid => throw new IllegalArgumentException(s"Subsequent element was not a section: $invalid")
        } partition {
          case Section(Header(2, Text(linksHeader)), _) if linksHeader.trim.equalsIgnoreCase("links") => true
          case _ => false
        }
        val links: Vector[Link] = linksSections.map(_.content).flatMap {
          case List(items) => items collect { case link@Link(_, _, _) => link }
          case _ => Vector()
        }
        Document(name, description, sections, links)
      }

    }

  }

}