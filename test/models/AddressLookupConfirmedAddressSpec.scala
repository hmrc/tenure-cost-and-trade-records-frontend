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

package models

import connectors.addressLookup.*
import utils.TestBaseSpec
import play.api.libs.json.Json

class AddressLookupConfirmedAddressSpec extends TestBaseSpec {

  val testAddress =
    Json.parse(input = """{
        |"auditRef": "e9e2fb3f-268f-4c4c-b928-3dc0b17259f2",
        |"address": {
        |   "lines": ["Line1","Line2","Line3","Line4"],
        |   "postcode":"NE1 1LX",
        |   "country": {
        |       "code": "GB",
        |       "name": "United Kingdom"
        |   }
        |}
        |}""".stripMargin)

  "AddressLookup" must {
    "return correct string format when passed asString from Json" in {
      val address = testAddress.as[AddressLookupConfirmedAddress]
      address.auditRef shouldBe "e9e2fb3f-268f-4c4c-b928-3dc0b17259f2"
    }
  }
}
