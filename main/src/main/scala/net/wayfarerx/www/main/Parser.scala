/*
 * Parser.scala
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
package main

import java.nio.file.{Files, Path => JPath}

import io.Codec
import util.{Failure, Success, Try}

import laika.api.Parse
import laika.parse.markdown.Markdown
import laika.tree.{Documents, Elements}

import model._

/**
 * Concrete decoder implementation.
 */
object Parser {

  import Markup._

  /** The codec to use. */
  private implicit val codec: Codec = Codec.UTF8

  /** The parser to use. */
  private val Parser = Parse.as(Markdown).withoutRewrite

  /**
   * Decodes the specified file.
   *
   * @tparam T The type of entity to decode.
   * @param file The file to decode.
   * @return The attempt at decoding a file.
   */
  def apply[T](file: JPath): Try[Document] = Try {
    val stream = Files.newInputStream(file)
    try Parser fromStream stream finally stream.close()
  } flatMap convertDocument

  /**
   * Attempts to convert a markdown document into an internal document.
   *
   * @param root The root of the element tree to decode.
   * @return The title, description, content and sections of the element.
   */
  private def convertDocument(root: Documents.Document): Try[Document] =
    root.content.content match {
      case (header@Elements.Header(1, _, _)) +: Elements.Paragraph(description, _) +: remaining =>
        convertToMarkup(description) flatMap { descriptionMarkup =>
          readSection(header, remaining) map { case (section, _) =>
            Document(Name(section.header.strip), None, descriptionMarkup, section.content, section.sections)
          }
        }
      case unknown =>
        Failure(new IllegalArgumentException(unknown.toString))
    }

  /**
   * Reads a section and its subsections from a sequence of blocks.
   *
   * @param header The header for the section being read.
   * @param blocks The blocks to read the section from.
   * @return A section and the remaining blocks.
   */
  private def readSection(
    header: Elements.Header,
    blocks: Seq[Elements.Block]
  ): Try[(Section, Seq[Elements.Block])] = {

    /* Read all subsections. */
    @annotation.tailrec
    def subsections(
      level: Int,
      remaining: Seq[Elements.Block],
      output: Vector[Section]
    ): Try[(Vector[Section], Seq[Elements.Block])] = {
      remaining match {
        case Seq() =>
          Success(output -> Seq())
        case (h@Elements.Header(nextLevel, _, _)) +: tail if nextLevel <= level =>
          Success(output -> (h +: tail))
        case (h@Elements.Header(_, _, _)) +: tail =>
          readSection(h, tail) match {
            case Success((section, next)) => subsections(level, next, output :+ section)
            case Failure(thrown) => Failure(thrown)
          }
        case unknown =>
          Failure(new IllegalArgumentException(unknown.toString))
      }
    }

    convertToMarkup(header.content) flatMap { markup =>
      val contentBlocks = blocks.takeWhile {
        case Elements.Header(_, _, _) => false
        case _ => true
      }
      convertToMarkup(contentBlocks) flatMap { content =>
        subsections(header.level, blocks drop contentBlocks.length, Vector()) map { case (sections, remaining) =>
          Section(header.level, markup, content, sections) -> remaining
        }
      }
    }
  }

  /**
   * Attempts to convert a sequence of span elements into markup.
   *
   * @param input The span elements to convert.
   * @return The converted markup if available.
   */
  private def convertToMarkup(input: Seq[Elements.Element]): Try[Markup] =
    ((Success(Vector()): Try[Vector[Markup]]) /: input) { (previous, span) =>
      previous flatMap (output => span match {

        case Elements.Text(content, _) =>
          Success(output :+ Markup(content))

        case Elements.Emphasized(spans, _) =>
          convertToMarkup(spans) map (output :+ Emphasis(_))

        case Elements.Strong(spans, _) =>
          convertToMarkup(spans) map (output :+ Strong(_))

        case Elements.ExternalLink(spans, target, title, _) =>
          convertToMarkup(spans) flatMap (createLink(target, title, _)) map (output :+ _)

        case Elements.Paragraph(Seq(Elements.Image(_, uri, _, _, title, _)), _) =>
          Success(output :+ Figure(Asset.Image.Single(Name(uri.uri.toString)),
            title map (Markup(_)) getOrElse Markup.empty))

        case Elements.Paragraph(spans, _) =>
          convertToMarkup(spans) map (output :+ Paragraph(_))

        case Elements.EnumList(items, _, _, _) =>
          val _items = items.collect { case Elements.EnumListItem(content, _, _, _) => content }
          ((Success(Vector()): Try[Vector[List.Item]]) /: _items) { (previous, item) =>
            previous flatMap (results => convertToMarkup(item) map (results :+ List.Item(_)))
          } map (output :+ List.Ordered(_))

        case Elements.BulletList(items, _, _) =>
          val _items = items.collect { case Elements.BulletListItem(content, _, _) => content }
          ((Success(Vector()): Try[Vector[List.Item]]) /: _items) { (previous, item) =>
            previous flatMap (results => convertToMarkup(item) map (results :+ List.Item(_)))
          } map (output :+ List.Unordered(_))

        case unknown =>
          Failure(new IllegalArgumentException(unknown.toString))

      })
    } map {
      case Vector() => Markup.empty
      case Vector(markup) => markup
      case output => Markup(output: _*)
    }

  /**
   * Creates a link markup element.
   *
   * @param target The target of the link.
   * @param title  The title of the link if specified.
   * @param markup The markup the link contains.
   * @return A link markup element.
   */
  private def createLink(target: String, title: Option[String], markup: Markup): Try[Link] = {
    val result: Try[Markup.Link] = target match {
      case name if name startsWith "#" =>
        Success(Link.Local(Name(name.substring(1)), title, markup))
      case url if url contains "://" =>
        url.indexOf('#') match {
          case index if index < 0 => Success(Link.Resolved(url, title, markup))
          case index => Success(Link.Resolved(url, title, markup))
        }
      case location if location startsWith "/" => location indexOf '#' match {
        case hashAt if hashAt > 0 =>
          Location(Path(location.split('/').toSeq: _*)) map { loc =>
            Success(Link.Internal(Pointer[AnyRef](loc), Some(Name(location.substring(hashAt + 1))), title, markup))
          } getOrElse Failure(new IllegalArgumentException(location))
        case _ =>
          Location(Path(location.split('/').filterNot(_.isEmpty).toSeq: _*)) map { loc =>
            Success(Link.Internal(Pointer[AnyRef](loc), None, title, markup))
          } getOrElse Failure(new IllegalArgumentException(location))
      }
      case path if path contains '/' => path indexOf '#' match {
        case hashAt if hashAt > 0 =>
          Success(Link.Internal(Pointer[AnyRef](
            Path(path.split('/').toSeq: _*)
          ), Some(Name(path.substring(hashAt + 1))), title, markup))
        case _ =>
          Success(Link.Internal(Pointer[AnyRef](Path(path.substring(2))), None, title, markup))
      }
      case name => name indexOf '#' match {
        case hashAt if hashAt > 0 =>
          Success(Link.Internal(Pointer[AnyRef](
            Name(name.substring(0, hashAt))
          ), Some(Name(name.substring(hashAt + 1))), title, markup))
        case _ => Success {
          if (name.trim.isEmpty) Link.Internal(Pointer[AnyRef](Name(markup.strip)), None, title, markup)
          else Link.Internal(Pointer[AnyRef](Name(target)), None, title, markup)
        }
      }
    }
    result
  }

}
