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

package models.submissions.aboutyouandtheproperty

import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.matchers.should.Matchers
import play.api.Configuration
import uk.gov.hmrc.crypto.Sensitive.SensitiveString
import models.submissions.common.{ContactDetails, SensitiveContactDetails}
import crypto.MongoCrypto
import utils.SensitiveTestHelper

class SensitiveCustomerDetailsSpec extends AnyWordSpecLike with Matchers with SensitiveTestHelper {

  val testConfig: Configuration    = loadTestConfig()
  implicit val crypto: MongoCrypto = createTestMongoCrypto(testConfig)

  "SensitiveCustomerDetails" should {

    "encrypt and decrypt customer details correctly" in {
      val originalCustomerDetails = CustomerDetails(
        fullName = "John Doe",
        contactDetails = ContactDetails(
          phone = "123456789",
          email = "test@example.com"
        )
      )

      val sensitiveCustomerDetails = SensitiveCustomerDetails(originalCustomerDetails)

      sensitiveCustomerDetails.fullName.isInstanceOf[SensitiveString]               shouldBe true
      sensitiveCustomerDetails.contactDetails.isInstanceOf[SensitiveContactDetails] shouldBe true

      sensitiveCustomerDetails.decryptedValue shouldBe originalCustomerDetails
    }

  }
}
