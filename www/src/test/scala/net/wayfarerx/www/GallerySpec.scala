/*
 * GallerySpec.scala
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

import org.scalatest._

/**
 * Test suite for the Gallery type.
 */
class GallerySpec extends FlatSpec with Matchers {

  behavior of "Gallery"

  implicit val loader: Asset.Loader = Asset.Loader(classOf[GallerySpec].getClassLoader)

  it should "manage picture galleries" in {
    val root = Asset("index-test-1")
    val images = root("images").get
    Gallery(root) shouldBe Gallery(Map(
      Id("image") -> Gallery.FixedPicture(
        images("image.png").get,
        Some("alt")
      ),
      Id("logo") -> Gallery.ScaledPicture(
        images("logo-small.png").get,
        images("logo-medium.png").get,
        images("logo-large.png").get,
        None
      )
    ))

  }

}
