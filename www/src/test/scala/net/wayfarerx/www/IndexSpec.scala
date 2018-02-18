/*
 * IndexSpec.scala
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
 * Test suite for the Index type.
 */
class IndexSpec extends FlatSpec with Matchers {

  behavior of "Index"

  val Parse: (Metadata, String) => String = (_, s) => s

  it should "Load resources from the classpath" in {
    val index1 = Index[String]("index-test-1", classOf[IndexSpec].getClassLoader)(Parse)
    index1.ids.toVector shouldBe Vector(Id("mock"), Id("mocks"), Id("other-mock"))
    index1.find(Id("foo")) shouldBe None
    index1.find(Id("mock")) shouldBe Some("mock1")
    index1.find(Id("mocks")) shouldBe Some("mock1")
    index1.find(Id("other-mock")) shouldBe Some("mock2")
    index1.list.toVector shouldBe Vector("mock1", "mock2")
    val index2 = Index[String]("index-test-2", classOf[IndexSpec].getClassLoader)(Parse)
    index2.ids.toVector shouldBe Vector(Id("last-mock"))
    index2.find(Id("foo")) shouldBe None
    index2.find(Id("mock")) shouldBe None
    index2.find(Id("mocks")) shouldBe None
    index2.find(Id("other-mock")) shouldBe None
    index2.find(Id("last-mock")) shouldBe Some("mock3")
    index2.list.toVector shouldBe Vector("mock3")
    val index3 = index1 ++ index2
    index3.ids.toVector shouldBe
      Vector(Id("mock"), Id("mocks"), Id("other-mock"), Id("last-mock"))
    index3.find(Id("foo")) shouldBe None
    index3.find(Id("mock")) shouldBe Some("mock1")
    index3.find(Id("mocks")) shouldBe Some("mock1")
    index3.find(Id("other-mock")) shouldBe Some("mock2")
    index3.find(Id("last-mock")) shouldBe Some("mock3")
    index3.list.toVector shouldBe Vector("mock1", "mock2", "mock3")
  }

  it should "detect duplicate IDs in resources" in {
    an[IllegalStateException] should be thrownBy
      Index[String]("index-test-3", classOf[IndexSpec].getClassLoader)(Parse)
  }

  it should "detect duplicate IDs in aggregations" in {
    an[IllegalStateException] should be thrownBy {
      Index[String]("index-test-1", classOf[IndexSpec].getClassLoader)(Parse) ++
        Index[String]("index-test-4", classOf[IndexSpec].getClassLoader)(Parse)
    }
  }

}
