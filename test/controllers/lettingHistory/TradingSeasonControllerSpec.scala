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

package controllers.lettingHistory

import models.submissions.lettingHistory.LettingHistory.*
import models.submissions.lettingHistory.*
import navigation.LettingHistoryNavigator
import play.api.test.Helpers.*
import uk.gov.hmrc.http.HeaderCarrier
import util.DateUtilLocalised
import views.html.lettingHistory.tradingSeason as TradingSeasonView

import java.time.{LocalDate, Year}

import scala.language.implicitConversions

class TradingSeasonControllerSpec extends LettingHistoryControllerSpec with FiscalYearSupport:

  "the TradingSeasonLength controller" when {
    "the user has not entered any period yet"       should {
      "be handling GET by replying 200 with the form showing date fields" in new ControllerFixture {
        val result = controller.show(fakeGetRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF8
        val page = contentAsJsoup(result)
        page.heading               shouldBe "lettingHistory.intendedLettings.tradingSeason.heading"
        page.backLink              shouldBe routes.IsYearlyAvailableController.show.url
        page.input("fromDate.day")   should beEmpty
        page.input("fromDate.month") should beEmpty
        page.input("toDate.day")     should beEmpty
        page.input("toDate.month")   should beEmpty

      }
      "be handling POST index=0 by replying 303 redirect to 'Occupier List' page" in new ControllerFixture {
        val request = fakePostRequest.withFormUrlEncodedBody(
          "fromDate.day"   -> "1",
          "fromDate.month" -> "4",
          "toDate.day"     -> "31",
          "toDate.month"   -> "3"
        )
        val result  = controller.submit(request)
        status(result)                                            shouldBe SEE_OTHER
        redirectLocation(result).value                            shouldBe routes.HasOnlineAdvertisingController.show.url
        verify(repository, once).saveOrUpdate(data.capture())(using any[HeaderCarrier])
        intendedLettings(data).value.tradingSeason.value.fromDate shouldBe LocalDate.of(
          Year.now.getValue,
          4,
          1
        )
        intendedLettings(data).value.tradingSeason.value.toDate   shouldBe LocalDate.of(
          Year.now.getValue,
          3,
          31
        )
      }
    }
    "the user has already entered a period"         should {
      "be handling GET by replying 200 with the pre-filled form showing date values" in new ControllerFixture(
        period = Some(
          LocalPeriod(
            fromDate = LocalDate.of(Year.now.getValue, 9, 10),
            toDate = LocalDate.of(Year.now.getValue, 2, 9)
          )
        )
      ) {
        val result = controller.show(fakeGetRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF8
        val page = contentAsJsoup(result)
        page.backLink              shouldBe routes.IsYearlyAvailableController.show.url
        page.input("fromDate.day")   should haveValue("10")
        page.input("fromDate.month") should haveValue("9")
        page.input("toDate.day")     should haveValue("9")
        page.input("toDate.month")   should haveValue("2")
      }
    }
    "regardless users having entered period or not" should {
      "be handling invalid POST /detail by replying 400 with error messages" in new ControllerFixture {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "fromDate.day"   -> "",
            "fromDate.month" -> "",
            "toDate.day"     -> "",
            "toDate.month"   -> ""
          )
        )
        status(result) shouldBe BAD_REQUEST
        val page   = contentAsJsoup(result)
        page.error("fromDate.day") shouldBe "error.date.required"
      }
    }
  }

  trait ControllerFixture(period: Option[LocalPeriod] = None)
      extends MockRepositoryFixture
      with SessionCapturingFixture:
    val controller = new TradingSeasonController(
      mcc = stubMessagesControllerComponents(),
      dateUtil = inject[DateUtilLocalised],
      navigator = inject[LettingHistoryNavigator],
      theView = inject[TradingSeasonView],
      sessionRefiner = preEnrichedActionRefiner(
        lettingHistory = Some(
          LettingHistory(
            intendedLettings = Some(
              IntendedDetail(
                tradingSeason = period
              )
            )
          )
        )
      ),
      repository
    )
