/*
 * Copyright 2022 HM Revenue & Customs
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

package models.submissions

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class AddressSpec extends AnyFlatSpec with Matchers {
  val address = Address("001", Some("GORING ROAD"), Some("GORING-BY-SEA, WORTHING"), "BN12 4AX")

  "Address" should "return the address as a single line" in {
    val result = address.singleLine
    result shouldBe "001, GORING ROAD, GORING-BY-SEA, WORTHING, BN12 4AX"
  }

  "Address" should "return the address as a multi line" in {
    val result = address.multiLine
    println(result)
    result shouldBe "001<br/> GORING ROAD<br/> GORING-BY-SEA, WORTHING<br/> BN12 4AX"
  }

}
