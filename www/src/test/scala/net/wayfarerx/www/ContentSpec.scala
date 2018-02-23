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

import org.scalatest._

/**
 * Test suite for the Content type.
 */
class ContentSpec extends FlatSpec with Matchers {

  behavior of "Content"

  it should "parse markdown documents" in {
    val doc = Content.Document(
      """
        |# Name
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
        | - https://wayfarerx.net/
        | - https://twitter.com/thewayfarerx/
        |
      """.stripMargin)

  }

}
