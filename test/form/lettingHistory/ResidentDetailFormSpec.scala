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

package form.lettingHistory

import models.submissions.lettingHistory.ResidentDetail
import form.lettingHistory.ResidentDetailForm.theForm

class ResidentDetailFormSpec extends FormSpec:

  it should "bind good data as expected" in {
    val data  = Map(
      "name"    -> "name",
      "address" -> "address"
    )
    val bound = theForm.bind(data)
    bound.hasErrors mustBe false
    bound.data mustBe data
  }

  it should "unbind good data as expected" in {
    val residentDetail = ResidentDetail(
      name = "name",
      address = "address"
    )
    val filled         = theForm.fill(residentDetail)
    filled.hasErrors mustBe false
    filled.data mustBe Map(
      "name"    -> "name",
      "address" -> "address"
    )
  }

  it should "detect errors" in {
    // When the form gets submitted before being filled
    val bound = theForm.bind(
      Map(
        "name"    -> "",
        "address" -> ""
      )
    )
    bound.hasErrors mustBe true
    bound.errors must have size 2
    bound.error("name").value.message mustBe "lettingHistory.residentDetail.name.required"
    bound.error("address").value.message mustBe "lettingHistory.residentDetail.address.required"
  }
