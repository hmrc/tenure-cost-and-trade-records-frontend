/*
 * Copyright 2024 HM Revenue & Customs
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

import navigation.AboutTheTradingHistoryNavigator
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.test.Helpers.{contentType, status, stubMessagesControllerComponents}
import play.twirl.api.HtmlFormat
import utils.TestBaseSpec
import views.html.taskList

class CheckYourAnswersOtherHolidayAccommodationControllerSpec extends TestBaseSpec {

  val backLink                            = controllers.aboutthetradinghistory.routes.OtherHolidayAccommodationController.show().url
  val mockAboutTheTradingHistoryNavigator = mock[AboutTheTradingHistoryNavigator]
  val mockTaskListView                    = mock[taskList]
  when(mockTaskListView.apply(any)(any, any)).thenReturn(HtmlFormat.empty)

  val checkYourAnswersOtherHolidayAccommodationController = new CheckYourAnswersOtherHolidayAccommodationController(
    stubMessagesControllerComponents(),
    mockAboutTheTradingHistoryNavigator,
    checkYourAnswersOtherHolidayAccommodationView,
    preEnrichedActionRefiner(
      referenceNumber = "99996045004",
      forType = "FOR6045",
      aboutTheTradingHistoryPartOne = Some(prefilledAboutTheTradingHistoryPartOneCYA6045)
    ),
    mockSessionRepo
  )

  "GET /"    should {
    "return 200" in {
      val result = checkYourAnswersOtherHolidayAccommodationController.show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = checkYourAnswersOtherHolidayAccommodationController.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }
  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = checkYourAnswersOtherHolidayAccommodationController.submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
}
