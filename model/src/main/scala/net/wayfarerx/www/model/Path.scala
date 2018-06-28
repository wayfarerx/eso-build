/*
 * Path.scala
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
 * A relative path in the site.
 *
 * @param tokens The tokens that make up the path.
 */
final class Path private(val tokens: Vector[Path.Token.Change]) extends AnyVal {

  import Path._

  /** True if this path is empty. */
  def isEmpty: Boolean =
    tokens.isEmpty

  /** The number of tokens in this path. */
  def length: Int =
    tokens.length

  /**
   * Returns the token at the specfied index.
   *
   * @param index The index of the token to return.
   * @return The token at the specfied index.
   */
  def apply(index: Int): Token =
    tokens(index)

  /**
   * Returns a portion of this path as a new path.
   *
   * @param from  The index to start selecting at.
   * @param until The index to stop selecting at.
   * @return A portion of this path as a new path.
   */
  def apply(from: Int, until: Int): Path =
    new Path(tokens.slice(from, until))

  /**
   * Appends a token to this path.
   *
   * @tparam T The type of element to use as a token.
   * @param that The token to append.
   * @return The resulting path.
   */
  def :+[T: Element](that: T): Path = implicitly[Element[T]].asToken(that) match {
    case Token.Current => this
    case child@Token.Child(_) => new Path(tokens :+ child)
    case Token.Parent => tokens match {
      case working if working.isEmpty || working.last == Token.Parent => new Path(working :+ Token.Parent)
      case working => new Path(working.init)
    }
  }

  /**
   * Appends a path to this path.
   *
   * @param that The path to append.
   * @return The resulting path.
   */
  @annotation.tailrec
  def :++(that: Path): Path = if (that.isEmpty) this else
    this :+ that(0) :++ that(1, that.length)

  /**
   * Prepends a token to this path.
   *
   * @tparam T The type of element to use as a token.
   * @param that The token to prepend.
   * @return The resulting path.
   */
  def +:[T: Element](that: T): Path = implicitly[Element[T]].asToken(that) match {
    case Token.Current => this
    case Token.Parent => new Path(Token.Parent +: tokens)
    case child@Token.Child(_) => tokens match {
      case working if working.isEmpty || working.head != Token.Parent => new Path(child +: working)
      case working => new Path(working.tail)
    }
  }

  /**
   * Prepends a path to this path.
   *
   * @param that The path to prepend.
   * @return The resulting path.
   */
  @annotation.tailrec
  def ++:(that: Path): Path = if (that.isEmpty) this else
    that(0, that.length - 1) ++: that(that.length - 1) +: this

  /** Attempts to create a normal version of this path. */
  def normalize: Option[Vector[Name]] =
    ((Some(Vector()): Option[Vector[Name]]) /: tokens) { (r, t) =>
      r flatMap { v =>
        t match {
          case Path.Token.Child(name) => Some(v :+ name)
          case Path.Token.Parent => None
        }
      }
    }

  /* Return the tokens in path form. */
  override def toString: String =
    tokens mkString "/"

}

/**
 * A factory for paths.
 */
object Path {

  /** The empty path. */
  val empty: Path = new Path(Vector())

  /**
   * Creates a new path from the specified sequence of tokens.
   *
   * @tparam T The type of element to use as a token.
   * @param elements The elements that make up the path.
   * @return A new path from the specified sequence of tokens.
   */
  def apply[T: Element](elements: T*): Path = {
    val element = implicitly[Element[T]]
    (empty /: elements) ((p, e) => p :+ element.asToken(e))
  }

  /**
   * Base type for path tokens.
   */
  sealed trait Token

  /**
   * Definitions of the supported token types.
   */
  object Token {

    /**
     * The token representing no change, dropped from paths.
     */
    case object Current extends Token {

      /* Return the constant form. */
      override def toString: String = "."

    }

    /**
     * Base type for path tokens that change the resulting path.
     */
    sealed trait Change extends Token

    /**
     * The token representing a move to the parent path.
     */
    case object Parent extends Change {

      /* Return the constant form. */
      override def toString: String = ".."

    }

    /**
     * The token representing a move to a child path.
     */
    case class Child(name: Name) extends Change {

      /* Return the value of this child's name. */
      override def toString: String = name.value

    }

  }

  /**
   * Type class for elements that can be used as tokens.
   *
   * @tparam T The type of element that can be used as a token.
   */
  trait Element[T] {

    /**
     * Converts an element to a token.
     *
     * @param element The element to convert.
     * @return The specified element as a token.
     */
    def asToken(element: T): Token

  }

  /**
   * The globally supported element type classes.
   */
  object Element {

    /** Tokens can obviously be used as tokens. */
    implicit val TokenAsElement: Element[Token] = t => t

    /** Names can be used as tokens. */
    implicit val NameAsElement: Element[Name] = Token.Child(_)

    /** Strings can be used as tokens. */
    implicit val StringAsElement: Element[String] = {
      case "." => Token.Current
      case ".." => Token.Parent
      case name => Token.Child(Name(name))
    }

  }

}
