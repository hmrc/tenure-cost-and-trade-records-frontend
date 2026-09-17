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

package models.submissions.lettingHistory

import play.api.libs.json.{JsSuccess, Json}
import test.MongoCryptoSupport
import uk.gov.hmrc.vo.unit.test.BaseSpec

import java.time.LocalDate

class SensitiveLettingHistorySpec extends BaseSpec with MongoCryptoSupport:

  private val clearLettingHistory: LettingHistory = LettingHistory(
    hasPermanentResidents = Some(true),
    permanentResidents = List(
      ResidentDetail(
        name = "Mr. Peter Pan",
        address = "20, Fantasy Street, Birds' Island, BIR067"
      )
    ),
    hasCompletedLettings = Some(true),
    completedLettings = List(
      OccupierDetail(
        name = "Miss Nobody",
        address = Some(
          OccupierAddress(
            buildingNameNumber = "21, Somewhere Place",
            street1 = Some("Basement"),
            town = "NeverTown",
            county = Some("Birds' Island"),
            postcode = "BN124AX"
          )
        ),
        rentalPeriod = Some(
          LocalPeriod(
            fromDate = LocalDate.of(2023, 4, 1),
            toDate = LocalDate.of(2024, 3, 31)
          )
        )
      )
    )
  )

  "SensitiveLettingHistory" should {
    "encrypt and decrypt sensitive fields correctly" in {
      val encryptedLettingHistory = SensitiveLettingHistory(clearLettingHistory)
      encryptedLettingHistory.decryptedValue shouldBe clearLettingHistory
    }

    "serialize to encrypted JSON" in {
      val encryptedValue = SensitiveLettingHistory(clearLettingHistory)
      val jsValue        = Json.toJson(encryptedValue)

      (jsValue \ "hasPermanentResidents").as[Boolean] shouldBe true
      val encryptedResidentDetail = (jsValue \ "permanentResidents").head.as[SensitiveResidentDetail]
      encryptedResidentDetail.name    should not be clearLettingHistory.permanentResidents.head.name
      encryptedResidentDetail.address should not be clearLettingHistory.permanentResidents.head.address

      (jsValue \ "hasCompletedLettings").as[Boolean] shouldBe true
      val encryptedOccupierDetails = (jsValue \ "completedLettings").head.as[SensitiveOccupierDetail]
      encryptedOccupierDetails.name                   should not be clearLettingHistory.completedLettings.head.name
      encryptedOccupierDetails.address.get.line1      should not be clearLettingHistory.completedLettings.head.address.get.buildingNameNumber
      encryptedOccupierDetails.address.get.line2.get  should not be clearLettingHistory.completedLettings.head.address.get.street1.get
      encryptedOccupierDetails.address.get.town       should not be clearLettingHistory.completedLettings.head.address.get.town
      encryptedOccupierDetails.address.get.county.get should not be clearLettingHistory.completedLettings.head.address.get.county.get
      encryptedOccupierDetails.address.get.postcode   should not be clearLettingHistory.completedLettings.head.address.get.postcode
      encryptedOccupierDetails.rental.isDefined     shouldBe true
      encryptedOccupierDetails.rental.get.fromDate  shouldBe clearLettingHistory.completedLettings.head.rentalPeriod.get.fromDate
      encryptedOccupierDetails.rental.get.toDate    shouldBe clearLettingHistory.completedLettings.head.rentalPeriod.get.toDate
    }

    "deserialize from encrypted JSON" in {
      val encryptedLettingHistory = SensitiveLettingHistory(clearLettingHistory)
      val jsValue                 = Json.toJson(encryptedLettingHistory)
      val deserialized            = Json.fromJson[SensitiveLettingHistory](jsValue)
      deserialized shouldBe JsSuccess(encryptedLettingHistory)
    }
  }
