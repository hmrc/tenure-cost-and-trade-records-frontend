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

import connectors.Audit
import controllers.aboutthetradinghistory
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import utils.TestBaseSpec

class SingleCaravansSubletControllerSpec extends TestBaseSpec {

  private val previousPage = aboutthetradinghistory.routes.SingleCaravansOwnedByOperatorController.show().url
  private val nextPage     = aboutthetradinghistory.routes.SingleCaravansAgeCategoriesController.show().url

  val mockAudit: Audit               = mock[Audit]
  def singleCaravansSubletController =
    new SingleCaravansSubletController(
      caravansTrading6045View,
      aboutYourTradingHistoryNavigator,
      preEnrichedActionRefiner(
        aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory6045),
        aboutTheTradingHistoryPartOne = Some(prefilledTurnoverSections6045)
      ),
      mockSessionRepo,
      stubMessagesControllerComponents(),
      mockAudit
    )

  "GET /" should {
    "return 200" in {
      val result = singleCaravansSubletController.show(fakeRequest)
      status(result) shouldBe OK
    }

    "return HTML" in {
      val result = singleCaravansSubletController.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")

      val content = contentAsString(result)
      content should include(previousPage)
      content should not include "/check-your-answers-about-the-trading-history"

    }

    "render back link to CYA if come from CYA" in {
      val result  = singleCaravansSubletController.show(fakeRequestFromCYA)
      val content = contentAsString(result)
      content should include("/check-your-answers-about-the-trading-history")
      content should not include previousPage
    }
  }

  private def validFormDataPerYear(idx: Int): Seq[(String, String)] =
    Seq(
      s"turnover[$idx].grossReceipts" -> (idx * 1000).toString,
      s"turnover[$idx].vans"          -> "222"
    )

  private def validFormData: Seq[(String, String)] =
    validFormDataPerYear(0) ++
      validFormDataPerYear(1) ++
      validFormDataPerYear(2)

  private def invalidFormData: Seq[(String, String)] =
    validFormDataPerYear(0) ++
      validFormDataPerYear(1)

  "SUBMIT /" should {
    "save the form data and redirect to the next page" in {
      val res = singleCaravansSubletController.submit(
        fakePostRequest.withFormUrlEncodedBody(validFormData*)
      )
      status(res)           shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(nextPage)
    }

    "return 400 and error message for invalid weeks" in {
      val res = singleCaravansSubletController.submit(
        fakePostRequest.withFormUrlEncodedBody(invalidFormData*)
      )
      status(res)        shouldBe BAD_REQUEST
      contentAsString(res) should include(
        """<a href="#turnover[2].grossReceipts">error.turnover.6045.caravans.single.subletByOperator.grossReceipts.required</a>"""
      )
    }

    "return 400 for empty turnoverSections" in {
      val res = singleCaravansSubletController.submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
      status(res) shouldBe BAD_REQUEST
    }
  }

}
