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

import actions.SessionRequest
import form.lettingHistory.RentalPeriodForm.theForm
import models.submissions.Form6010.MonthsYearDuration
import models.submissions.aboutyouandtheproperty.AboutYouAndThePropertyPartTwo
import models.submissions.lettingHistory.LocalPeriod
import play.api.data.Form
import play.api.mvc.AnyContent
import test.FormSpec
import util.DateUtilLocalised

import java.time.LocalDate
import scala.language.implicitConversions

class RentalPeriodFormSpec extends FormSpec:

  "RentalPeriodForm" should {
    "bind good data as expected" in new SessionFixture(isWelsh = false) {
      val data: Map[String, String] = Map(
        "fromDate.day"   -> "1",
        "fromDate.month" -> "4",
        "fromDate.year"  -> (previousFiscalYearEnd - 1).toString,
        "toDate.day"     -> "31",
        "toDate.month"   -> "3",
        "toDate.year"    -> previousFiscalYearEnd.toString
      )
      val bound: Form[LocalPeriod]  = theForm.bind(data)

      bound.hasErrors shouldBe false
      bound.data      shouldBe data
    }

    "unbind good data as expected" in new SessionFixture(isWelsh = false) {
      val rentalPeriod: LocalPeriod = LocalPeriod(
        fromDate = LocalDate.of(previousFiscalYearEnd - 1, 8, 13),
        toDate = LocalDate.of(previousFiscalYearEnd, 12, 25)
      )
      val filled: Form[LocalPeriod] = theForm.fill(rentalPeriod)

      filled.hasErrors shouldBe false
      filled.data      shouldBe Map(
        "fromDate.day"   -> "13",
        "fromDate.month" -> "8",
        "fromDate.year"  -> (previousFiscalYearEnd - 1).toString,
        "toDate.day"     -> "25",
        "toDate.month"   -> "12",
        "toDate.year"    -> previousFiscalYearEnd.toString
      )
    }

    "detect errors related to fields being required" in new SessionFixture(isWelsh = false) {
      val bound: Form[LocalPeriod] = theForm.bind(
        Map(
          "fromDate.day"   -> "",
          "fromDate.month" -> "",
          "fromDate.year"  -> "",
          "toDate.day"     -> "",
          "toDate.month"   -> "",
          "toDate.year"    -> ""
        )
      )

      bound.hasErrors                         shouldBe true
      bound.errors                              should have size 2
      bound.error("fromDate.day").get.message shouldBe "error.date.required"
      bound.error("toDate.day").get.message   shouldBe "error.date.required"
    }

    "detect errors related to fields being constrained according to the Welsh journey" in new SessionFixture(
      isWelsh = true
    ) {
      val bound: Form[LocalPeriod] = theForm.bind(
        Map(
          "fromDate.day"   -> "1",
          "fromDate.month" -> "4",
          "fromDate.year"  -> (previousFiscalYearEnd - 4).toString,
          "toDate.day"     -> "1",
          "toDate.month"   -> "4",
          "toDate.year"    -> previousFiscalYearEnd.toString
        )
      )

      bound.hasErrors                     shouldBe true
      bound.errors                          should have size 2
      bound.error("fromDate").get.message shouldBe s"The from date must be on or after $startDateWales"
      bound.error("toDate").get.message   shouldBe s"""The "to date" must be less than or equal to $endDate"""
    }

    "detect errors related to fields being constrained according to the English journey" in new SessionFixture(
      isWelsh = false
    ) {
      val bound: Form[LocalPeriod] = theForm.bind(
        Map(
          "fromDate.day"   -> "1",
          "fromDate.month" -> "4",
          "fromDate.year"  -> (previousFiscalYearEnd - 2).toString,
          "toDate.day"     -> "1",
          "toDate.month"   -> "4",
          "toDate.year"    -> previousFiscalYearEnd.toString
        )
      )

      bound.hasErrors                     shouldBe true
      bound.errors                          should have size 2
      bound.error("fromDate").get.message shouldBe s"The from date must be on or after $startDateEnglish"
      bound.error("toDate").get.message   shouldBe s"""The "to date" must be less than or equal to $endDate"""
    }

    "return error when from date is before first available for commercial letting" in new SessionFixture(
      isWelsh = false
    ) {
      given sessionReq: SessionRequest[AnyContent] = sessionRequest(false).copy(
        sessionData = sessionRequest(false).sessionData.copy(
          aboutYouAndThePropertyPartTwo =
            AboutYouAndThePropertyPartTwo(commercialLetDate = MonthsYearDuration(2, previousFiscalYearEnd))
        )
      )

      val commercialLetFirstAvailable: String = summon[DateUtilLocalised].formatDate(LocalDate.of(previousFiscalYearEnd, 2, 1))

      val bound: Form[LocalPeriod] = theForm.bind(
        Map(
          "fromDate.day"   -> "31",
          "fromDate.month" -> "1",
          "fromDate.year"  -> previousFiscalYearEnd.toString,
          "toDate.day"     -> "1",
          "toDate.month"   -> "3",
          "toDate.year"    -> previousFiscalYearEnd.toString
        )
      )

      bound.hasErrors                     shouldBe true
      bound.errors                          should have size 1
      bound.error("fromDate").get.message shouldBe s"The from date must be on or after $commercialLetFirstAvailable"
    }

    "detect errors related to the from date being greater than the to date" in new SessionFixture(isWelsh = false) {
      val bound: Form[LocalPeriod] = theForm.bind(
        Map(
          "fromDate.day"   -> "10",
          "fromDate.month" -> "4",
          "fromDate.year"  -> (previousFiscalYearEnd - 1).toString,
          "toDate.day"     -> "1",
          "toDate.month"   -> "4",
          "toDate.year"    -> (previousFiscalYearEnd - 1).toString
        )
      )

      bound.hasErrors           shouldBe true
      bound.errors                should have size 1
      bound.errors.head.message shouldBe "lettingHistory.rentalPeriod.error"
    }
  }
