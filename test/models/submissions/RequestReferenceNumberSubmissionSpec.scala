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

package models.submissions

import models.submissions.common.{Address, ContactDetails}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import play.api.libs.json.Json
import utils.FakeObjects

import java.time.Instant

/**
  * @author Yuriy Tumakha
  */
class RequestReferenceNumberSubmissionSpec extends AnyFlatSpec with should.Matchers with FakeObjects:

  "RequestReferenceNumberSubmission model" should "be serialized/deserialized from json" in {

    val requestReferenceNumberSubmissionModel = RequestReferenceNumberSubmission(
      "12345",
      "BusinessTradingName",
      Address(
        buildingNameNumber = "123",
        street1 = Some("Main Street"),
        town = "Bristol",
        county = Some("Bristol"),
        postcode = "AN12 3YZ"
      ),
      "Full Name",
      ContactDetails(
        "07711122233345",
        "test@email.co.uk"
      ),
      None,
      Instant.ofEpochMilli(1745326680),
      Some("en")
    )

    val jsonString =
      """{"businessTradingName":"BusinessTradingName","fullName":"Full Name","id":"12345","createdAt":"1970-01-21T04:48:46.680Z","address":{"postcode":"AN12 3YZ","street1":"Main Street","county":"Bristol","buildingNameNumber":"123","town":"Bristol"},"contactDetails":{"phone":"07711122233345","email":"test@email.co.uk"},"lang":"en"}"""

    val json = Json.toJson(requestReferenceNumberSubmissionModel)
    json.as[RequestReferenceNumberSubmission] shouldBe requestReferenceNumberSubmissionModel
    Json.stringify(json)                      shouldBe jsonString
  }
