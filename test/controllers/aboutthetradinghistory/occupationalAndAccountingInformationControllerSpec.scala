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
import views.html.aboutthetradinghistory.aboutYourTradingHistory

class occupationalAndAccountingInformationControllerSpec extends TestBaseSpec {

  val mockAboutYouNavigator: AboutTheTradingHistoryNavigator   = mock[AboutTheTradingHistoryNavigator]
  val mockAboutYourTradingHistoryView: aboutYourTradingHistory = mock[aboutYourTradingHistory]
  when(mockAboutYourTradingHistoryView.apply(any)(any, any)).thenReturn(HtmlFormat.empty)

  val aboutYourTradingHistoryController = new AboutYourTradingHistoryController(
    stubMessagesControllerComponents(),
    mockAboutYouNavigator,
    mockAboutYourTradingHistoryView,
    preFilledSession,
    mockSessionRepo
  )

  "GET /" should {
    "return 200" in {
      val result = aboutYourTradingHistoryController.show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = aboutYourTradingHistoryController.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = aboutYourTradingHistoryController.submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
}
