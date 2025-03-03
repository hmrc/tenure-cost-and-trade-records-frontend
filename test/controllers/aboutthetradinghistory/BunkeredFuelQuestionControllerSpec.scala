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
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.test.{FakeRequest, Helpers}
import play.api.test.Helpers.{GET, POST, contentAsString, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

class BunkeredFuelQuestionControllerSpec extends TestBaseSpec {

  val mockAudit: Audit = mock[Audit]

  val bunkeredFuelQuestionController = new BunkeredFuelQuestionController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYourTradingHistoryNavigator,
    bunkeredFuelQuestionView,
    preEnrichedActionRefiner(aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory)),
    mockSessionRepo
  )

  "GET /" should {
    "return 200" in {
      val result = bunkeredFuelQuestionController.show(fakeRequest)
      status(result) shouldBe OK
    }

    "return HTML" in {
      val result = bunkeredFuelQuestionController.show(fakeRequest)
      contentType(result)     shouldBe Some("text/html")
      Helpers.charset(result) shouldBe Some("utf-8")
    }

    "render back link to CYA if come from CYA" in {
      val result  = bunkeredFuelQuestionController.show(fakeRequestFromCYA)
      val content = contentAsString(result)
      content should include("/check-your-answers-about-the-trading-history")
      content should not include "/financial-year-end"
    }

    "return correct backLink when 'from=TL' query param is present" in {
      val result = bunkeredFuelQuestionController.show()(FakeRequest(GET, "/path?from=TL"))
      contentAsString(result) should include(controllers.routes.TaskListController.show().url)
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = bunkeredFuelQuestionController.submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted" in {
      val res = bunkeredFuelQuestionController.submit()(
        FakeRequest(POST, "/").withFormUrlEncodedBody(
          "bunkeredFuelQuestion" -> "yes"
        )
      )
      status(res) shouldBe SEE_OTHER
    }
  }

}
