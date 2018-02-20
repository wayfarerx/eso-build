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

/**
 * Base type for all items in the content tree.
 */
sealed trait Content

/**
 * Definitions of the items in the content tree.
 */
object Content {

  //
  // Base types and categories.
  //

  /**
   * Base type for all block items in the content tree.
   */
  sealed trait Block extends Content

  /**
   * Types associated with block content.
   */
  object Block {

    /** A collection of block content. */
    type Fragment = Vector[Block]

    /**
     * Factory for block content collections.
     */
    object Fragment {

      /** The empty block content collection. */
      def empty: Fragment = Vector.empty

      /**
       * Creates an block content collection containing the specified items.
       *
       * @param items The items to include in the collection.
       * @return An block content collection containing the specified items.
       */
      def apply(items: Block*): Fragment = Vector(items: _*)

    }

  }

  /**
   * Base type for all inline items in the content tree.
   */
  sealed trait Inline extends Content

  /**
   * Types associated with inline content.
   */
  object Inline {

    /** A collection of inline content. */
    type Fragment = Vector[Inline]

    /**
     * Factory for inline content collections.
     */
    object Fragment {

      /** The empty inline content collection. */
      def empty: Fragment = Vector.empty

      /**
       * Creates an inline content collection containing the specified items.
       *
       * @param items The items to include in the collection.
       * @return An inline content collection containing the specified items.
       */
      def apply(items: Inline*): Fragment = Vector(items: _*)

    }

  }

  //
  // Specific implementations.
  //

  /**
   * Represents a header in the document.
   *
   * @param level   The level of this header (1-6).
   * @param content The content of this header.
   */
  case class Header(level: Int, content: Inline.Fragment) extends Block



  /**
   * The root of the content tree.
   *
   * @param name     The name of the document.
   * @param content  The content of the document.
   */
  case class Document(name: Name, content: Block.Fragment) extends Content

  /**
   * Factory and parser for documents.
   */
  object Document extends {

    import laika.api._
    import laika.parse.markdown._
    import laika.tree._

    /**
     * Creates a document with the specified metadata, parsing the supplied markdown.
     *
     * @return A document with the specified metadata after parsing the supplied markdown.
     */
    def apply(markdown: String): Document = Document(
      ???,
      (Parse as Markdown fromString markdown).content.content.map(convertBlock).toVector
    )

    private def convertBlock(block: Elements.Block): Block = block match {
      case Elements.Header(level, content, _) =>
        Header(level, content.map(convertInline).toVector)
      case _ =>
        ???
    }

    private def convertInline(inline: Elements.Span): Inline = inline match {
      case _ =>
        ???
    }

  }

}