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

import scala.ref.SoftReference

/**
 * A finite collection of indexed objects.
 */
final class Index[Indexed: Name.HasName] private(
  path: String,
  parse: String => Indexed,
  classLoader: ClassLoader = classOf[Index[_]].getClassLoader
) {

  import Name.HasName.Named

  /** The keys defined in this index. */
  private lazy val keys: Set[String] = {
    val source = io.Source.fromResource(path, classLoader)(io.Codec.UTF8)
    try source.getLines.filter(_ endsWith ".md").toSet
    finally source.close()
  }

  /** The keys indexed by the IDs that refer to them. */
  private lazy val keysById: Map[Id, String] = iterator.flatMap {
    case (key, indexed) => (indexed.name.id, key) +: indexed.name.alias.map(_ -> key).toVector
  }.toMap

  /** The cache of indexed objects by ID. */
  private val indexedById = collection.mutable.Map[Id, SoftReference[Indexed]]()

  /** The cache of indexed objects by resource key. */
  private val indexedByKey = collection.mutable.Map[String, SoftReference[Indexed]]()

  /**
   * Attempts to materialize the indexed object found under the specified ID.
   *
   * @param id The ID of the indexed object to search for.
   * @return The specified indexed object if it was found.
   */
  def find(id: Id): Option[Indexed] =
    synchronized {
      indexedById get id collect {
        case SoftReference(indexed) => indexed
      }
    } orElse {
      keysById get id map (key => synchronized(load(key)))
    }

  /**
   * Returns an iterator over the entire collection of indexed objects.
   *
   * @return An iterator over the entire collection of indexed objects.
   */
  def list: Iterator[Indexed] =
    iterator map (_._2)

  /**
   * Returns an iterator over the entire collection of indexed objects.
   *
   * @return An iterator over the entire collection of indexed objects.
   */
  private def iterator: Iterator[(String, Indexed)] =
    keys.iterator map { key =>
      key -> synchronized {
        indexedByKey get key match {
          case Some(SoftReference(indexed)) => indexed
          case _ => load(key)
        }
      }
    }

  /**
   * Loads the object identified by the specified key.
   *
   * @param key The key that identifies the object.
   * @return The object identified by the specified key.
   */
  private def load(key: String): Indexed = {
    val indexed = parse {
      val source = io.Source.fromResource(key, classLoader)(io.Codec.UTF8)
      try source.getLines mkString "\n" finally source.close()
    }
    val reference = SoftReference(indexed)
    indexedById += indexed.name.id -> reference
    indexed.name.alias foreach (indexedById += _ -> reference)
    indexedByKey += key -> reference
    indexed
  }

}
