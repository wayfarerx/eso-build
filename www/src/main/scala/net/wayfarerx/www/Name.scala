/*
 * Name.scala
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
 * The name of an object in the system.
 *
 * @param singular The singular form of the name.
 * @param plural   The plural form of the name.
 */
case class Name(singular: String, plural: String) {

  import Name._

  /** The ID form of the name. */
  final lazy val id: Id = Id(singular)

  /** The ID alias form of the name if it differs from the ID. */
  final lazy val alias: Option[Id] = Some(Id(plural)) filter (_ != id)

  /** All the IDs this name occupies. */
  def ids: Vector[Id] = id +: alias.toVector

  /**
   * Returns the form used for the specified count.
   *
   * @param count The number of items being considered, defaults to one.
   * @return The form used for the specified count.
   */
  def apply(count: Double = 1.0): String = count match {
    case 1.0 | -1.0 => singular
    case _ => plural
  }

  /** The capitalized singular form of the name. */
  def singularCapitalized: String = capitalize(singular)

  /** The capitalized plural form of the name. */
  def pluralCapitalized: String = capitalize(plural)

  /**
   * Returns the capitalized form used for the specified count.
   *
   * @param count The number of items being considered, defaults to one.
   * @return The capitalized form used for the specified count.
   */
  def capitalized(count: Double = 1.0): String = count match {
    case 1.0 | -1.0 => singularCapitalized
    case _ => pluralCapitalized
  }

  /** The formalized singular form of the name. */
  def singularFormalized: String = formalize(singular)

  /** The formalized plural form of the name. */
  def pluralFormalized: String = formalize(plural)

  /**
   * Returns the formalized form used for the specified count.
   *
   * @param count The number of items being considered, defaults to one.
   * @return The formalized form used for the specified count.
   */
  def formalized(count: Double = 1.0): String = count match {
    case 1.0 | -1.0 => singularFormalized
    case _ => pluralFormalized
  }

}

/**
 * Factory for names.
 */
object Name {

  /** The suffix to match to build singular and plural names. */
  private val CompoundPattern =
    """\(([^\|\(\)]+)(\|([^\|\(\)]+))?\)$""".r

  /** The pattern of word to match when formalizing. */
  private val CapitalizePattern =
    """[\p{IsLetter}\p{IsDigit}]""".r

  /** The pattern of word to match when formalizing. */
  private val FormalizePattern =
    """[\p{IsLetter}\p{IsDigit}][\p{IsLetter}\p{IsDigit}\p{IsPunctuation}\-_]*""".r

  /** The words to ignore when formalizing. */
  private val FormalizeIgnore = Set(
    "a",
    "an",
    "and",
    "any",
    "as",
    "at",
    "but",
    "by",
    "for",
    "from",
    "her",
    "his",
    "in",
    "into",
    "it",
    "its",
    "it's",
    "my",
    "nor",
    "of",
    "or",
    "our",
    "so",
    "some",
    "that",
    "the",
    "their",
    "these",
    "they",
    "this",
    "those",
    "to",
    "what",
    "whatever",
    "which",
    "whichever",
    "whose",
    "with",
    "yet",
    "your"
  )

  /**
   * Creates a name with the same singular and plural forms.
   *
   * @param name The name to use.
   * @return A name with the same singular and plural forms.
   */
  def apply(name: String): Name = {
    CompoundPattern.findFirstIn(name) match {
      case Some(found) =>
        val prefix = name.substring(0, name.length - found.length)
        val suffix = found.substring(1, found.length - 1)
        suffix.indexOf('|') match {
          case index if index > 0 =>
            Name(prefix + suffix.substring(0, index), prefix + suffix.substring(index + 1, suffix.length))
          case _ =>
            Name(prefix, prefix + suffix)
        }
      case None =>
        Name(name, name)
    }
  }

  /**
   * Capitalizes a string as if it was at the start of a sentence.
   *
   * @param str The string to capitalize.
   * @return The string capitalized as if it was at the start of a sentence.
   */
  private def capitalize(str: String): String =
    CapitalizePattern.findFirstMatchIn(str) map {
      letter =>
        (if (letter.start > 0) str.substring(0, letter.start) else "") +
          str.substring(letter.start, letter.end).toUpperCase +
          (if (letter.end < str.length) str.substring(letter.end, str.length) else "")
    } getOrElse str

  /**
   * Formalizes a string as if it was the title of a document.
   *
   * @param str The string to formalize.
   * @return The string formalized as if it was the title of a document.
   */
  private def formalize(str: String): String =
    if (str.isEmpty) str else {
      var result = Vector[String]()
      var cursor = 0
      FormalizePattern.findAllMatchIn(str) foreach {
        word =>
          if (word.start > cursor) result :+= str.substring(cursor, word.start)
          val text = str.substring(word.start, word.end)
          result :+= (if (cursor > 0 && FormalizeIgnore(text.toLowerCase)) text else capitalize(text))
          cursor = word.end
      }
      ((if (result.size > 1) result.init :+ capitalize(result.last) else result) :+ str.substring(cursor)).mkString
    }

}
