/*
 * Asset.scala
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

import scala.collection.immutable.ListSet

/**
 * Base type for assets.
 */
sealed trait Asset {

  /** The base name of the asset. */
  def name: Name

  /** The path to prefix asset names with. */
  def prefix: Path

  /** The extensions append to asset names. */
  def extensions: ListSet[String]

}

/**
 * Factory for asset searches.
 */
object Asset {

  /**
   * Base type for assets that resolve to a single file.
   */
  sealed trait Single extends Asset {

    /** The query over every possible instance of this asset. */
    final def query: Vector[(Path, String)] =
      extensions.toVector map (ext => prefix -> s"$name.$ext")

  }

  /**
   * Base type for assets that resolve to a collection of files.
   */
  sealed trait Collection[T] extends Asset {

    /** The variations of the asset that are required. */
    def variations: ListSet[T]

    /** The query over every possible instance of this asset. */
    final def query: Map[T, Vector[(Path, String)]] =
      variations.toVector.map(v => v -> extensions.toVector.map(ext => prefix -> s"$name-$v.$ext")).toMap

  }

  /**
   * Base type for image assets.
   */
  sealed trait Image extends Asset {

    /* Use the common image prefix. */
    final override def prefix: Path = Image.Prefix

    /* Use the common image extensions. */
    final override def extensions: ListSet[String] = Image.Extensions

  }

  object Image {

    /** The prefix to use for image searches. */
    private val Prefix = Path("images")


    /** The extensions to use for image searches. */
    private val Extensions = ListSet("png", "gif", "jpg", "jpeg")

    /**
     * A single image asset.
     *
     * @param name The name of this image asset.
     */
    case class Single(name: Name) extends Asset.Single with Image

    /**
     * A collection of scaled image assets.
     *
     * @param name The base name of this collections of image assets.
     * @param variations The sizes required for this collections of image assets.
     */
    case class Collection private (name: Name, variations: ListSet[Sizes]) extends Asset.Collection[Sizes] with Image

    /**
     * Factory for collections of image assets.
     */
    object Collection {

      /**
       * Creates a new collection of image assets.
       *
       * @param name The base name of this collections of image assets.
       * @param sizes The sizes required for this collections of image assets.
       * @return A new collection of image assets.
       */
      def apply(name: Name, sizes: Sizes*): Collection =
        Collection(name, if (sizes.isEmpty) ListSet(Sizes.All: _*) else ListSet(sizes: _*))

    }

  }

}