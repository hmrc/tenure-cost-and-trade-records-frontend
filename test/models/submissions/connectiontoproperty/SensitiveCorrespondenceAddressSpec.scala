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

import models.submissions.MongoCryptoSupport
import org.scalatest.OptionValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.{JsSuccess, Json}

class SensitiveCorrespondenceAddressSpec extends AnyFlatSpec with Matchers with OptionValues with MongoCryptoSupport:

  it should "encrypt and decrypt sensitive fields correctly" in {
    val encryptedAddress = SensitiveCorrespondenceAddress(clearAddress)
    encryptedAddress.decryptedValue shouldBe clearAddress
  }

  it should "serialize to encrypted JSON" in {
    val encryptedValue = SensitiveCorrespondenceAddress(clearAddress)
    val jsValue        = Json.toJson(encryptedValue)

    (jsValue \ "buildingNameNumber").as[String] should not be clearAddress.buildingNameNumber
    (jsValue \ "street1").as[String]            should not be clearAddress.street1.get
    (jsValue \ "town").as[String]               should not be clearAddress.town
    (jsValue \ "county").as[String]             should not be clearAddress.county.get
    (jsValue \ "postcode").as[String]           should not be clearAddress.postcode
  }

  it should "deserialize from encrypted JSON" in {
    val encryptedAddress = SensitiveCorrespondenceAddress(clearAddress)
    val jsValue          = Json.toJson(encryptedAddress)
    val deserialized     = Json.fromJson[SensitiveCorrespondenceAddress](jsValue)
    deserialized shouldBe JsSuccess(encryptedAddress)
  }

  val clearAddress = CorrespondenceAddress(
    buildingNameNumber = "1",
    street1 = Some("Street 1"),
    town = "Town",
    county = Some("County"),
    postcode = "AB12 3CD"
  )
