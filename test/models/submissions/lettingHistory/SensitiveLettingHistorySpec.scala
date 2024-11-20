/*
 * Copyright 2024 HM Revenue & Customs
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

import com.typesafe.config.ConfigFactory
import crypto.MongoCrypto
import models.submissions.common.AnswerYes
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.OptionValues
import play.api.Configuration
import play.api.libs.json.{JsSuccess, Json}

class SensitiveLettingHistorySpec extends AnyFlatSpec with Matchers with OptionValues:

  given MongoCrypto = new MongoCrypto(Configuration(ConfigFactory.load()))

  it should "encrypt and decrypt sensitive fields correctly" in {
    val encryptedLettingHistory = SensitiveLettingHistory(clearLettingHistory)
    encryptedLettingHistory.decryptedValue mustBe clearLettingHistory
  }

  it should "serialize to encrypted JSON" in {
    val encryptedValue = SensitiveLettingHistory(clearLettingHistory)
    val jsonValue      = Json.toJson(encryptedValue)

    (jsonValue \ "hasPermanentResidents").as[String] mustBe "yes"
    val encryptedResidentDetail = (jsonValue \ "permanentResidents").head.as[SensitiveResidentDetail]
    encryptedResidentDetail.name    must not be clearLettingHistory.permanentResidents.head.name
    encryptedResidentDetail.address must not be clearLettingHistory.permanentResidents.head.address

    (jsonValue \ "hasCompletedLettings").as[String] mustBe "yes"
    val encryptedOccupierDetails = (jsonValue \ "completedLettings").head.as[SensitiveOccupierDetail]
    encryptedOccupierDetails.name                 must not be clearLettingHistory.completedLettings.head.name
    encryptedOccupierDetails.address.line1        must not be clearLettingHistory.completedLettings.head.address.line1
    encryptedOccupierDetails.address.line2.value  must not be clearLettingHistory.completedLettings.head.address.line2.value
    encryptedOccupierDetails.address.town         must not be clearLettingHistory.completedLettings.head.address.town
    encryptedOccupierDetails.address.county.value must not be clearLettingHistory.completedLettings.head.address.county.value
    encryptedOccupierDetails.address.postcode     must not be clearLettingHistory.completedLettings.head.address.postcode
  }

  it should "deserialize from encrypted JSON" in {
    val encryptedLettingHistory = SensitiveLettingHistory(clearLettingHistory)
    val jsonValue               = Json.toJson(encryptedLettingHistory)
    Json.fromJson[SensitiveLettingHistory](jsonValue) mustBe JsSuccess(encryptedLettingHistory)
  }

  val clearLettingHistory = LettingHistory(
    hasPermanentResidents = Some(AnswerYes),
    permanentResidents = List(
      ResidentDetail(
        name = "Mr. Peter Pan",
        address = "20, Fantasy Street, Birds' Island, BIR067"
      )
    ),
    hasCompletedLettings = Some(AnswerYes),
    completedLettings = List(
      OccupierDetail(
        name = "Miss Nobody",
        address = Address(
          line1 = "21, Somewhere Place",
          line2 = Some("Basement"),
          town = "NeverTown",
          county = Some("Birds' Island"),
          postcode = "BN124AX"
        )
      )
    )
  )
