/*
 * Copyright 2025 HM Revenue & Customs
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

import crypto.MongoCrypto
import models.submissions.common.AnswersYesNo.*
import models.submissions.common.{Address, ContactDetails}
import play.api.Configuration
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import utils.SensitiveTestHelper

import java.time.LocalDate

class SensitiveStillConnectedDetailsSpec extends AnyWordSpecLike with Matchers with SensitiveTestHelper:

  val testConfig: Configuration = loadTestConfig()
  given MongoCrypto             = createTestMongoCrypto(testConfig)

  "SensitiveStillConnectedDetails" should {

    "encrypt and decrypt sensitive fields correctly" in {
      val clearData     = StillConnectedDetails(
        addressConnectionType = Some(AddressConnectionType.AddressConnectionTypeYesChangeAddress),
        connectionToProperty = Some(ConnectionToProperty.ConnectionToThePropertyOccupierAgent),
        editAddress =
          Some(EditTheAddress(Address("buildingNameNumber", Some("street1"), "town", Some("county"), "postcode"))),
        isPropertyVacant = Some(AnswerNo),
        tradingNameOperatingFromProperty = Some(TradingNameOperatingFromProperty("tradingName")),
        tradingNameOwnTheProperty = Some(AnswerNo),
        tradingNamePayingRent = Some(AnswerYes),
        areYouThirdParty = Some(AnswerYes),
        vacantPropertyStartDate = Some(StartDateOfVacantProperty(LocalDate.of(2023, 1, 1))),
        isAnyRentReceived = Some(AnswerYes),
        provideContactDetails = Some(
          ProvideContactDetails(
            YourContactDetails(
              fullName = "John Doe",
              contactDetails = ContactDetails("phoneNumber", "emailAddress"),
              additionalInformation = None
            )
          )
        ),
        lettingPartOfPropertyDetailsIndex = 0,
        maxOfLettings = Some(false),
        lettingPartOfPropertyDetails = IndexedSeq(
          LettingPartOfPropertyDetails(
            tenantDetails = TenantDetails("name", "descriptionOfLettings", correspondenceAddress = None),
            lettingPartOfPropertyRentDetails = None,
            itemsIncludedInRent = List("item1", "item2"),
            addAnotherLettingToProperty = Some(AnswerNo),
            maxOfLettings = Some(true)
          )
        ),
        checkYourAnswersConnectionToProperty =
          Some(CheckYourAnswersConnectionToProperty(AnswerYes, Some(true)))
      )
      val encryptedData = SensitiveStillConnectedDetails(clearData)
      val d             = encryptedData.decryptedValue
      d shouldBe clearData
    }
  }
