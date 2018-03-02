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

final class Gallery(val items: Map[Id, Gallery.Picture]) extends AnyVal {

}

object Gallery {

  val Empty: Gallery = new Gallery(Map())

  private val ImageTypes = Set(Asset.MIME.GIF, Asset.MIME.JPEG, Asset.MIME.PNG)

  def apply(category: Asset)(implicit loader: Asset.Loader): Gallery = {
    category("images") map { root =>
      val alt = root("alt.properties") map { props =>
        val properties = new Properties()
        properties.load(new StringReader(props.loadText()))
        properties.asScala.toMap.map(p => Id(p._1) -> p._2)
      } getOrElse Map()
      val assets = root.list() filter (_.mimeType exists ImageTypes) map (a => Id(a.name) -> a) map {
        case (id, asset) => id match {
          case small if small.value endsWith s"-${Size.Small}" => (small, asset, Some(Size.Small))
          case medium if medium.value endsWith s"-${Size.Medium}" => (medium, asset, Some(Size.Medium))
          case large if large.value endsWith s"-${Size.Large}" => (large, asset, Some(Size.Large))
          case _ => (id, asset, None)
        }
      }
      val fixed = assets collect { case (id, asset, None) => id -> FixedPicture(asset, alt get id) }
      val scaled = assets.collect { case (id, asset, Some(size)) => (id, asset, size) }
        .groupBy(s => s._1.value.substring(0, s._1.value.lastIndexOf("-"))).toVector flatMap {
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
  } getOrElse Empty

  sealed trait Picture {

    def altText: Option[String]

  }

  case class FixedPicture(asset: Asset, altText: Option[String]) extends Picture

  case class ScaledPicture(small: Asset, medium: Asset, large: Asset, altText: Option[String]) extends Picture

}
