/*
 * Category.scala
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

/**
 * A finite collection of topics loaded from markdown files in the class path.
 */
sealed trait Category[Topic <: AnyRef] {

  /** The IDs managed by this index. */
  def ids: Set[Id]

  /**
   * Attempts to return the topic found under the specified ID.
   *
   * @param id The ID of the topic to search for.
   * @return The specified topic if it was found.
   */
  def find(id: Id): Option[Topic]

  /**
   * Returns an iterator over the entire collection of topics.
   *
   * @return An iterator over the entire collection of topics.
   */
  def list: Iterator[Topic]

  /**
   * Appends another index to this index.
   *
   * @tparam T The type of topic in the resulting index.
   * @param that The index to append to this index.
   * @return An index aggregating this index and the specified index.
   */
  final def ++[T >: Topic <: AnyRef](that: Category[_ <: T]): Category[T] =
    ids intersect that.ids match {
      case duplicates if duplicates.nonEmpty =>
        throw new IllegalStateException(s"Duplicate ID definitions in aggregate for: ${duplicates mkString ", "}.")
      case _ =>
        new Category.Aggregation[T](this, that)
    }

}

/**
 * Factory for indexes
 */
object Category {

  /** Work with UTF-8 files. */
  implicit private def UTF8: scala.io.Codec = scala.io.Codec.UTF8

  /**
   * Creates a new index of the data located at the specified path using the supplied class loader.
   *
   * @tparam Topic The type of topic object managed by the index.
   * @param root   The root asset to load data from.
   * @param parse  The method that transforms the content of a resource into a topic object.
   * @param loader The assets to load data from, defaults to the library class loader.
   * @return A new index of the data located at the specified path using the supplied class loader.
   */
  def apply[Topic <: AnyRef](root: Asset)(parse: String => Topic)(implicit loader: Asset.Loader): Category[Topic] = {
    val assets = root.list() filter (_.mimeType contains Asset.MIME.Markdown)
    val assetsById = assets flatMap { asset =>
      val source = scala.io.Source.fromResource(asset.path, loader.classLoader)
      val name = try {
        val remaining = source.getLines().dropWhile(_.trim.isEmpty)
        if (remaining.hasNext) remaining.next match {
          case title if title.trim.startsWith("# ") => Name(title.substring(title.indexOf('#') + 1).trim)
        } else Name(asset.path)
      } finally source.close()
      name.ids map (_ -> asset)
    }
    val duplicates = assetsById.map(_._1).map(_ -> 1).groupBy(_._1).map {
      case (id, counts) => id -> counts.map(_._2).sum
    }.filter(_._2 > 1).keys.toSet
    if (duplicates.nonEmpty) {
      throw new IllegalStateException(s"Duplicate ID definitions in ${root.path} for: ${duplicates mkString ", "}.")
    } else {
      new Source[Topic](assetsById.toMap, parse)
    }
  }

  /**
   * A finite collection of topics loaded from markdown files in the class path.
   *
   * @tparam Topic The type of topic held in this source.
   * @param assetsById The mapping of IDs to assets.
   * @param parse      The function that transforms text into topics.
   * @param loader     The loader to use for this source.
   */
  final private class Source[Topic <: AnyRef](
    assetsById: Map[Id, Asset],
    parse: String => Topic)(
    implicit loader: Asset.Loader
  ) extends Category[Topic] {

    /** The cache of topics by asset. */
    private val topicsByAsset = collection.mutable.Map[Asset, SoftReference[Topic]]()

    /* Return an iterator over the entire collection of topic IDs. */
    override def ids: Set[Id] =
      assetsById.keys.toSet

    /* Attempt to materialize the topic found under the specified ID. */
    override def find(id: Id): Option[Topic] =
      assetsById get id map materialize

    /* Return an iterator over the entire collection of topics. */
    override def list: Iterator[Topic] =
      assetsById.values.toVector.distinct.iterator map materialize

    /**
     * Materializes a topic from the cache or from disk.
     *
     * @param asset The asset for the topic to materialize.
     * @return The materialized object.
     */
    private def materialize(asset: Asset): Topic =
      topicsByAsset synchronized {
        topicsByAsset get asset collect { case SoftReference(topic) => topic } getOrElse {
          val topic = parse(asset.loadText())
          topicsByAsset += asset -> SoftReference(topic)
          topic
        }
      }

  }

  /**
   * An aggregation of categories.
   *
   * @tparam Topic The type of topic held in this aggregation.
   * @param first  The first group of topics to aggregate.
   * @param second The second group of topics to aggregate.
   */
  final private class Aggregation[Topic <: AnyRef](
    first: Category[_ <: Topic],
    second: Category[_ <: Topic]
  ) extends Category[Topic] {

    /* Return an iterator over the entire collection of topic IDs. */
    override lazy val ids: Set[Id] =
      first.ids ++ second.ids

    /* Attempt to materialize the topic found under the specified ID. */
    override def find(id: Id): Option[Topic] =
      first.find(id) orElse second.find(id)

    /* Return an iterator over the entire collection of topics. */
    override def list: Iterator[Topic] =
      first.list ++ second.list


  }

}
