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

package models.submissions.notconnected

import models.submissions.MongoCryptoSupport
import models.submissions.common.ContactDetails
import models.submissions.notconnected.RemoveConnectionsDetails.*
import org.scalatest.OptionValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.{JsSuccess, Json}

class SensitiveRemoveConnectionDetailsSpec extends AnyFlatSpec with Matchers with OptionValues with MongoCryptoSupport:

  it should "encrypt and decrypt sensitive fields correctly" in {
    val encryptedDetails = SensitiveRemoveConnectionDetails(clearEnvelope)
    encryptedDetails.decryptedValue shouldBe clearEnvelope
  }

  it should "serialize to encrypted JSON" in {
    val encryptedValue = SensitiveRemoveConnectionDetails(clearEnvelope)
    val jsValue        = Json.toJson(encryptedValue)

    val removeConnectionsDetails = (jsValue \ "removeConnectionDetails").as[RemoveConnectionsDetails]
    removeConnectionsDetails.removeConnectionFullName         should not be clearEnvelope.removeConnectionDetails.get.removeConnectionFullName
    removeConnectionsDetails.removeConnectionDetails.email    should not be clearEnvelope.removeConnectionDetails.get.removeConnectionDetails.email
    removeConnectionsDetails.removeConnectionDetails.phone    should not be clearEnvelope.removeConnectionDetails.get.removeConnectionDetails.phone
    removeConnectionsDetails.removeConnectionAdditionalInfo shouldBe clearEnvelope.removeConnectionDetails.get.removeConnectionAdditionalInfo

    val pastConnectionType = (jsValue \ "pastConnectionType").as[PastConnectionType]
    pastConnectionType.name shouldBe clearEnvelope.pastConnectionType.get.name
  }

  it should "deserialize from encrypted JSON" in {
    val encryptedDetails = SensitiveRemoveConnectionDetails(clearEnvelope)
    val jsValue          = Json.toJson(encryptedDetails)
    val deserialized     = Json.fromJson[SensitiveRemoveConnectionDetails](jsValue)
    deserialized shouldBe JsSuccess(encryptedDetails)
  }

  val clearEnvelope = RemoveConnectionDetails(
    removeConnectionDetails = Some(
      RemoveConnectionsDetails(
        removeConnectionFullName = "fullName",
        removeConnectionDetails = ContactDetails(
          phone = "01234567890",
          email = "info@foobar.com"
        ),
        removeConnectionAdditionalInfo = Some("additional information")
      )
    ),
    pastConnectionType = PastConnectionType.fromName("yes")
  )
