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
 * @param singular     The singular form of the name.
 * @param plural       The plural form of the name.
 * @param abbreviation The abbreviated version of this name.
 */
case class Name(singular: String, plural: String, abbreviation: Option[String] = None) {

  import Name._

  /** The ID form of the name. */
  final lazy val id: String = normalized
    .replaceAll("""[\'\"\(\)\[\]\{\}\<\>]+""", "")
    .replaceAll("""[^a-zA-Z0-9]+""", "-")

  /** The normalized form of the name. */
  final lazy val normalized: String = singular.trim.replaceAll("""\s+""", " ").toLowerCase

  /**
   * Returns the form used for the specified count.
   *
   * @param count The number of items being considered.
   * @return The form used for the specified count.
   */
  def apply(count: Double): String = count match {
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
   * @param count The number of items being considered.
   * @return The capitalized form used for the specified count.
   */
  def capitalized(count: Double): String = count match {
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
   * @param count The number of items being considered.
   * @return The formalized form used for the specified count.
   */
  def formalized(count: Double): String = count match {
    case 1.0 | -1.0 => singularFormalized
    case _ => pluralFormalized
  }

}

/**
 * Factory for names.
 */
object Name {

  /** The pattern that matches the first character of a word. */
  private val First = "[a-ZA-Z_]+"

  /** The pattern of word to match when formalizing. */
  private val FormalizePattern =
    s"""$First[a-zA-Z0-9-_]+""".r

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
   * @param abbreviation The abbreviated version of this name.
   * @return A name with the same singular and plural forms.
   */
  def apply(name: String, abbreviation: Option[String] = None): Name =
    Name(name, name, abbreviation)

  /**
   * Capitalizes a string as if it was at the start of a sentence.
   *
   * @param str The string to capitalize.
   * @return The string capitalized as if it was at the start of a sentence.
   */
  private def capitalize(str: String): String =
    if (str.isEmpty) str else str(0).toUpper + str.substring(1, str.length)

  /**
   * Formalizes a string as if it was the title of a document.
   *
   * @param str The string to formalize.
   * @return The string formalized as if it was the title of a document.
   */
  private def formalize(str: String): String =
    if (str.isEmpty) str else {
      val input = capitalize(str)
      var result = Vector[String]()
      var lastStart, lastEnd = 0
      FormalizePattern.findAllMatchIn(input) foreach { word =>
        if (word.start > lastEnd) result += input.substring(lastEnd, word.start)
        val text = input.substring(word.start, word.end)
        result += (if (FormalizeIgnore(text)) text else capitalize(text))
        lastStart = word.start
        lastEnd = word.end
      }
      ((if (result.size > 1) result.init :+ capitalize(result.last) else result) ++ input.substring(lastEnd)).mkString
    }

}
