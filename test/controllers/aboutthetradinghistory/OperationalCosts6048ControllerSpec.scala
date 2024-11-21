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

import controllers.aboutthetradinghistory
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import utils.TestBaseSpec

class OperationalCosts6048ControllerSpec extends TestBaseSpec {

  private val previousPage = aboutthetradinghistory.routes.AdministrativeCosts6048Controller.show.url

  private val nextPage = aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url

  private val cyaPage = aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url

  def operationalCosts6048Controller =
    new OperationalCosts6048Controller(
      operationalCosts6048View,
      aboutYourTradingHistoryNavigator,
      preEnrichedActionRefiner(
        aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory6048),
        aboutTheTradingHistoryPartOne = Some(prefilledTurnoverSections6048)
      ),
      mockSessionRepo,
      stubMessagesControllerComponents()
    )

  "GET /" should {
    "return 200" in {
      val result = operationalCosts6048Controller.show(fakeRequest)
      status(result) shouldBe OK
    }

    "return HTML" in {
      val result = operationalCosts6048Controller.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")

      val content = contentAsString(result)
      content should include(previousPage)
      content should not include cyaPage

    }

    "render back link to CYA if come from CYA" in {
      val result  = operationalCosts6048Controller.show(fakeRequestFromCYA)
      val content = contentAsString(result)
      content should include(cyaPage)
      content should not include previousPage
    }
  }

  private def validFormDataPerYear(idx: Int): Seq[(String, String)] =
    Seq(
      s"operationalCostsSeq.turnover[$idx].energyBills"                -> (idx * 10).toString,
      s"operationalCostsSeq.turnover[$idx].laundryCleaning"            -> (idx * 100).toString,
      s"operationalCostsSeq.turnover[$idx].repairsRenewalsMaintenance" -> (idx * 1000).toString,
      s"operationalCostsSeq.turnover[$idx].tvLicences"                 -> (idx * 22000).toString,
      s"operationalCostsSeq.turnover[$idx].travellingAndMotorExpenses" -> (idx * 33000).toString,
      s"operationalCostsSeq.turnover[$idx].other"                      -> "0"
    )

  private def validFormData: Seq[(String, String)] =
    validFormDataPerYear(0) ++
      validFormDataPerYear(1) ++
      validFormDataPerYear(2)

  private def invalidFormData: Seq[(String, String)] =
    validFormDataPerYear(0).map(t => (t._1, "-8")) ++
      validFormDataPerYear(1) ++
      validFormDataPerYear(2)

  "SUBMIT /" should {
    "save the form data and redirect to the next page" in {
      val res = operationalCosts6048Controller.submit(
        fakePostRequest.withFormUrlEncodedBody(validFormData*)
      )
      status(res)           shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(nextPage)
    }

    "return 400 and error message for invalid form data - negative value" in {
      val res = operationalCosts6048Controller.submit(
        fakePostRequest.withFormUrlEncodedBody(invalidFormData*)
      )
      status(res)        shouldBe BAD_REQUEST
      contentAsString(res) should include(
        """<a href="#operationalCostsSeq.turnover[0].energyBills">error.turnover.6048.operationalCosts.energyBills.negative</a>"""
      )
    }

    "return 400 and error message for invalid form data - missed value" in {
      val res = operationalCosts6048Controller.submit(
        fakePostRequest.withFormUrlEncodedBody(validFormDataPerYear(2)*)
      )
      status(res)        shouldBe BAD_REQUEST
      contentAsString(res) should include(
        """<a href="#operationalCostsSeq.turnover[0].energyBills">error.turnover.6048.operationalCosts.energyBills.required</a>"""
      )
    }

    "return 400 for empty turnoverSections" in {
      val res = operationalCosts6048Controller.submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
      status(res) shouldBe BAD_REQUEST
    }
  }

}
