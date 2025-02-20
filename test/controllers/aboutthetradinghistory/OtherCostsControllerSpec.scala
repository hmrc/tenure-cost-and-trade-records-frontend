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

import actions.SessionRequest
import connectors.Audit
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class OtherCostsControllerSpec extends TestBaseSpec {

  val mockAudit: Audit = mock[Audit]

  val sessionRequest = SessionRequest(aboutYourTradingHistory6015YesSession, fakeRequest)
  val postRequest    = sessionRequest.copy(request = FakeRequest("POST", "/").withFormUrlEncodedBody(Seq.empty*))

  val otherCostsController = new OtherCostsController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYourTradingHistoryNavigator,
    otherCostsView,
    preEnrichedActionRefiner(
      aboutTheTradingHistory = aboutYourTradingHistory6015YesSession.aboutTheTradingHistory
    ),
    mockSessionRepo
  )

  "GET /" should {
    "return 200" in {
      val result = otherCostsController.show(sessionRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = otherCostsController.show(sessionRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return correct backLink when 'from=TL' query param is present" in {
      val result = otherCostsController.show()(FakeRequest(GET, "/path?from=TL"))
      contentAsString(result) should include(controllers.routes.TaskListController.show().url)
    }

    "return correct backLink when 'from=IES' query param is present" in {
      val result = otherCostsController.show()(FakeRequest(GET, "/path?from=IES"))
      val html   = contentAsString(result)

      html should include(
        controllers.aboutthetradinghistory.routes.IncomeExpenditureSummaryController.show().url
      )
    }
  }

  "SUBMIT /" should {
    "redirect to the next page if no other costs filled" in {
      val result = otherCostsController.submit(postRequest)
      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(
        controllers.aboutthetradinghistory.routes.IncomeExpenditureSummaryController.show().url
      )
    }
  }

}
