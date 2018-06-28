/*
 * Site.scala
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

import reflect.ClassTag

/**
 * Base type for site metadata providers.
 *
 * @tparam T The type of the root entity.
 */
trait Site[T <: AnyRef] {

  /** The name of the site. */
  def name: Name

  /** The author of the site. */
  def author: Author

  /** The base URL for the site. */
  def baseUrl: String

  /** The stylesheet for the site. */
  def stylesheet: String

  /** The links to external stylesheets. */
  def stylesheetLinks: Vector[Site.StyleSheetLink]

  /** The header image for this site. */
  def headerImage: Option[Asset.Image.Single]

  /** The navigation pointers. */
  def navigation: Vector[Markup.Link.Internal[_ <: AnyRef]]

  /** The identities pointers. */
  def identities: Vector[Markup.Link.Resolved]

  /** The statement at the end of every page. */
  def statement: Vector[Markup]

  /** The type hints for the site. */
  def hints: Site.Hints[T]

}

/**
 * Definitions associated with sites.
 */
object Site {

  /**
   * A link to an external stylesheet.
   *
   * @param href The location of the stylesheet.
   * @param integrity The optional integrity hash.
   * @param crossorigin The optional cross-origin setting.
   */
  case class StyleSheetLink(
    href: String,
    integrity: Option[String] = None,
    crossorigin: Option[String] = None
  )

  /**
   * Hints about the type of entities at certain locations.
   *
   * @tparam T The type of the underlying entity.
   * @param hints The hints to selectively use for children.
   */
  case class Hints[T <: AnyRef : ClassTag : Entity](
    hints: Vector[(Hints.Select, Hints[_ <: AnyRef])]
  ) {

    import Hints._

    /** The type of object contained in this node. */
    def classTag: ClassTag[T] = implicitly[ClassTag[T]]

    /** The evidence that the instance contained in this node is an entity. */
    def entity: Entity[T] = implicitly[Entity[T]]

    /**
     * Returns the hints for the specified children of this hint.
     *
     * @param name The name that identifies the child to return hints for.
     * @return The hints for the specified children of this hint.
     */
    def apply(name: Name): Hints[_ <: AnyRef] = hints collectFirst {
      case (Select.Matching(_name), hint) if _name == name => hint
      case (Select.All, hint) => hint
    } getOrElse (if (hints.isEmpty) this else copy(Vector.empty))

  }

  /**
   * Definitions associated with hints.
   */
  object Hints {

    /**
     * Creates a collection of hints.
     *
     * @tparam T The type of the underlying instance.
     * @param hints The hints to use for all children.
     * @return A collection of hints.
     */
    def apply[T <: AnyRef : ClassTag : Entity](hints: Hints[_ <: AnyRef]): Hints[T] =
      Hints(Select.All -> hints)

    /**
     * Creates a collection of hints.
     *
     * @tparam T The type of the underlying instance.
     * @param hints The hints to selectively use for children.
     * @return A collection of hints.
     */
    def apply[T <: AnyRef : ClassTag : Entity](hints: (Select, Hints[_ <: AnyRef])*): Hints[T] =
      Hints[T](hints.toVector)

    /**
     * Base type for hint selectors.
     */
    sealed trait Select

    /**
     * The supported selector types.
     */
    object Select {

      /**
       * Converts a string into a selector.
       *
       * @param str The string to convert.
       * @return A new hint selector.
       */
      def apply(str: String): Select =
        if (str == "*") All else Matching(Name(str))

      /**
       * Selects all children.
       */
      case object All extends Select

      /**
       * Selects any matching children.
       *
       * @param name The name to match.
       */
      case class Matching(name: Name) extends Select

    }

  }

}