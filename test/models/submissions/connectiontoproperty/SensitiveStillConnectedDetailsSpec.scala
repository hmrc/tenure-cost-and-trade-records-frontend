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

package models.submissions.connectiontoproperty

import models.submissions.common.AnswersYesNo.*
import models.submissions.common.{Address, CheckYourAnswersAndConfirm, ContactDetails}
import test.SensitiveTestHelper
import uk.gov.hmrc.vo.unit.test.BaseSpec

import java.time.LocalDate

class SensitiveStillConnectedDetailsSpec extends BaseSpec with SensitiveTestHelper:

  "SensitiveStillConnectedDetails" should:
    "encrypt and decrypt sensitive fields correctly" in {
      val clearData     = StillConnectedDetails(
        addressConnectionType = Some(AddressConnectionType.AddressConnectionTypeYesChangeAddress),
        connectionToProperty = Some(ConnectionToProperty.ConnectionToThePropertyOccupierAgent),
        editAddress = Some(Address("buildingNameNumber", Some("street1"), "town", Some("county"), "postcode")),
        isPropertyVacant = Some(AnswerNo),
        tradingNameOperatingFromProperty = Some("tradingName"),
        tradingNameOwnTheProperty = Some(AnswerNo),
        tradingNamePayingRent = Some(AnswerYes),
        areYouThirdParty = Some(AnswerYes),
        vacantPropertyStartDate = Some(LocalDate.of(2023, 1, 1)),
        isAnyRentReceived = Some(AnswerYes),
        provideContactDetails = Some(
          YourContactDetails(
            fullName = "John Doe",
            contactDetails = ContactDetails("phoneNumber", "emailAddress"),
            additionalInformation = None
          )
        ),
        maxOfLettings = Some(false),
        lettingPartOfPropertyDetails = IndexedSeq(
          LettingPartOfPropertyDetails(
            tenantDetails = TenantDetails(
              "name",
              "descriptionOfLettings",
              correspondenceAddress =
                Some(Address("buildingNameNumber", Some("street1"), "town", Some("county"), "postcode"))
            ),
            lettingPartOfPropertyRentDetails = None,
            itemsIncludedInRent = List("item1", "item2"),
            addAnotherLettingToProperty = Some(AnswerNo),
            maxOfLettings = Some(true)
          )
        ),
        checkYourAnswersConnectionToProperty = Some(CheckYourAnswersAndConfirm(AnswerYes, Some(true)))
      )
      val encryptedData = SensitiveStillConnectedDetails(clearData)

      encryptedData.decryptedValue shouldBe clearData
    }
