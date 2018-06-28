/*
 * CocktailSpec.scala
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
package drinks

import org.scalatest._

/**
 * Test suite for the Cocktail type.
 */
class CocktailSpec extends FlatSpec with Matchers {

  behavior of "Cocktail"

  it should "load cocktails from disk" in {/*
    val manhattan = Cocktail.All.find(Id("manhattan")).get
    manhattan.name.id shouldBe Id("manhattan")
    manhattan.description shouldBe Content.Paragraph(
      Content.Text("A before dinner cocktail made with "),
      Content.Link.Internal(Id("rye-whiskey"), Content.Text("rye whiskey")),
      Content.Text(" and "),
      Content.Link.Internal(Id("sweet-vermouth"), Content.Text("sweet vermouth")),
      Content.Text(".")
    )*/
  }

}
