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
import crypto.MongoCrypto
import models.submissions.aboutyouandtheproperty.{AlternativeAddress, AlternativeContactDetails, SensitiveAlternativeContactDetails}
import uk.gov.hmrc.crypto.Sensitive.SensitiveString
import utils.SensitiveTestHelper

class SensitiveAdditionalInformationSpec extends AnyWordSpecLike with Matchers with SensitiveTestHelper {

  val testConfig: Configuration    = loadTestConfig()
  implicit val crypto: MongoCrypto = createTestMongoCrypto(testConfig)

  "SensitiveAdditionalInformation" should {

    "encrypt and decrypt sensitive fields correctly" in {
      val originalAdditionalInformation = AdditionalInformation(
        furtherInformationOrRemarksDetails = None,
        checkYourAnswersAdditionalInformation = None
      )

      val sensitiveAdditionalInformation = SensitiveAdditionalInformation(originalAdditionalInformation)

      sensitiveAdditionalInformation.furtherInformationOrRemarksDetails
        .isInstanceOf[Option[SensitiveString]] shouldBe true

      sensitiveAdditionalInformation.decryptedValue shouldBe originalAdditionalInformation
    }

  }
}
