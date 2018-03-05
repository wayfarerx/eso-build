/*
 * Gallery.scala
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

import collection.JavaConverters._

import java.io.StringReader
import java.util.Properties

/**
 * A container for all the image assets found in a directory.
 *
 * @param items The image assets indexed by local ID.
 */
case class Gallery(items: Map[Id, Gallery.Picture])

/**
 * Factory for gallery instances.
 */
object Gallery {

  /** The empty gallery. */
  val empty: Gallery = new Gallery(Map())

  /** The MIME types of supported images. */
  private val ImageTypes = Set(Asset.MIME.GIF, Asset.MIME.JPEG, Asset.MIME.PNG)

  /**
   * Creates a gallery for the specified category asset.
   *
   * @param category The category asset to search.
   * @param loader The asset loader to use.
   * @return A gallery for the specified category asset.
   */
  def apply(category: Asset)(implicit loader: Asset.Loader): Gallery = {
    category("images") map { root =>
      val alt = root("alt.properties") map { props =>
        val properties = new Properties()
        properties.load(new StringReader(props.loadText()))
        properties.asScala.toMap.map(p => Id(p._1) -> p._2)
      } getOrElse Map()
      val assets = root.list() filter (_.mimeType exists ImageTypes) map { asset =>
        val id = Id(asset.name.substring(0, asset.name.lastIndexOf('.')))
        id match {
          case small if small.value endsWith s"-${Size.Small}" => (small, asset, Some(Size.Small))
          case medium if medium.value endsWith s"-${Size.Medium}" => (medium, asset, Some(Size.Medium))
          case large if large.value endsWith s"-${Size.Large}" => (large, asset, Some(Size.Large))
          case other => (other, asset, None)
        }
      }
      val fixed = assets collect { case (id, asset, None) => id -> FixedPicture(asset, alt get id) }
      val scaled = assets.collect { case (id, asset, Some(size)) => (id, asset, size) }
        .groupBy(s => s._1.value.substring(0, s._1.value.lastIndexOf('-'))).toVector flatMap {
        case (prefix, items) =>
          val id = Id(prefix)
          val map = items.groupBy(_._3).mapValues(_.head._2)
          if (map.size == 3) Some(id -> ScaledPicture(map(Size.Small), map(Size.Medium), map(Size.Large), alt get id))
          else None
      }
      fixed ++ scaled
    } getOrElse Vector() match {
      case nonEmpty if nonEmpty.nonEmpty => Some(new Gallery(nonEmpty.toMap))
      case _ => None
    }
  } getOrElse empty

  /**
   * Base type for gallery pictures.
   */
  sealed trait Picture {

    /** Returns the alt-text of this picture. */
    def altText: Option[String]

  }

  /**
   * Defines a picture that has only one resolution.
   *
   * @param asset The asset of the single-resolution picture.
   * @param altText The alt-text of the single-resolution picture.
   */
  case class FixedPicture(asset: Asset, altText: Option[String]) extends Picture

  /**
   * Defines a picture that has three resolutions.
   *
   * @param small The asset of the small-resolution picture.
   * @param medium The asset of the medium-resolution picture.
   * @param large The asset of the large-resolution picture.
   * @param altText The alt-text of the all the pictures.
   */
  case class ScaledPicture(small: Asset, medium: Asset, large: Asset, altText: Option[String]) extends Picture {

    /**
     * Returns the asset of the specified size.
     *
     * @param size The size of the asset to return.
     * @return The asset of the specified size.
     */
    def apply(size: Size): Asset = size match {
      case Size.Small => small
      case Size.Medium => medium
      case Size.Large => large
    }

  }

}
