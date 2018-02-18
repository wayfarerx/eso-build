/*
 * NameSpec.scala
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
 * Test suite for the Name type.
 */
class NameSpec extends FlatSpec with Matchers {

  behavior of "Name"

  it should "Track, capitalize and formalize singular and plural names" in {
    val oranges = Name("fresh orange", "fresh oranges")
    oranges.id shouldBe Id("fresh-orange")
    oranges.alias shouldBe Some(Id("fresh-oranges"))
    oranges.ids shouldBe Vector(Id("fresh-orange"), Id("fresh-oranges"))
    oranges.toString shouldBe "fresh orange"
    oranges() shouldBe "fresh orange"
    oranges(0) shouldBe "fresh oranges"
    oranges(Math.PI) shouldBe "fresh oranges"
    oranges.capitalized() shouldBe "Fresh orange"
    oranges.capitalized(0) shouldBe "Fresh oranges"
    oranges.capitalized(Math.PI) shouldBe "Fresh oranges"
    oranges.formalized() shouldBe "Fresh Orange"
    oranges.formalized(0) shouldBe "Fresh Oranges"
    oranges.formalized(Math.PI) shouldBe "Fresh Oranges"
  }

  it should "not create duplicate IDs" in {
    val name = Name("hi")
    name.id shouldBe Id("hi")
    name.alias shouldBe None
    name.ids shouldBe Vector(Id("hi"))
  }

  it should "capitalize quoted strings" in {
    Name(""""quoted"""").capitalized() shouldBe """"Quoted""""
  }

  it should "formalize quoted strings" in {
    Name(""""quoted"""").formalized() shouldBe """"Quoted""""
  }

  it should "formalize complex sentences" in {
    Name("it's time to go to the whatever.").formalized() shouldBe "It's Time to Go to the Whatever."
  }

}
