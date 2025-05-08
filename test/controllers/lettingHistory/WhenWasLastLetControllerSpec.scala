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
import models.submissions.lettingHistory.LettingHistory._
import models.submissions.lettingHistory.{IntendedDetail, LettingHistory}
import navigation.LettingHistoryNavigator
import play.api.libs.json.Writes
import play.api.test.Helpers.*
import uk.gov.hmrc.http.HeaderCarrier
import util.DateUtilLocalised
import views.html.lettingHistory.whenWasLastLet as WhenWasLastLetView

import java.time.LocalDate

class WhenWasLastLetControllerSpec extends LettingHistoryControllerSpec:

  "the LastRental controller" when {
    "the user has not entered any date yet"         should {
      "be handling GET / by replying 200 with the form showing the date field" in new ControllerFixture {
        val result = controller.show(fakeGetRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF8
        val page = contentAsJsoup(result)
        page.heading           shouldBe "lettingHistory.intendedLettings.whenWasLastLet.heading"
        page.backLink          shouldBe routes.HasStoppedLettingController.show.url
        page.input("date.day")   should beEmpty
        page.input("date.month") should beEmpty
        page.input("date.year")  should beEmpty
      }
      "be handling POST / by replying 303 redirect to the 'Yearly Available' page" in new ControllerFixture {
        val request = fakePostRequest.withFormUrlEncodedBody(
          "date.day"   -> "1",
          "date.month" -> "4",
          "date.year"  -> "2024"
        )
        val result  = controller.submit(request)
        status(result)                                    shouldBe SEE_OTHER
        redirectLocation(result).value                    shouldBe routes.IsYearlyAvailableController.show.url
        verify(repository, once).saveOrUpdate(data.capture())(using any[Writes[Session]], any[HeaderCarrier])
        intendedLettings(data).value.whenWasLastLet.value shouldBe LocalDate.of(2024, 4, 1)
      }
    }
    "the user has already entered a date"           should {
      "be handling GET / by replying 200 with the pre-filled form showing the date value" in new ControllerFixture(
        whenWasLastLet = Some(LocalDate.of(2024, 12, 25))
      ) {
        val result = controller.show(fakeGetRequest)
        status(result)            shouldBe OK
        contentType(result).value shouldBe HTML
        charset(result).value     shouldBe UTF8
        val page = contentAsJsoup(result)
        page.heading           shouldBe "lettingHistory.intendedLettings.whenWasLastLet.heading"
        page.backLink          shouldBe routes.HasStoppedLettingController.show.url
        page.input("date.day")   should haveValue("25")
        page.input("date.month") should haveValue("12")
        page.input("date.year")  should haveValue("2024")
      }
    }
    "regardless the user had entered a date or not" should {
      "be handling invalid POST /detail by replying 400 with error messages" in new ControllerFixture {
        val result = controller.submit(
          fakePostRequest.withFormUrlEncodedBody(
            "date.day"   -> "",
            "date.month" -> "",
            "date.year"  -> ""
          )
        )
        status(result) shouldBe BAD_REQUEST
        val page   = contentAsJsoup(result)
        page.error("date.day") shouldBe "error.date.required"
      }
    }
  }

  trait ControllerFixture(whenWasLastLet: Option[LocalDate] = None)
      extends MockRepositoryFixture
      with SessionCapturingFixture:
    val controller = new WhenWasLastLetController(
      mcc = stubMessagesControllerComponents(),
      dateUtil = inject[DateUtilLocalised],
      navigator = inject[LettingHistoryNavigator],
      theView = inject[WhenWasLastLetView],
      sessionRefiner = preEnrichedActionRefiner(
        lettingHistory = Some(
          LettingHistory(
            intendedLettings = Some(
              IntendedDetail(
                whenWasLastLet = whenWasLastLet
              )
            )
          )
        )
      ),
      repository
    )
