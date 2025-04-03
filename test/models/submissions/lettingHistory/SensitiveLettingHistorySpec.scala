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

package models.submissions.lettingHistory

import models.submissions.MongoCryptoSupport
import org.scalatest.OptionValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.{JsSuccess, Json}

import java.time.LocalDate

class SensitiveLettingHistorySpec extends AnyFlatSpec with Matchers with OptionValues with MongoCryptoSupport:

  it should "encrypt and decrypt sensitive fields correctly" in {
    val encryptedLettingHistory = SensitiveLettingHistory(clearLettingHistory)
    encryptedLettingHistory.decryptedValue shouldBe clearLettingHistory
  }

  it should "serialize to encrypted JSON" in {
    val encryptedValue = SensitiveLettingHistory(clearLettingHistory)
    val jsValue        = Json.toJson(encryptedValue)

    (jsValue \ "hasPermanentResidents").as[Boolean] shouldBe true
    val encryptedResidentDetail = (jsValue \ "permanentResidents").head.as[SensitiveResidentDetail]
    encryptedResidentDetail.name    should not be clearLettingHistory.permanentResidents.head.name
    encryptedResidentDetail.address should not be clearLettingHistory.permanentResidents.head.address

    (jsValue \ "hasCompletedLettings").as[Boolean] shouldBe true
    val encryptedOccupierDetails = (jsValue \ "completedLettings").head.as[SensitiveOccupierDetail]
    encryptedOccupierDetails.name                    should not be clearLettingHistory.completedLettings.head.name
    encryptedOccupierDetails.address.line1           should not be clearLettingHistory.completedLettings.head.address.line1
    encryptedOccupierDetails.address.line2.value     should not be clearLettingHistory.completedLettings.head.address.line2.value
    encryptedOccupierDetails.address.town            should not be clearLettingHistory.completedLettings.head.address.town
    encryptedOccupierDetails.address.county.value    should not be clearLettingHistory.completedLettings.head.address.county.value
    encryptedOccupierDetails.address.postcode        should not be clearLettingHistory.completedLettings.head.address.postcode
    encryptedOccupierDetails.rental.isDefined      shouldBe true
    encryptedOccupierDetails.rental.value.fromDate shouldBe clearLettingHistory.completedLettings.head.rentalPeriod.value.fromDate
    encryptedOccupierDetails.rental.value.toDate   shouldBe clearLettingHistory.completedLettings.head.rentalPeriod.value.toDate
  }

  it should "deserialize from encrypted JSON" in {
    val encryptedLettingHistory = SensitiveLettingHistory(clearLettingHistory)
    val jsValue                 = Json.toJson(encryptedLettingHistory)
    val deserialized            = Json.fromJson[SensitiveLettingHistory](jsValue)
    deserialized shouldBe JsSuccess(encryptedLettingHistory)
  }

  val clearLettingHistory = LettingHistory(
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
        address = Address(
          line1 = "21, Somewhere Place",
          line2 = Some("Basement"),
          town = "NeverTown",
          county = Some("Birds' Island"),
          postcode = "BN124AX"
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
