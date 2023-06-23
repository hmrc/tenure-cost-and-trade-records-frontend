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

package models.submissions.aboutYourLeaseOrTenure

import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.matchers.should.Matchers
import play.api.Configuration
import uk.gov.hmrc.crypto.Sensitive.SensitiveString
import crypto.MongoCrypto
import utils.SensitiveTestHelper

class SensitiveAboutTheLandlordSpec extends AnyWordSpecLike with Matchers with SensitiveTestHelper {

  val testConfig: Configuration = loadTestConfig()
  implicit val crypto: MongoCrypto = createTestMongoCrypto(testConfig)

  "SensitiveAboutTheLandlord" should {

    "encrypt and decrypt sensitive fields correctly" in {
      val originalAboutTheLandlord = AboutTheLandlord(
        landlordFullName = "John Doe",
        landlordAddress = LandlordAddress(
          buildingNameNumber = "123",
          street1 = Some("Street 1"),
          town = "Town",
          county = Some("County"),
          postcode = "12345"
        )
      )

      val sensitiveAboutTheLandlord = SensitiveAboutTheLandlord(originalAboutTheLandlord)

      // Ensure the sensitive fields are encrypted
      sensitiveAboutTheLandlord.landlordFullName.isInstanceOf[SensitiveString] shouldBe true
      sensitiveAboutTheLandlord.landlordAddress.isInstanceOf[SensitiveLandlordAddress] shouldBe true

      // Ensure the sensitive fields are decrypted correctly
      sensitiveAboutTheLandlord.decryptedValue shouldBe originalAboutTheLandlord
    }

  }
}

