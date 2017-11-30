/*
 * data.scala
 *
 * Copyright 2017 wayfarerx <x@wayfarerx.net> (@thewayfarerx)
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
 * Base class for structured data trees.
 */
sealed trait Data

object Data {

  sealed trait Singular extends Data

}

/**
 * A leaf node containing a single value.
 *
 * @param value The value of this node.
 */
case class Value(value: String) extends Data.Singular

/**
 * A branch node containing an ordered collection of structured data trees.
 *
 * @param collection The ordered collection of structured data trees.
 */
case class Collection(collection: Vector[Data.Singular]) extends Data

/**
 * Factory for collection data.
 */
object Collection {

  /**
   * Creates a new collection data.
   *
   * @param collection The collection of data the instance will contain.
   * @return A new collection data.
   */
  def apply(collection: Data.Singular*): Collection = Collection(collection.toVector)

}

/**
 * A branch node containing an indexed collection of structured data trees.
 *
 * @param structure The indexed collection of structured data trees.
 */
case class Structure(structure: Map[String, Data]) extends Data.Singular {

  /**
   * Returns the data associated with the specified key.
   *
   * @param key The key to look up data for.
   * @return The data associated with the specified key.
   */
  def apply(key: String): Data = structure(key)

}

/**
 * Factory for structured data.
 */
object Structure {

  /**
   * Creates a new structure data.
   *
   * @param structure The structured data the instance will contain.
   * @return A new structure data.
   */
  def apply(structure: (String, Data)*): Structure = Structure(structure.toMap)

}
