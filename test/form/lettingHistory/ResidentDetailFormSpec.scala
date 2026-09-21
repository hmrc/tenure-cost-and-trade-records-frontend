/*
 * Copyright 2026 HM Revenue & Customs
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

import form.lettingHistory.ResidentDetailForm.theForm
import models.submissions.lettingHistory.ResidentDetail
import test.FormSpec

class ResidentDetailFormSpec extends FormSpec:

  "ResidentDetailForm" should {
    "bind good data as expected" in {
      val data  = Map(
        "name"    -> "name",
        "address" -> "address"
      )
      val bound = theForm.bind(data)

      bound.hasErrors shouldBe false
      bound.data      shouldBe data
    }

    "unbind good data as expected" in {
      val residentDetail = ResidentDetail(
        name = "name",
        address = "address"
      )
      val filled         = theForm.fill(residentDetail)

      filled.hasErrors shouldBe false
      filled.data      shouldBe Map(
        "name"    -> "name",
        "address" -> "address"
      )
    }

    "detect errors" in {
      val bound = theForm.bind(
        Map(
          "name"    -> "",
          "address" -> ""
        )
      )

      bound.hasErrors                    shouldBe true
      bound.errors                         should have size 2
      bound.error("name").get.message    shouldBe "lettingHistory.residentDetail.name.required"
      bound.error("address").get.message shouldBe "lettingHistory.residentDetail.address.required"
    }
  }
