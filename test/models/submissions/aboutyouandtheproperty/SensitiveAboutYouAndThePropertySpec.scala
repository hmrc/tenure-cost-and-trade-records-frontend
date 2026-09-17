/*
 * Copyright 2026 HM Revenue & Customs
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

import models.submissions.common.*
import test.MongoCryptoSupport
import uk.gov.hmrc.vo.unit.test.BaseSpec

class SensitiveAboutYouAndThePropertySpec extends BaseSpec with MongoCryptoSupport:

  "SensitiveAboutYouAndTheProperty" should {
    "encrypt and decrypt sensitive fields correctly" in {
      val originalAboutYouAndTheProperty  = AboutYouAndTheProperty(
        customerDetails = Some(
          CustomerDetails(
            fullName = "John Doe",
            contactDetails = ContactDetails(
              phone = "123456789",
              email = "test@example.com"
            )
          )
        )
      )
      val sensitiveAboutYouAndTheProperty = SensitiveAboutYouAndTheProperty(originalAboutYouAndTheProperty)

      sensitiveAboutYouAndTheProperty.customerDetails.get.isInstanceOf[SensitiveCustomerDetails] shouldBe true

      sensitiveAboutYouAndTheProperty.decryptedValue shouldBe originalAboutYouAndTheProperty
    }
  }
