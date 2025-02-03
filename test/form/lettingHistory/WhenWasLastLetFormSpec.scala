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

import actions.SessionRequest
import controllers.lettingHistory.FiscalYearSupport
import form.lettingHistory.WhenWasLastLetForm.theForm
import play.api.mvc.AnyContent

import java.time.LocalDate

class WhenWasLastLetFormSpec extends FormSpec:

  it should "bind good data as expected" in new SessionFixture {
    val data  = Map(
      "date.day"   -> "1",
      "date.month" -> "4",
      "date.year"  -> "2024"
    )
    val bound = theForm.bind(data)
    bound.hasErrors mustBe false
    bound.data mustBe data
  }

  it should "unbind good data as expected" in new SessionFixture {
    val date   = LocalDate.of(2024, 8, 13)
    val filled = theForm.fill(date)
    filled.hasErrors mustBe false
    filled.data mustBe Map(
      "date.day"   -> "13",
      "date.month" -> "8",
      "date.year"  -> "2024"
    )
  }

  it should "detect errors related to fields being required" in new SessionFixture {
    val bound = theForm.bind(
      Map(
        "date.day"   -> "",
        "date.month" -> "",
        "date.year"  -> ""
      )
    )
    bound.hasErrors mustBe true
    bound.errors must have size 1
    bound.error("date.day").value.message mustBe "error.date.required"
  }

  trait SessionFixture extends FiscalYearSupport:
    given SessionRequest[AnyContent] = sessionRequest(isWelsh = false)
