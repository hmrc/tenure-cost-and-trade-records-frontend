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

import models.Session
import models.submissions.lettingHistory.LettingHistory.*
import models.submissions.lettingHistory.*
import navigation.LettingHistoryNavigator
import play.api.libs.json.Writes
import play.api.test.Helpers.*
import uk.gov.hmrc.http.HeaderCarrier
import util.DateUtilLocalised
import views.html.lettingHistory.rentalPeriod as RentalPeriodView

import java.time.LocalDate

class RentalPeriodControllerSpec extends LettingHistoryControllerSpec with FiscalYearSupport:

  "the RentalPeriod controller" when {
    "the user has not entered any period yet"       should {
      "be handling GET index=0 by replying 200 with the form showing date fields" in new ControllerFixture {
        val result = controller.show(maybeIndex = Some(0))(fakeGetRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF8
        val page = contentAsJsoup(result)
        page.heading               shouldBe "lettingHistory.rentalPeriod.heading"
        page.backLink              shouldBe routes.OccupierDetailController.show(Some(0)).url
        page.input("fromDate.day")   should beEmpty
        page.input("fromDate.month") should beEmpty
        page.input("fromDate.year")  should beEmpty
        page.input("toDate.day")     should beEmpty
        page.input("toDate.month")   should beEmpty
        page.input("toDate.year")    should beEmpty
      }
      "be handling POST index=0 by replying 303 redirect to 'Occupier List' page" in new ControllerFixture {
        val request = fakePostRequest.withFormUrlEncodedBody(
          "fromDate.day"   -> "1",
          "fromDate.month" -> "4",
          "fromDate.year"  -> (previousFiscalYearEnd - 1).toString,
          "toDate.day"     -> "31",
          "toDate.month"   -> "3",
          "toDate.year"    -> previousFiscalYearEnd.toString
        )
        val result  = controller.submit(maybeIndex = Some(0))(request)
        status(result)                                           shouldBe SEE_OTHER
        redirectLocation(result).value                           shouldBe routes.OccupierListController.show.url
        verify(repository, once).saveOrUpdate(data.capture())(using any[Writes[Session]], any[HeaderCarrier])
        completedLettings(data)                                    should have size 1
        completedLettings(data).head.rentalPeriod.value.fromDate shouldBe LocalDate.of(previousFiscalYearEnd - 1, 4, 1)
        completedLettings(data).head.rentalPeriod.value.toDate   shouldBe LocalDate.of(previousFiscalYearEnd, 3, 31)
      }
    }
    "the user has already entered a period"         should {
      "be handling GET index=0 by replying 200 with the pre-filled form showing date values" in new ControllerFixture(
        period = Some(
          LocalPeriod(
            fromDate = LocalDate.of(previousFiscalYearEnd, 9, 10),
            toDate = LocalDate.of(previousFiscalYearEnd - 1, 2, 9)
          )
        )
      ) {
        val result = controller.show(maybeIndex = Some(0))(fakeGetRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF8
        val page = contentAsJsoup(result)
        page.backLink              shouldBe routes.OccupierDetailController.show(index = Some(0)).url
        page.input("fromDate.day")   should haveValue("10")
        page.input("fromDate.month") should haveValue("9")
        page.input("fromDate.year")  should haveValue(previousFiscalYearEnd.toString)
        page.input("toDate.day")     should haveValue("9")
        page.input("toDate.month")   should haveValue("2")
        page.input("toDate.year")    should haveValue(s"${previousFiscalYearEnd - 1}")
      }
    }
    "regardless users having entered period or not" should {
      "be handling GET / missing index by replying 303 redirect to 'Occupiers List' page" in new ControllerFixture {
        val result = controller.show(maybeIndex = None)(fakeGetRequest)
        status(result)                 shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.OccupierListController.show.url
      }
      "be handling GET / unknown index by replying 303 redirect to 'Occupiers List' page" in new ControllerFixture {
        val result = controller.show(maybeIndex = Some(99))(fakeGetRequest)
        status(result)                 shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.OccupierListController.show.url
      }
      "be handling invalid POST /detail by replying 400 with error messages" in new ControllerFixture {
        val result = controller.submit(maybeIndex = Some(0))(
          fakePostRequest.withFormUrlEncodedBody(
            "fromDate.day"   -> "",
            "fromDate.month" -> "",
            "fromDate.year"  -> "",
            "toDate.day"     -> "",
            "toDate.month"   -> "",
            "toDate.year"    -> ""
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
    val controller = new RentalPeriodController(
      mcc = stubMessagesControllerComponents(),
      dateUtil = inject[DateUtilLocalised],
      navigator = inject[LettingHistoryNavigator],
      theView = inject[RentalPeriodView],
      sessionRefiner = preEnrichedActionRefiner(
        lettingHistory = Some(
          LettingHistory(
            hasCompletedLettings = Some(true),
            completedLettings = List(
              OccupierDetail(
                name = "Somebody",
                address = Address(
                  line1 = "22, Somewhere Street",
                  line2 = None,
                  town = "Neverland",
                  county = None,
                  postcode = "BN124AX"
                ),
                rentalPeriod = period
              )
            )
          )
        )
      ),
      repository
    )
