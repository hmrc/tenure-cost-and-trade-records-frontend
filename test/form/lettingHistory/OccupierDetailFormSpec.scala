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

import form.lettingHistory.OccupierDetailForm.theForm
import models.submissions.lettingHistory.{OccupierAddress, OccupierDetail}
import test.FormSpec

import scala.language.implicitConversions

class OccupierDetailFormSpec extends FormSpec:

  "OccupierDetailForm" should {
    "bind good data as expected" in {
      val data  = Map(
        "name" -> "name"
      )
      val bound = theForm.bind(data)

      bound.hasErrors shouldBe false
      bound.data      shouldBe data
    }

    "unbind good data as expected" in {
      val occupierDetail = OccupierDetail(
        name = "name",
        address =
          OccupierAddress(
            buildingNameNumber = "89, Fantasy Street",
            street1 = None,
            town = "Birds Island",
            county = "Neverland",
            postcode = "BN124AX"
          ),
        rentalPeriod = None
      )
      val filled         = theForm.fill(occupierDetail)

      filled.hasErrors shouldBe false
      filled.data      shouldBe Map("name" -> "name")
    }

    "detect errors" in {
      val bound = theForm.bind(
        Map(
          "name" -> ""
        )
      )

      bound.hasErrors                 shouldBe true
      bound.errors                      should have size 1
      bound.error("name").get.message shouldBe "lettingHistory.occupierDetail.name.required"
    }
  }
