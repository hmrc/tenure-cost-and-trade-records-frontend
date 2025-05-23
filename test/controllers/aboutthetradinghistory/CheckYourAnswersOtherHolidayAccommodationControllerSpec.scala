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

package controllers.aboutthetradinghistory

import actions.SessionRequest
import models.ForType.*
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne
import navigation.AboutTheTradingHistoryNavigator
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import play.twirl.api.HtmlFormat
import utils.TestBaseSpec
import views.html.taskList.taskList

class CheckYourAnswersOtherHolidayAccommodationControllerSpec extends TestBaseSpec {

  val backLink                            = controllers.aboutthetradinghistory.routes.OtherHolidayAccommodationController.show().url
  val mockAboutTheTradingHistoryNavigator = mock[AboutTheTradingHistoryNavigator]
  val mockTaskListView                    = mock[taskList]
  val sessionRequest                      = SessionRequest(aboutYourTradingHistory6045CYAOtherHolidayAccommodationSessionYes, fakeRequest)
  when(mockTaskListView()(using any, any)).thenReturn(HtmlFormat.empty)

  def checkYourAnswersOtherHolidayAccommodationController(
    aboutTheTradingHistoryPartOne: AboutTheTradingHistoryPartOne = prefilledAboutTheTradingHistoryPartOneCYA6045
  ) = new CheckYourAnswersOtherHolidayAccommodationController(
    stubMessagesControllerComponents(),
    mockAboutTheTradingHistoryNavigator,
    checkYourAnswersOtherHolidayAccommodationView,
    preEnrichedActionRefiner(
      referenceNumber = "99996045004",
      forType = FOR6045,
      aboutTheTradingHistoryPartOne = Some(aboutTheTradingHistoryPartOne)
    ),
    mockSessionRepo
  )

  "GET /"    should {
    "return 200" in {
      val result = checkYourAnswersOtherHolidayAccommodationController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = checkYourAnswersOtherHolidayAccommodationController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "render with values" in {
      val result =
        checkYourAnswersOtherHolidayAccommodationController(prefilledAboutTheTradingHistoryPartOneCYA6045All).show(
          sessionRequest
        )
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")

    }
  }
  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = checkYourAnswersOtherHolidayAccommodationController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
}
