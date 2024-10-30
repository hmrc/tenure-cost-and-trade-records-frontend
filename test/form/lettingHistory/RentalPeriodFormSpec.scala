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
import form.lettingHistory.RentalPeriodForm.theForm
import models.submissions.lettingHistory.LocalPeriod
import play.api.mvc.AnyContent

import java.time.LocalDate

class RentalPeriodFormSpec extends FormSpec:

  it should "bind good data as expected" in new SessionFixture(isWelsh = false) {
    val data  = Map(
      "fromDate.day"   -> "1",
      "fromDate.month" -> "4",
      "fromDate.year"  -> (previousFiscalYearEnd - 1).toString,
      "toDate.day"     -> "31",
      "toDate.month"   -> "3",
      "toDate.year"    -> previousFiscalYearEnd.toString
    )
    val bound = theForm.bind(data)
    bound.hasErrors mustBe false
    bound.data mustBe data
  }

  it should "unbind good data as expected" in new SessionFixture(isWelsh = false) {
    val rentalPeriod = LocalPeriod(
      fromDate = LocalDate.of(previousFiscalYearEnd - 1, 8, 13),
      toDate = LocalDate.of(previousFiscalYearEnd, 12, 25)
    )
    val filled       = theForm.fill(rentalPeriod)
    filled.hasErrors mustBe false
    filled.data mustBe Map(
      "fromDate.day"   -> "13",
      "fromDate.month" -> "8",
      "fromDate.year"  -> (previousFiscalYearEnd - 1).toString,
      "toDate.day"     -> "25",
      "toDate.month"   -> "12",
      "toDate.year"    -> previousFiscalYearEnd.toString
    )
  }

  it should "detect errors related to fields being required" in new SessionFixture(isWelsh = false) {
    // When the form gets submitted before being filled
    val bound = theForm.bind(
      Map(
        "fromDate.day"   -> "",
        "fromDate.month" -> "",
        "fromDate.year"  -> "",
        "toDate.day"     -> "",
        "toDate.month"   -> "",
        "toDate.year"    -> ""
      )
    )
    bound.hasErrors mustBe true
    bound.errors must have size 2
    bound.error("fromDate.day").value.message mustBe "error.date.required"
    bound.error("toDate.day").value.message mustBe "error.date.required"
  }

  it should "detect errors related to fields being constrained according to the Welsh journey" in new SessionFixture(
    isWelsh = true
  ) {
    val bound = theForm.bind(
      Map(
        "fromDate.day"   -> "1",
        "fromDate.month" -> "4",
        "fromDate.year"  -> (previousFiscalYearEnd - 4).toString,
        "toDate.day"     -> "1",
        "toDate.month"   -> "4",
        "toDate.year"    -> previousFiscalYearEnd.toString
      )
    )
    bound.hasErrors mustBe true
    bound.errors must have size 2
    bound.error("fromDate").value.message must include("""The "from date" must be greater than or equal to""")
    bound.error("toDate").value.message   must include("""The "to date" must be less than or equal to""")
  }

  it should "detect errors related to fields being constrained according to the English journey" in new SessionFixture(
    isWelsh = false
  ) {
    val bound = theForm.bind(
      Map(
        "fromDate.day"   -> "1",
        "fromDate.month" -> "4",
        "fromDate.year"  -> (previousFiscalYearEnd - 2).toString,
        "toDate.day"     -> "1",
        "toDate.month"   -> "4",
        "toDate.year"    -> previousFiscalYearEnd.toString
      )
    )
    bound.hasErrors mustBe true
    bound.errors must have size 2
    bound.error("fromDate").value.message must include("""The "from date" must be greater than or equal to""")
    bound.error("toDate").value.message   must include("""The "to date" must be less than or equal to""")
  }

  it should "detect errors related to the from date being greater than the to date" in new SessionFixture(
    isWelsh = false
  ) {
    val bound = theForm.bind(
      Map(
        "fromDate.day"   -> "10",
        "fromDate.month" -> "4",
        "fromDate.year"  -> (previousFiscalYearEnd - 1).toString,
        "toDate.day"     -> "1",
        "toDate.month"   -> "4",
        "toDate.year"    -> (previousFiscalYearEnd - 1).toString
      )
    )
    bound.hasErrors mustBe true
    bound.errors must have size 1
    bound.errors.head.message must include("""The "from date" must be less than or equal to the "to date"""")
  }

  trait SessionFixture(isWelsh: Boolean) extends FiscalYearSupport:
    given SessionRequest[AnyContent] = sessionRequest(isWelsh)
