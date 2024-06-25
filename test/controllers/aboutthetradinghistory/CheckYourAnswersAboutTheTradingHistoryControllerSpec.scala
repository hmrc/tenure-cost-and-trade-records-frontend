/*
 * Copyright 2023 HM Revenue & Customs
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
import play.twirl.api.HtmlFormat
import utils.TestBaseSpec
import views.html.taskList

class CheckYourAnswersAboutTheTradingHistoryControllerSpec extends TestBaseSpec {

  val backLink = controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedController.show().url

  val mockAboutTheTradingHistoryNavigator = mock[AboutTheTradingHistoryNavigator]

  val mockTaskListView = mock[taskList]
  when(mockTaskListView.apply(any)(any, any)).thenReturn(HtmlFormat.empty)

  val checkYourAnswersAboutTradingHistoryController = new CheckYourAnswersAboutTheTradingHistoryController(
    stubMessagesControllerComponents(),
    mockAboutTheTradingHistoryNavigator,
    checkYourAnswersAboutTheTradingHistoryView,
    preFilledSession,
    mockSessionRepo
  )

  val checkYourAnswersAboutTradingHistoryController6015 = new CheckYourAnswersAboutTheTradingHistoryController(
    stubMessagesControllerComponents(),
    mockAboutTheTradingHistoryNavigator,
    checkYourAnswersAboutTheTradingHistoryView,
    preFilledSession6015,
    mockSessionRepo
  )

  val checkYourAnswersAboutTradingHistoryController6020 = new CheckYourAnswersAboutTheTradingHistoryController(
    stubMessagesControllerComponents(),
    mockAboutTheTradingHistoryNavigator,
    checkYourAnswersAboutTheTradingHistoryView,
    preFilledSession6020,
    mockSessionRepo
  )

  val checkYourAnswersAboutTradingHistoryController6076 = new CheckYourAnswersAboutTheTradingHistoryController(
    stubMessagesControllerComponents(),
    mockAboutTheTradingHistoryNavigator,
    checkYourAnswersAboutTheTradingHistoryView,
    preFilledSession6076,
    mockSessionRepo
  )

  "GET /" should {
    "return 200" in {
      val result = checkYourAnswersAboutTradingHistoryController.show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = checkYourAnswersAboutTradingHistoryController.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 6015" in {
      val result = checkYourAnswersAboutTradingHistoryController6015.show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML 6015" in {
      val result = checkYourAnswersAboutTradingHistoryController6015.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 6020" in {
      val result = checkYourAnswersAboutTradingHistoryController6020.show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML 6020" in {
      val result = checkYourAnswersAboutTradingHistoryController6020.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 6076" in {
      val result = checkYourAnswersAboutTradingHistoryController6076.show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML 6076" in {
      val result = checkYourAnswersAboutTradingHistoryController6076.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = checkYourAnswersAboutTradingHistoryController.submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

}
