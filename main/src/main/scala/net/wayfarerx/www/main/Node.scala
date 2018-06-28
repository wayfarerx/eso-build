/*
 * Node.scala
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

import collection.JavaConverters._
import io.Codec
import ref.SoftReference
import util.{Failure, Success, Try}

import model._

/**
 * Base type for all nodes in a site.
 */
sealed trait Node {

  import Node._

  /** The type of entity contained in this node. */
  type EntityType <: AnyRef

  /** The type of object contained in this node. */
  def cls: Class[_]

  /** The name of this node. */
  def name: Name

  /** The source directory that contains this node. */
  def source: JPath

  /** The document file that describes this node. */
  def document: JPath

  /** The location of this node in the site. */
  def location: Location

  /** The context of this node. */
  def context: Context

  /** The site of this node. */
  def site: Site[_]

  /** Calculates the distance between this node and another node. */
  def distanceTo(that: Node): Int

  /** The parent node if one exists. */
  def parentNode: Option[Node]

  /** The title of this node. */
  def title: Try[String]

  /** The description of this node. */
  def description: Try[Markup]

  /** The nested children of this node. */
  def children: Try[Vector[Nested]]

  /** The index used by this node. */
  def index: Index

  /**
   * Attempts to decode this node's entity.
   *
   * @return The decoded entity if available.
   */
  def decode(): Try[EntityType]

  /**
   * Attempts to encode this node's entity.
   *
   * @return The encode entity if available.
   */
  def encode(): Try[Document]

}

/**
 * Factory for nodes.
 */
object Node {

  /** The codec to use. */
  private implicit val codec: Codec = Codec.UTF8

  /** The name of index documents. */
  private val Index = "index.md"

  /** The suffix of index documents. */
  private val IndexSuffix = "/" + Index

  /** The suffix of markdown documents. */
  private val MarkdownSuffix = ".md"

  /**
   * Creates a root node.
   *
   * @param source The source directory of the root node.
   * @param site  The hints about the root node.
   * @return A new root node.
   */
  def apply[T <: AnyRef](source: JPath, site: Site[T]): Node =
    Root(source resolve Index, site)

  /**
   * Extracts a node.
   *
   * @param node The node to extract.
   * @return The content of the specified node.
   */
  def unapply(node: Node): Option[(Class[_], Name, JPath, JPath, Location, Try[Vector[Nested]])] =
    Some(node.cls, node.name, node.source, node.document, node.location, node.children)

  /**
   * Base type for nested nodes in a site.
   */
  sealed trait Nested extends Node {

    /** The parent of this node. */
    def parent: Node

    /* The location of this node in the site. */
    final override lazy val location: Location = Location.Nested(parent.location, name)

    /* Return the parent node. */
    final override def parentNode: Option[Node] = Some(parent)

  }

  /**
   * Extractor for nested nodes.
   */
  object Nested {

    /**
     * Extracts a nested node.
     *
     * @param nested The nested node to extract.
     * @return The content of the specified nested node.
     */
    def unapply(nested: Nested): Option[(Class[_], Name, JPath, JPath, Location, Try[Vector[Nested]], Node)] =
      Some((nested.cls, nested.name, nested.source, nested.document, nested.location, nested.children, nested.parent))

  }

  /**
   * Mixin that supports common node implementations.
   *
   * @tparam T The type of the node.
   */
  private sealed trait NodeSupport[T <: AnyRef] extends Node {

    /* Set the entity type. */
    final override type EntityType = T

    /** The cached entity value. */
    private var cache: Option[SoftReference[T]] = None

    /** The type hints for this node. */
    def hints: Site.Hints[T]

    /* Return the hinted type. */
    final override def cls: Class[_] = hints.classTag.runtimeClass

    /* Return the directory that contains the document. */
    final override lazy val source: JPath = document.getParent

    /* The context for this node. */
    final override lazy val context = new Ctx(this)

    /* The title of this node. */
    final override lazy val title: Try[String] = Try {
      val source = io.Source.fromFile(document.toFile)
      try source.getLines take 1 collect {
        case l if l startsWith "# " => l.substring(2).trim
      } match {
        case titles if titles.hasNext => Success(titles.next)
        case _ => Failure(new IllegalStateException("Title not found."))
      } finally source.close()
    }.flatten

    /* The description of this node. */
    override def description: Try[Markup] =
      decode() map (hints.entity.description(context, _))

    /* Calculate the distance to another node. */
    final override def distanceTo(that: Node): Int =
      if (that == this) 0 else {
        val prefixLength = location.commonPrefix(that.location).path.length
        location.path.length - prefixLength + (that.location.path.length - prefixLength)
      }

    /* Attempt to decode this node's entity. */
    final override def decode(): Try[T] = synchronized {
      cache flatMap (_.get) map (Success(_)) getOrElse {
        Parser[T](document) flatMap (hints.entity.decode(context, _)) map { entity =>
          cache = Some(SoftReference(entity))
          entity
        }
      }
    }

    /* Attempt to decode and publish this node's entity. */
    final override def encode(): Try[Document] =
      decode() flatMap (hints.entity.encode(context, _))

  }

  /**
   * Mixin that supports common node parent implementations.
   *
   * @tparam T The type of the node.
   */
  private sealed trait ParentSupport[T <: AnyRef] extends NodeSupport[T] {

    /* Return child nodes. */
    final override lazy val children: Try[Vector[Nested]] = Try {
      explore(source) map {
        case (child, name) if child.toString.replace('\\', '/') endsWith IndexSuffix =>
          Branch(name, child, hints(name), this)
        case (child, name) =>
          Leaf(name, child, hints(name), this)
      }
    }

  }

  /**
   * The root node in a site.
   *
   * @param document The root document of the site.
   * @param site    The hints to seed type information with.
   */
  private case class Root[T <: AnyRef](document: JPath, override val site: Site[T])
    extends ParentSupport[T] {

    /* Create the index. */
    override lazy val index: Index = new Index(this)

    /* Return the empty name. */
    override def name: Name = Name.empty

    /* The location of this node in the site. */
    override def location: Location = Location.Root

    /* Return the site's hints. */
    override def hints: Site.Hints[T] = site.hints

    /* Return none. */
    override def parentNode: Option[Node] = None

  }

  /**
   * A nested node in a site.
   *
   * @param name     The name of this branch.
   * @param document The document describing this branch.
   * @param hints    The hints available for this branch.
   * @param parent   The parent of this branch.
   */
  private case class Branch[T <: AnyRef](
    name: Name,
    document: JPath,
    hints: Site.Hints[T],
    parent: NodeSupport[_ <: AnyRef]
  ) extends ParentSupport[T] with Nested {

    /* Delegate to the parent node. */
    override def site: Site[_] = parent.site

    /* Delegate to the parent node. */
    override def index: Index = parent.index

  }

  /**
   * A nested node in a site.
   *
   * @param name     The name of this branch.
   * @param document The document describing this branch.
   * @param hints    The hints available for this branch.
   * @param parent   The parent of this branch.
   */
  private case class Leaf[T <: AnyRef](
    name: Name,
    document: JPath,
    hints: Site.Hints[T],
    parent: NodeSupport[_ <: AnyRef]
  ) extends NodeSupport[T] with Nested {

    /* Delegate to the parent node. */
    override def site: Site[_] = parent.site

    /* Return no child nodes. */
    override def children: Try[Vector[Nested]] = Success(Vector.empty)

    /* Delegate to the parent node. */
    override def index: Index = parent.index

  }

  /**
   * Explores the specified directory, returning nested node document paths.
   *
   * @param directory The directory to explore.
   * @return Any nested node document paths and names.
   */
  private def explore(directory: JPath): Vector[(JPath, Name)] =
    if (!Files.isDirectory(directory)) Vector() else
      Files.list(directory).iterator.asScala.flatMap { child =>
        if (Files.isDirectory(child)) {
          val index = child resolve Index
          if (Files.isRegularFile(index)) Iterator(index -> Name(child.getName(child.getNameCount - 1).toString))
          else Iterator.empty
        } else if (Files.isRegularFile(child)) {
          val str = child.toString.replace('\\', '/')
          if (!str.endsWith(IndexSuffix) && str.endsWith(MarkdownSuffix)) {
            val name = child.getName(child.getNameCount - 1).toString
            Iterator(child -> Name(name.substring(0, name.length - 3)))
          } else Iterator.empty
        } else Iterator.empty
      }.toVector


  /**
   * Implementation of context bound to a node.
   */
  private final class Ctx(source: NodeSupport[_ <: AnyRef]) extends Context {

    /* Transform a pointer into an absolute path. */
    override def locate[U <: AnyRef](pointer: Pointer[U]): Try[String] =
      resolve(pointer) map (_.location.toString)

    /* Attempt to load the title. */
    override def loadTitle[T <: AnyRef](pointer: Pointer[T]): Try[String] =
      resolve(pointer) flatMap (_.title)

    /* Attempt to load the description. */
    override def loadDescription[T <: AnyRef](pointer: Pointer[T]): Try[Markup] =
      resolve(pointer) flatMap (_.description)

    /**
     * Attempts to resolve the referenced node.
     *
     * @tparam U The type of node to resolve.
     * @param pointer The pointer to the node to resolve.
     * @return The result of attempting to resolve the referenced node.
     */
    private def resolve[U <: AnyRef](pointer: Pointer[U]): Try[Node] = pointer match {
      case Pointer.Absolute(loc) =>
        source.index(loc, pointer.cls)
          .map(Success(_))
          .getOrElse(Failure(new IllegalArgumentException(loc.toString)))
      case Pointer.Relative(path) =>
        val resolved = source.location.path :++ path
        Location(resolved).flatMap(source.index(_, pointer.cls))
          .map(Success(_))
          .getOrElse(Failure(new IllegalArgumentException(path.toString)))
      case Pointer.Search(name) =>
        source.index(source, name, pointer.cls) match {
          case node +: _ => Success(node)
          case _ => Failure(new IllegalArgumentException(name.toString))
        }
    }

  }

}