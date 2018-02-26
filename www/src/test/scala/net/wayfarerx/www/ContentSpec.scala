/*
 * ContentSpec.scala
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

import java.net.URI

import org.scalatest._

/**
 * Test suite for the Content type.
 */
class ContentSpec extends FlatSpec with Matchers {

  behavior of "Content"

  it should "parse markdown documents" in {
    import Content._
    Document("""# Name
        |
        |A very important document.
        |
        |## Cool Stuff
        |
        |Super cool things happening.
        |
        |## Other Stuff
        |
        |Check this out.
        |
        |## Links
        |
        |- [Cool stuff](#cool-stuff)
        |- [whiskey]()
        |- [Yum](cocktail-glass)
        |- [website](https://wayfarerx.net/)
        |- [twitter](https://twitter.com/thewayfarerx/)
        |""".stripMargin) shouldBe Document(
      Name("Name"),
      Paragraph(Text("A very important document.")),
      Vector(
        Section(Header(2, Text("Cool Stuff")), Paragraph(Text("Super cool things happening."))),
        Section(Header(2, Text("Other Stuff")), Paragraph(Text("Check this out.")))
      ),
      Vector(
        Link.Local(Id("cool-stuff"), Text("Cool stuff")),
        Link.Internal(Id("whiskey"), Text("whiskey")),
        Link.Internal(Id("cocktail-glass"), Text("Yum")),
        Link.External(new URI("https://wayfarerx.net/"), Text("website")),
        Link.External(new URI("https://twitter.com/thewayfarerx/"), Text("twitter"))
      )
    )

  }

}
