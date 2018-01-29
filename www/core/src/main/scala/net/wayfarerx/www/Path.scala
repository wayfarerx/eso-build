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

/**
 * Base type for all path representations.
 */
sealed trait Path {

  /** Returns the number of elements in this path. */
  def length: Int

  /** Returns this path after removing all `.` and `..` elements possible. */
  def normalize: Path

  /**
   * Appends that path to this path.
   *
   * @param that The path to append.
   * @return That path appended to this path.
   */
  def /(that: Path): Path

  /**
   * Appends that string to this path.
   *
   * @param that The string to append.
   * @return That string appended to this path.
   */
  final def /(that: String): Path = this / Path(that)

}

/**
 * Definitions of the path ADT.
 */
object Path {

  /** The identifier for the current path. */
  val Current: String = "."

  /** The identifier for the parent path. */
  val Parent: String = ".."

  /** The separator for path elements. */
  val Separator: String = "/"

  /**
   * Creates a path from the specified strings.
   *
   * @param strings The strings to construct the path from.
   * @return A path from the specified strings.
   */
  def apply(strings: String*): Path =
    strings flatMap (_.split("""[\/\\]+""", -1)) filterNot (_.isEmpty) map Leaf match {
      case empty if empty.isEmpty => Empty
      case nonEmpty => (nonEmpty.init :\ (nonEmpty.last: NonEmpty)) (_ / _)
    }

  /**
   * The empty path.
   */
  case object Empty extends Path {

    /* Zero length. */
    override def length: Int = 0

    /* Normalize to this. */
    override def normalize: Path = this

    /* Construct a multiple. */
    override def /(that: Path): Path = that

    /* Return the name. */
    override def toString: String = ""

  }

  /**
   * Base type for all non-empty path representations.
   */
  sealed trait NonEmpty extends Path {

    /* Return a non-empty path. */
    override def /(that: Path): NonEmpty

  }

  /**
   * Implementation that represents a single name in a path.
   */
  case class Leaf private[Path](name: String) extends NonEmpty {

    /** True if this leaf represents the prefix path. */
    def isCurrent: Boolean = name == Current

    /** True if this leaf represents the prefix path minus its last element. */
    def isParent: Boolean = name == Parent

    /** True if this leaf represents a normal path element. */
    def isElement: Boolean = !isCurrent && !isParent

    /* Length of one. */
    override def length: Int = 1

    /* Normalize to this. */
    override def normalize: Path = this

    /* Construct a multiple. */
    override def /(that: Path): NonEmpty = that match {
      case Empty => this
      case leaf@Leaf(_) => Branch(this, leaf)
      case branch@Branch(_, _) => Branch(this, branch)
    }

    /* Return the name. */
    override def toString: String = name

  }

  /**
   * Implementation that represents a sequence of single path elements.
   */
  case class Branch(head: Leaf, tail: NonEmpty) extends NonEmpty {

    /* Length of all contained paths. */
    override def length: Int = head.length + tail.length

    /* Remove as many non-names as possible. */
    override def normalize: Path = if (head.isCurrent) tail.normalize else tail match {
      case Leaf(Current) => head
      case Leaf(Parent) if !head.isParent => Empty
      case Branch(Leaf(Current), remaining) => head / remaining.normalize
      case Branch(Leaf(Parent), remaining) if !head.isParent => remaining.normalize
      case remaining => head / remaining.normalize
    }

    /* Construct a branch. */
    override def /(that: Path): NonEmpty =
      if (that == Empty) this else copy(tail = tail / that)

    /* Return a delimited string. */
    override def toString: String = s"$head$Separator$tail"

  }

}
