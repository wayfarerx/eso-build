/*
 * IdSpec.scala
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
 * Test suite for the Id type.
 */
class IdSpec extends FlatSpec with Matchers {

  behavior of "Id"

  it should "normalize IDs to be lowercase, URL-safe ASCII" in {
    Id("Basic").value shouldBe "basic"
    Id("some Words").value shouldBe "some-words"
    Id("It's a sentence (with 1 aside).").value shouldBe "its-a-sentence-with-1-aside"
    Id("añejo tequila").value shouldBe "anejo-tequila"
  }

}
