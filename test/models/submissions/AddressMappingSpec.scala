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

package models.submissions

import models.submissions.common.Address
import play.api.libs.json.{JsResult, JsSuccess, Json}
import utils.TestBaseSpec

class AddressMappingSpec extends TestBaseSpec {

  val toBeDeserialized = """{"buildingNameNumber":"Some House","street1":"Some Street","street2":"Some City","county":"Some County","postcode":"AA11 1AA"}"""
  val expected = Address(
    buildingNameNumber = "Some House",
    street1 = Some("Some Street"),
    town = "Some City",
    county = Some("Some County"),
    postcode = "AA11 1AA"
  )

  def toJson(data: Address): String = {
    val json = Json.toJson(data).toString
    json
  }

  def fromJson(json: String): JsResult[Address] = {
    val parsed = Json.parse(json)
    Json.fromJson[Address](parsed)
  }

  "Address with a fully filled in address" should {
    "create a fully filled Address" in {
      val actual = fromJson(toBeDeserialized)
      actual should be(JsSuccess(expected))
    }
  }

}
