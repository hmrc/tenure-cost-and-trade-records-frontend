/*
 * Copyright 2023 HM Revenue & Customs
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

import models.submissions.aboutYourLeaseOrTenure.LandlordAddress
import models.submissions.aboutfranchisesorlettings.{CateringAddress, LettingAddress}
import models.submissions.common.Address
import utils.TestBaseSpec

class AddressesSpec extends TestBaseSpec {
  // Test Address
  val address = Address("001", Some("GORING ROAD"), Some("GORING-BY-SEA, WORTHING"), "BN12 4AX")

  "Address" should {
    "return the address as a single line" in {
      val result = address.singleLine
      result shouldBe "001, GORING ROAD, GORING-BY-SEA, WORTHING, BN12 4AX"
    }
    "return the address as a multi line" in {
      val result = address.multiLine
      result shouldBe "001<br/> GORING ROAD<br/> GORING-BY-SEA, WORTHING<br/> BN12 4AX"
    }
  }

  // Test Catering Address
  val cateringAddress =
    CateringAddress("001", Some("GORING ROAD"), Some("GORING-BY-SEA, WORTHING"), Some("SUSSEX"), "BN12 4AX")

  "CateringAddress" should {
    "return the address as a single line" in {
      val result = cateringAddress.singleLine
      result shouldBe "001, GORING ROAD, GORING-BY-SEA, WORTHING, SUSSEX, BN12 4AX"
    }
    "return the address as a multi line" in {
      val result = cateringAddress.multiLine
      result shouldBe "001<br/> GORING ROAD<br/> GORING-BY-SEA, WORTHING<br/> SUSSEX<br/> BN12 4AX"
    }
  }

  // Test Land Address
  val landAddress =
    LandlordAddress("001", Some("GORING ROAD"), Some("GORING-BY-SEA, WORTHING"), Some("SUSSEX"), "BN12 4AX")

  "LandlordAddress" should {
    "return the address as a single line" in {
      val result = landAddress.singleLine
      result shouldBe "001, GORING ROAD, GORING-BY-SEA, WORTHING, SUSSEX, BN12 4AX"
    }
    "return the address as a multi line" in {
      val result = landAddress.multiLine
      result shouldBe "001<br/> GORING ROAD<br/> GORING-BY-SEA, WORTHING<br/> SUSSEX<br/> BN12 4AX"
    }
  }

  // Test Letting Address
  val lettingAddress =
    LettingAddress("001", Some("GORING ROAD"), Some("GORING-BY-SEA, WORTHING"), Some("SUSSEX"), "BN12 4AX")

  "LettingAddress" should {
    "return the address as a single line" in {
      val result = lettingAddress.singleLine
      result shouldBe "001, GORING ROAD, GORING-BY-SEA, WORTHING, SUSSEX, BN12 4AX"
    }
    "return the address as a multi line" in {
      val result = lettingAddress.multiLine
      result shouldBe "001<br/> GORING ROAD<br/> GORING-BY-SEA, WORTHING<br/> SUSSEX<br/> BN12 4AX"
    }
  }

}
