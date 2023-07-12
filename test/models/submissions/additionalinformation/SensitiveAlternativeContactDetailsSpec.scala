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

package models.submissions.additionalinformation

import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.matchers.should.Matchers
import play.api.Configuration
import models.submissions.common._
import crypto.MongoCrypto
import uk.gov.hmrc.crypto.Sensitive.SensitiveString
import utils.SensitiveTestHelper

class SensitiveAlternativeContactDetailsSpec extends AnyWordSpecLike with Matchers with SensitiveTestHelper {

  val testConfig: Configuration    = loadTestConfig()
  implicit val crypto: MongoCrypto = createTestMongoCrypto(testConfig)

  "SensitiveAlternativeContactDetails" should {

    "encrypt and decrypt sensitive fields correctly" in {
      val originalAlternativeContactDetails = AlternativeContactDetails(
        alternativeContactFullName = Some("John Doe"),
        alternativeContactDetails = Some(
          ContactDetails(
            phone = "1234567890",
            email = "johndoe@example.com"
          )
        ),
        alternativeContactAddress = Some(
          AlternativeAddress(
            buildingNameNumber = "123",
            street1 = Some("Street 1"),
            town = "Town",
            county = Some("County"),
            postcode = "12345"
          )
        )
      )

      val sensitiveAlternativeContactDetails = SensitiveAlternativeContactDetails(originalAlternativeContactDetails)

      sensitiveAlternativeContactDetails.alternativeContactFullName.isInstanceOf[Option[SensitiveString]] shouldBe true
      sensitiveAlternativeContactDetails.alternativeContactDetails
        .isInstanceOf[Option[SensitiveContactDetails]]                                                    shouldBe true
      sensitiveAlternativeContactDetails.alternativeContactAddress
        .isInstanceOf[Option[SensitiveContactDetailsAddress]]                                             shouldBe true

      sensitiveAlternativeContactDetails.decryptedValue shouldBe originalAlternativeContactDetails
    }

  }
}
