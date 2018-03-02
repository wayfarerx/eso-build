/*
 * CategorySpec.scala
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
 * Test suite for the Category type.
 */
class CategorySpec extends FlatSpec with Matchers {

  behavior of "Category"

  val Parse: String => String = identity

  implicit val loader: Asset.Loader = Asset.Loader(classOf[CategorySpec].getClassLoader)

  it should "Load resources from the classpath" in {
    val index1 = Category[String](Asset("index-test-1"))(Parse)
    index1.ids shouldBe Set(Id("mock"), Id("mocks"), Id("other-mock"))
    index1.find(Id("foo")) shouldBe None
    index1.find(Id("mock")) shouldBe Some("# Mock(s)")
    index1.find(Id("mocks")) shouldBe Some("# Mock(s)")
    index1.find(Id("other-mock")) shouldBe Some("# Other Mock")
    index1.list.toVector shouldBe Vector("# Mock(s)", "# Other Mock")
    val index2 = Category[String](Asset("index-test-2"))(Parse)
    index2.ids shouldBe Set(Id("last-mock"))
    index2.find(Id("foo")) shouldBe None
    index2.find(Id("mock")) shouldBe None
    index2.find(Id("mocks")) shouldBe None
    index2.find(Id("other-mock")) shouldBe None
    index2.find(Id("last-mock")) shouldBe Some("# Last Mock")
    index2.list.toVector shouldBe Vector("# Last Mock")
    val index3 = index1 ++ index2
    index3.ids shouldBe Set(Id("mock"), Id("mocks"), Id("other-mock"), Id("last-mock"))
    index3.find(Id("foo")) shouldBe None
    index3.find(Id("mock")) shouldBe Some("# Mock(s)")
    index3.find(Id("mocks")) shouldBe Some("# Mock(s)")
    index3.find(Id("other-mock")) shouldBe Some("# Other Mock")
    index3.find(Id("last-mock")) shouldBe Some("# Last Mock")
    index3.list.toVector shouldBe Vector("# Mock(s)", "# Other Mock", "# Last Mock")
  }

  it should "detect duplicate IDs in resources" in {
    an[IllegalStateException] should be thrownBy
      Category[String](Asset("index-test-3"))(Parse)
  }

  it should "detect duplicate IDs in aggregations" in {
    an[IllegalStateException] should be thrownBy {
      Category[String](Asset("index-test-1"))(Parse) ++
        Category[String](Asset("index-test-4"))(Parse)
    }
  }

}
