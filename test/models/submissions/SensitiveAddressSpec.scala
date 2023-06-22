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

import crypto.MongoCrypto
import models.submissions.common.{Address, SensitiveAddress}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.{Configuration, Environment, Mode}
import uk.gov.hmrc.crypto.Sensitive.SensitiveString
import utils.SensitiveTestHelper

class SensitiveAddressSpec extends AnyWordSpecLike with Matchers with SensitiveTestHelper {
  val testConfig: Configuration = loadTestConfig()
  implicit val crypto: MongoCrypto = new TestMongoCrypto(testConfig)

  "SensitiveAddress" should {

    "encrypt and decrypt address fields correctly" in {
      val originalAddress = Address(
        buildingNameNumber = "123",
        street1 = Some("Street 1"),
        street2 = "Street 2",
        county = Some("County"),
        postcode = "12345"
      )

      val sensitiveAddress = SensitiveAddress(originalAddress)

      // Ensure the sensitive fields are encrypted
      sensitiveAddress.buildingNameNumber.isInstanceOf[SensitiveString] shouldBe true
      sensitiveAddress.street2.isInstanceOf[SensitiveString] shouldBe true
      sensitiveAddress.postcode.isInstanceOf[SensitiveString] shouldBe true

      // Ensure the sensitive fields are decrypted correctly
      sensitiveAddress.decryptedValue shouldBe originalAddress
    }

  }
}