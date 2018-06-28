/*
 * Resolver.scala
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

import java.io.StringReader
import java.nio.file.Files
import java.util.Properties

import collection.JavaConverters._
import ref.SoftReference

import model._

/**
 * Resolver for images and other assets.
 */
final class Resolver {

  import Resolver._

  /** The cache of alt text collections. */
  private var altTextCache = Map[Location, SoftReference[Map[String, String]]]()

  /**
   * Attempts to resolve a generic image asset.
   *
   * @param node  The node to resolve from.
   * @param asset The asset to resolve.
   * @return The resolved image asset if available.
   */
  def resolve(node: Node, asset: Asset.Image): Option[Image] = asset match {
    case img@Asset.Image.Single(_) => resolve(node, img)
    case img@Asset.Image.Collection(_, _) => resolve(node, img)
  }

  /**
   * Attempts to resolve a single image asset.
   *
   * @param node  The node to resolve from.
   * @param asset The asset to resolve.
   * @return The resolved image asset if available.
   */
  @annotation.tailrec
  def resolve(node: Node, asset: Asset.Image.Single): Option[Image.Single] =
    search(node, asset, asset.query) map { case (location, file) =>
      Image.Single(location -> file, loadAltText(node, asset))
    } match {
      case result@Some(_) => result
      case None => node.parentNode match {
        case Some(parent) => resolve(parent, asset)
        case None => None
      }
    }

  /**
   * Attempts to resolve a collection of image assets.
   *
   * @param node  The node to resolve from.
   * @param asset The assets to resolve.
   * @return The resolved image assets if available.
   */
  @annotation.tailrec
  def resolve(node: Node, asset: Asset.Image.Collection): Option[Image.Collection] = {
    val query = asset.query
    query.flatMap { case (size, options) =>
      search(node, asset, options) map (size -> _)
    } match {
      case result if result.size == query.size =>
        Some(Image.Collection(result, loadAltText(node, asset)))
      case _ => node.parentNode match {
        case Some(parent) => resolve(parent, asset)
        case None => None
      }
    }
  }

  /**
   * Searches for a suitable asset on the file system.
   *
   * @param node    The node to search within.
   * @param asset   The asset to search for.
   * @param options The paths and file names to check for.
   * @return The location and file name of a suitible asset.
   */
  private def search(
    node: Node,
    asset: Asset,
    options: Vector[(Path, String)]
  ): Option[(Location, String)] =
    options.iterator.map(q => (q._1, q._2, node.source.resolve(s"${q._1}/${q._2}"))).collectFirst {
      case (path, file, image) if Files.isRegularFile(image) => path -> file
    } flatMap { case (path, file) =>
      Location(node.location.path :++ path) map (l => (l, file))
    } map { case (location, file) => (location, file) }

  /**
   * Attempts to load the alt text for an asset.
   *
   * @param node  The node the asset was found in.
   * @param asset The assets to load alt text for.
   * @return The alt text for an asset if it exists.
   */
  private def loadAltText(node: Node, asset: Asset): Option[String] = {
    altTextCache get node.location flatMap (_.get) orElse {
      val file = node.source.resolve(asset.prefix.toString).resolve("alt.properties")
      if (Files.isRegularFile(file)) {
        val properties = new Properties
        properties.load(new StringReader(new String(Files.readAllBytes(file), "UTF-8")))
        val result = properties.asScala.toMap
        altTextCache += (node.location -> SoftReference(result))
        Some(result)
      } else None
    } flatMap (_ get asset.name.value)
  }

}

/**
 * Definitions associated with resolvers.
 */
object Resolver {

  /**
   * Base type for resolved images.
   */
  sealed trait Image {

    /** The alt text if available. */
    def alt: Option[String]

  }

  /**
   * Implementations of resolved images.
   */
  object Image {

    /**
     * A single resolved image.
     *
     * @param resource The location and file name resolved.
     * @param alt      The alt text if available.
     */
    case class Single private[Resolver](
      resource: (Location, String),
      alt: Option[String]
    ) extends Image

    /**
     * A collection resolved images indexed by size.
     *
     * @param resources The locations and file names resolved.
     * @param alt       The alt text if available.
     */
    case class Collection private[Resolver](
      resources: Map[Sizes, (Location, String)],
      alt: Option[String]
    ) extends Image

  }

}
