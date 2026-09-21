/*
 * Copyright 2026 HM Revenue & Customs
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
import controllers.aboutthetradinghistory
import models.submissions.common.AnswersYesNo
import models.submissions.common.AnswersYesNo.*
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import test.ControllerSpec

class StaticCaravansControllerSpec extends ControllerSpec:

  val mockAudit: Audit = mock[Audit]

  private val previousPage  = aboutthetradinghistory.routes.CheckYourAnswersAccountingInfoController.show.url
  private val nextPageOnYes = aboutthetradinghistory.routes.GrossReceiptsCaravanFleetHireController.show().url
  private val nextPageOnNo  = aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url

  def staticCaravansController: StaticCaravansController =
    StaticCaravansController(
      staticCaravansView,
      mockAudit,
      aboutYourTradingHistoryNavigator,
      preEnrichedActionRefiner(
        aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory6045),
        aboutTheTradingHistoryPartOne = Some(prefilledTurnoverSections6045)
      ),
      mockSessionRepository,
      stubMessagesControllerComponents()
    )

  "GET /" should {
    "return 200" in {
      val result = staticCaravansController.show(getRequest)
      status(result) shouldBe OK
    }

    "return HTML" in {
      val result = staticCaravansController.show(getRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")

      val content = contentAsString(result)
      content should include(previousPage)
      content should not include "/check-your-answers-about-the-trading-history"
    }

    "render back link to CYA if come from CYA" in {
      val result  = staticCaravansController.show(getRequestFromCYA)
      val content = contentAsString(result)
      content should include("/check-your-answers-about-the-trading-history")
      content should not include previousPage
    }
  }

  private def validFormData(answer: AnswersYesNo): Seq[(String, String)] =
    Seq("staticCaravans" -> answer.toString)

  "SUBMIT /" should {
    "save the form data and redirect to the next page on answer Yes" in {
      val res = staticCaravansController.submit(
        postRequest.withFormUrlEncodedBody(validFormData(AnswerYes)*)
      )
      status(res)           shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(nextPageOnYes)
    }

    "save the form data and redirect to the next page on answer No" in {
      val res = staticCaravansController.submit(
        postRequest.withFormUrlEncodedBody(validFormData(AnswerNo)*)
      )
      status(res)           shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(nextPageOnNo)
    }

    "return 400 for empty form data" in {
      val res = staticCaravansController.submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
      status(res) shouldBe BAD_REQUEST
    }
  }
