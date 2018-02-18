/*
 * Indexer.scala
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

import ref.SoftReference
import io.circe.yaml
import net.wayfarerx.www

/**
 * A finite collection of indexed objects loaded from markdown files in the class path.
 */
sealed trait Index[Indexed <: AnyRef] {

  /** The IDs managed by this index. */
  def ids: Iterator[Id]

  /**
   * Attempts to return the indexed object found under the specified ID.
   *
   * @param id The ID of the indexed object to search for.
   * @return The specified indexed object if it was found.
   */
  def find(id: Id): Option[Indexed]

  /**
   * Returns an iterator over the entire collection of indexed objects.
   *
   * @return An iterator over the entire collection of indexed objects.
   */
  def list: Iterator[Indexed]

  /**
   * Appends another index to this index.
   *
   * @tparam T The type of indexed object in the resulting index.
   * @param that The index to append to this index.
   * @return An index aggregating this index and the specified index.
   */
  final def ++[T >: Indexed <: AnyRef](that: Index[_ <: T]): Index[T] = {
    val duplicates = Index.findDuplicates((ids ++ that.ids).toVector)
    if (duplicates.nonEmpty) {
      throw new IllegalStateException(s"Duplicate ID definitions in aggregate for: ${duplicates mkString ", "}.")
    } else {
      new www.Index.Aggregation[T](this, that)
    }
  }

}

/**
 * Factory for indexes
 */
object Index {

  /** Work with UTF-8 files. */
  implicit private def UTF8: scala.io.Codec = scala.io.Codec.UTF8

  /**
   * Creates a new index of the data located at the specified path using the supplied class loader.
   *
   * @tparam Indexed The type of indexed object managed by the index.
   * @param path        The path to load data from.
   * @param classLoader The classloader to load data from, defaults to the class loader for `Index`.
   * @param parse       The method that transforms the content of a resource into an indexed object.
   * @return A new index of the data located at the specified path using the supplied class loader.
   */
  def apply[Indexed <: AnyRef](
    path: String,
    classLoader: ClassLoader = classOf[Index[_]].getClassLoader
  )(
    parse: (Metadata.Structure, String) => Indexed
  ): Index[Indexed] = {
    val keys = loadKeys(path, classLoader)
    val keysById = keys flatMap (loadKeyByIds(_, classLoader))
    val duplicates = findDuplicates(keysById.map(_._1))
    if (duplicates.nonEmpty) {
      throw new IllegalStateException(s"Duplicate ID definitions in $path for: ${duplicates mkString ", "}.")
    } else {
      new Source[Indexed](Resources(keys.toSet, keysById.toMap, classLoader), parse)
    }
  }

  /**
   * Loads the keys available at the specified path.
   *
   * @param path        The path to search.
   * @param classLoader The class loader to search with.
   * @return The keys available at the specified path.
   */
  private def loadKeys(path: String, classLoader: ClassLoader): Vector[String] = {
    val prefix = if (path endsWith "/") path else path + "/"
    val source = scala.io.Source.fromResource(prefix, classLoader)
    try source.getLines.filter(_ endsWith ".md").map(prefix + _).toVector
    finally source.close()
  }

  /**
   * Loads a mapping of the supplied key indexed by the IDs specified by the resource's front matter.
   *
   * @param key         The key of the resource to inspect.
   * @param classLoader The class loader to load from.
   * @return A mapping of the supplied key indexed by the IDs specified by the resource's front matter.
   */
  private def loadKeyByIds(key: String, classLoader: ClassLoader): Vector[(Id, String)] = {
    val source = scala.io.Source.fromResource(key, classLoader)
    try readFrontMatter(source.getLines.buffered) finally source.close()
  }.get[Name]("name") map (_.ids) getOrElse Vector(Id(key)) map (_ -> key)

  /**
   * Reads a resource's front matter from an interator of lines.
   *
   * @param lines the lines to extract the front matter from.
   * @return The extracted front matter.
   */
  private def readFrontMatter(lines: BufferedIterator[String]): Metadata.Structure =
    if (lines.headOption.contains("---")) {
      lines.next()
      yaml.parser.parse(lines.takeWhile(_ != "---").mkString("\n")) match {
        case Left(err) => throw err
        case Right(metadata) => (metadata: Metadata) match {
          case structure@Metadata.Structure(_) => structure
          case _ => Metadata.Empty
        }
      }
    } else Metadata.Empty

  /**
   * Returns any of the specified IDs that appear more than once.
   *
   * @param ids The IDs to extract duplicates from.
   * @return Any of the specified IDs that appear more than once.
   */
  private def findDuplicates(ids: Vector[Id]): Set[Id] =
    ids.map(_ -> 1).groupBy(_._1).map {
      case (id, counts) => id -> counts.map(_._2).sum
    }.filter(_._2 > 1).keys.toSet

  /**
   * Metadata about the resources in an index.
   *
   * @param keys        The keys of the individual resources.
   * @param keysById    The mapping of IDs to keys.
   * @param classLoader The class loader to load resources from.
   */
  private case class Resources(keys: Set[String], keysById: Map[Id, String], classLoader: ClassLoader) {

    /**
     * Loads the data from the resource identified by the specified key.
     *
     * @param key The key that identifies the resource to load.
     * @return The data from the resource identified by the specified key.
     */
    def load(key: String): (Metadata.Structure, String) = {
      val source = scala.io.Source.fromResource(key, classLoader)
      try {
        val lines = source.getLines.buffered
        val frontMatter = readFrontMatter(lines)
        frontMatter -> (lines mkString "\n")
      } finally source.close()
    }

  }

  /**
   * A finite collection of indexed objects loaded from markdown files in the class path.
   *
   * @tparam Indexed The type of indexed object held in this source.
   * @param resources The resources that make up this index.
   * @param parse     The function that transforms text into indexed objects.
   */
  final private class Source[Indexed <: AnyRef](
    resources: Index.Resources,
    parse: (Metadata.Structure, String) => Indexed
  ) extends Index[Indexed] {

    /** The cache of indexed objects by resource key. */
    private val indexedByKey = collection.mutable.Map[String, SoftReference[Indexed]]()

    /* Return an iterator over the entire collection of indexed IDs. */
    override def ids: Iterator[Id] =
      resources.keysById.keys.iterator

    /* Attempt to materialize the indexed object found under the specified ID. */
    override def find(id: Id): Option[Indexed] =
      resources.keysById get id map materialize

    /* Return an iterator over the entire collection of indexed objects. */
    override def list: Iterator[Indexed] =
      resources.keysById.values.toVector.distinct.iterator map materialize

    /**
     * Materializes an indexed object from the cache or from disk.
     *
     * @param key The key of the indexed object to materialize.
     * @return The materialized object.
     */
    private def materialize(key: String): Indexed =
      indexedByKey synchronized {
        indexedByKey get key collect { case SoftReference(indexed) => indexed } getOrElse {
          val indexed = parse.tupled(resources.load(key))
          indexedByKey += key -> SoftReference(indexed)
          indexed
        }
      }

  }

  /**
   * An aggregation of indexed objects.
   *
   * @tparam Indexed The type of indexed object held in this aggregation.
   * @param first  The first group of indexed objects to aggregate.
   * @param second The second group of indexed objects to aggregate.
   */
  final private class Aggregation[Indexed <: AnyRef](
    first: Index[_ <: Indexed],
    second: Index[_ <: Indexed]
  ) extends Index[Indexed] {

    /* Return an iterator over the entire collection of indexed IDs. */
    override def ids: Iterator[Id] =
      first.ids ++ second.ids

    /* Attempt to materialize the indexed object found under the specified ID. */
    override def find(id: Id): Option[Indexed] =
      first.find(id) orElse second.find(id)

    /* Return an iterator over the entire collection of indexed objects. */
    override def list: Iterator[Indexed] =
      first.list ++ second.list


  }

}
