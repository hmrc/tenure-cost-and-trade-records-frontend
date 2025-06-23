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

package models.submissions.requestReferenceNumber

import models.submissions.MongoCryptoSupport
import models.submissions.common.{Address, ContactDetails}
import org.scalatest.OptionValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.{JsSuccess, Json}

class SensitiveRequestReferenceNumberDetailsSpec
    extends AnyFlatSpec
    with Matchers
    with OptionValues
    with MongoCryptoSupport:

  it should "encrypt and decrypt sensitive fields correctly" in {
    val encryptedDetails = SensitiveRequestReferenceNumberDetails(clearDetails)
    encryptedDetails.decryptedValue shouldBe clearDetails
  }

  it should "serialize to encrypted JSON" in
    pending

  it should "deserialize from encrypted JSON" in {
    val encryptedDetails = SensitiveRequestReferenceNumberDetails(clearDetails)
    val jsValue          = Json.toJson(encryptedDetails)
    val deserialized     = Json.fromJson[SensitiveRequestReferenceNumberDetails](jsValue)
    deserialized shouldBe JsSuccess(encryptedDetails)
  }

  val clearDetails = RequestReferenceNumberDetails(
    propertyDetails = Some(
      RequestReferenceNumberPropertyDetails(
        businessTradingName = "Business Name",
        address = Some(
          Address(
            buildingNameNumber = "1",
            street1 = Some("Street 1"),
            town = "Town",
            county = Some("County"),
            postcode = "AB12 3CD"
          )
        )
      )
    ),
    contactDetails = Some(
      RequestReferenceNumberContactDetails(
        noReferenceNumberFullName = "Full Name",
        noReferenceNumberContactDetails = ContactDetails(
          phone = "1234567890",
          email = "whoami@email.com"
        ),
        noReferenceNumberAdditionalInfo = Some("Additional Info")
      )
    ),
    checkYourAnswers = Some(RequestReferenceNumberCheckYourAnswers("true"))
  )
