/*
 * AssetSpec.scala
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
 * Test suite for the Asset type.
 */
class AssetSpec extends FlatSpec with Matchers {

  behavior of "Asset"

  it should "manage enumerating and loading classpath resources" in {
    implicit val loader: Asset.Loader = Asset.Loader(getClass.getClassLoader)
    val missing = Asset("missing")
    val root = Asset("asset-test")
    val md = Asset(root, "file.md").get
    val markdown = Asset(root, "file.markdown").get
    val properties = Asset(root, "file.properties").get
    val images = Asset(root, "images").get
    val gif = Asset(images, "file.gif").get
    val jpeg = Asset(images, "file.jpeg").get
    val jpg = Asset(images, "file.jpg").get
    val png = Asset(images, "file.png").get

    Seq(root, md, markdown, properties, images, gif, jpeg, jpg, png) foreach (_.exists shouldBe true)
    Seq(missing) foreach (_.exists shouldBe false)

    Seq(root, images) foreach (_.isDirectory shouldBe true)
    Seq(missing, md, markdown, properties, gif, jpeg, jpg, png) foreach (_.isDirectory shouldBe false)

    Seq(md, markdown, properties, gif, jpeg, jpg, png) foreach (_.isFile shouldBe true)
    Seq(missing, root, images) foreach (_.isFile shouldBe false)

    Seq(
      missing -> None,
      root -> None,
      md -> Some(Asset.MIME.Markdown),
      markdown -> Some(Asset.MIME.Markdown),
      properties -> Some(Asset.MIME.Properties),
      images -> None,
      gif -> Some(Asset.MIME.GIF),
      jpeg -> Some(Asset.MIME.JPEG),
      jpg -> Some(Asset.MIME.JPEG),
      png -> Some(Asset.MIME.PNG)
    ) foreach (p => p._1.mimeType shouldBe p._2)

    missing("missing") shouldBe None
    root("missing") shouldBe None
    root("file.md") shouldBe Some(md)
    md("missing") shouldBe None
    images("missing") shouldBe None
    images("file.gif") shouldBe Some(gif)
    gif("missing") shouldBe None

    missing.list().toSet shouldBe Set()
    root.list().toSet shouldBe Set(md, markdown, properties, images)
    md.list().toSet shouldBe Set()
    markdown.list().toSet shouldBe Set()
    properties.list().toSet shouldBe Set()
    images.list().toSet shouldBe Set(gif, jpeg, jpg, png)
    gif.list().toSet shouldBe Set()
    jpeg.list().toSet shouldBe Set()
    jpg.list().toSet shouldBe Set()
    png.list().toSet shouldBe Set()

    missing.load() shouldBe Array[Byte]()
    missing.loadText() shouldBe ""
    root.load() shouldBe Array[Byte]()
    root.loadText() shouldBe ""
    md.loadText() shouldBe "# md"
    markdown.loadText() shouldBe "# markdown"
    properties.loadText() shouldBe "key=properties"
    images.load() shouldBe Array[Byte]()
    images.loadText() shouldBe ""
    gif.load().length should be > 0
    jpeg.load().length should be > 0
    jpg.load().length should be > 0
    png.load().length should be > 0
  }

}
