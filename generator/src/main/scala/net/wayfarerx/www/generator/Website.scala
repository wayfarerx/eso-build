/*
 * Website.scala
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

package net.wayfarerx.www.generator

import collection.immutable.Map

/**
 * Base type for classes that require access to the entire website.
 */
trait Website {

  /** Representation of the background image in three sizes. */
  final type Backgrounds = Website.Backgrounds

  /** Factory for the background image in three sizes. */
  final val Backgrounds: Website.Backgrounds.type = Website.Backgrounds

  /** The location of the website. */
  def Server: String

  /** The assets in the website indexed by location. */
  def Assets: Map[String, Asset]

  /** The available alt text in the website indexed by asset. */
  def AltText: Map[Asset, String]

  /** The pages in the website indexed by location. */
  def Pages: Map[String, Page]

  /** The locations in the website indexed by page. */
  def Locations: Map[Page, String]

  /** The stylesheets in the website indexed by location. */
  def Stylesheets: Map[String, Stylesheet]

  /**
   * Extensions to the asset class to support alt text.
   *
   * @param asset The asset to extend.
   */
  final implicit class AssetExtensions(asset: Asset) {

    /** Returns the alt text for the asset. */
    def altText: Option[String] = AltText get asset

  }

  /**
   * Extensions to the page class to support locations, parents and associated images.
   *
   * @param page The page to extend.
   */
  final implicit class PageExtensions(page: Page) {

    /** Returns the location of the page. */
    def location: String = Locations(page)

    /** Returns the parent of the page if it has one. */
    def parent: Option[Page] = location match {
      case "/" => None
      case path => Pages get path.substring(0, path.lastIndexOf('/', path.length - 2) + 1)
    }

    /** Returns the image associated with the page. */
    def image: Asset = {
      val path = s"/images${location}image.png"
      parent match {
        case Some(p) => Assets.getOrElse(path, p.image)
        case None => Assets(path)
      }
    }

    /** Returns the background images associated with the page. */
    def backgrounds: Backgrounds = {
      val path = s"/images${location}background-@@@.jpg"
      parent match {
        case Some(p) =>
          Assets.get(path.replace("@@@", "large")) flatMap { large =>
            Assets.get(path.replace("@@@", "medium")) flatMap { medium =>
              Assets.get(path.replace("@@@", "small")) map { small =>
                Backgrounds(large, medium, small)
              }
            }
          } getOrElse p.backgrounds
        case None =>
          Backgrounds(
            Assets(path.replace("@@@", "large")),
            Assets(path.replace("@@@", "medium")),
            Assets(path.replace("@@@", "small")))
      }
    }

  }

}

/**
 * Definitions associated with websites.
 */
object Website {

  /**
   * The three sizes of background images.
   *
   * @param large The large background image.
   * @param medium The medium background image.
   * @param small The small background image.
   */
  case class Backgrounds(large: Asset, medium: Asset, small: Asset)

}
