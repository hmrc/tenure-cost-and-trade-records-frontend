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

package form.lettingHistory

import form.lettingHistory.OccupierDetailForm.theForm
import models.submissions.lettingHistory.{Address, OccupierDetail}

class OccupierDetailFormSpec extends FormSpec:

  it should "bind good data as expected" in {
    val data  = Map(
      "name"             -> "name",
      "address.line1"    -> "89, Fantasy Street",
      // "address.line2" -> "Basement",
      "address.town"     -> "Birds Island",
      // "address.county" -> "Neverland",
      "address.postcode" -> "BN124AX"
    )
    val bound = theForm.bind(data)
    bound.hasErrors mustBe false
    bound.data mustBe data
  }

  it should "unbind good data as expected" in {
    val occupierDetail = OccupierDetail(
      name = "name",
      address = Address(
        line1 = "89, Fantasy Street",
        line2 = None,
        town = "Birds Island",
        county = Some("Neverland"),
        postcode = "BN124AX"
      ),
      rental = None
    )
    val filled         = theForm.fill(occupierDetail)
    filled.hasErrors mustBe false
    filled.data mustBe Map(
      "name"             -> "name",
      "address.line1"    -> "89, Fantasy Street",
      "address.town"     -> "Birds Island",
      "address.county"   -> "Neverland",
      "address.postcode" -> "BN124AX"
    )
  }

  it should "detect errors" in {
    // When the form gets submitted even though "untouched"
    val bound = theForm.bind(
      Map(
        "name"             -> "",
        "address.line1"    -> "",
        "address.line2"    -> "",
        "address.town"     -> "",
        "address.county"   -> "",
        "address.postcode" -> ""
      )
    )
    bound.hasErrors mustBe true
    bound.errors must have size 4
    bound.error("name").value.message mustBe "lettingHistory.occupierDetail.name.required"
    bound.error("address.line1").value.message mustBe "error.buildingNameNumber.required"
    bound.error("address.town").value.message mustBe "error.townCity.required"
    bound.error("address.postcode").value.message mustBe "error.postcodeAlternativeContact.required"
  }
