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

import connectors.Audit
import navigation.AboutTheTradingHistoryNavigator
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import utils.TestBaseSpec
import views.html.aboutthetradinghistory.whenDidYouFirstOccupy as WhenDidYouFirstOccupyView

class occupationalAndAccountingInformationControllerSpec extends TestBaseSpec {

  val mockAudit: Audit = mock[Audit]

  val mockAboutYouNavigator: AboutTheTradingHistoryNavigator = mock[AboutTheTradingHistoryNavigator]
  val mockWhenDidYouFirstOccupyView                          = mock[WhenDidYouFirstOccupyView]
  when(mockWhenDidYouFirstOccupyView.apply(any, any)(any, any)).thenReturn(HtmlFormat.empty)

  val aboutYourTradingHistoryController = new WhenDidYouFirstOccupyController(
    stubMessagesControllerComponents(),
    mockAudit,
    mockAboutYouNavigator,
    mockWhenDidYouFirstOccupyView,
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
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
}
