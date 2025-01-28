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
import controllers.aboutthetradinghistory
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import utils.TestBaseSpec

class CaravansTotalSiteCapacityControllerSpec extends TestBaseSpec {

  val mockAudit: Audit = mock[Audit]

  private val previousPage = aboutthetradinghistory.routes.TwinUnitCaravansAgeCategoriesController.show().url

  private val nextPage = aboutthetradinghistory.routes.CaravansPerServiceController.show().url

  def caravansTotalSiteCapacityController =
    new CaravansTotalSiteCapacityController(
      caravansTotalSiteCapacityView,
      mockAudit,
      aboutYourTradingHistoryNavigator,
      preEnrichedActionRefiner(
        aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory6045),
        aboutTheTradingHistoryPartOne = Some(prefilledTurnoverSections6045)
      ),
      mockSessionRepo,
      stubMessagesControllerComponents()
    )

  "GET /" should {
    "return 200" in {
      val result = caravansTotalSiteCapacityController.show(fakeRequest)
      status(result) shouldBe OK
    }

    "return HTML" in {
      val result = caravansTotalSiteCapacityController.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")

      val content = contentAsString(result)
      content should include(previousPage)
      content should not include "/check-your-answers-about-the-trading-history"

    }

    "render back link to CYA if come from CYA" in {
      val result  = caravansTotalSiteCapacityController.show(fakeRequestFromCYA)
      val content = contentAsString(result)
      content should include("/check-your-answers-about-the-trading-history")
      content should not include previousPage
    }
  }

  private def validFormData: Seq[(String, String)] =
    Seq(
      "ownedByOperatorForFleetHire"     -> "111",
      "privatelyOwnedForOwnerAndFamily" -> "222",
      "subletByOperator"                -> "333",
      "subletByPrivateOwners"           -> "444",
      "charitablePurposes"              -> "555",
      "seasonalStaff"                   -> "666"
    )

  private def invalidNumberFormData: Seq[(String, String)] =
    Seq(
      "ownedByOperatorForFleetHire"     -> "abcdef",
      "privatelyOwnedForOwnerAndFamily" -> "222",
      "subletByOperator"                -> "333",
      "subletByPrivateOwners"           -> "444",
      "charitablePurposes"              -> "555",
      "seasonalStaff"                   -> "666"
    )

  "SUBMIT /" should {
    "save the form data and redirect to the next page" in {
      val res = caravansTotalSiteCapacityController.submit(
        fakePostRequest.withFormUrlEncodedBody(validFormData*)
      )
      status(res)           shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(nextPage)
    }

    "return 400 and error message for invalid number" in {
      val res = caravansTotalSiteCapacityController.submit(
        fakePostRequest.withFormUrlEncodedBody(invalidNumberFormData*)
      )
      status(res)        shouldBe BAD_REQUEST
      contentAsString(res) should include(
        """<a href="#ownedByOperatorForFleetHire">error.caravans.ownedByOperatorForFleetHire.nonNumeric</a>"""
      )
    }

    "return 400 for empty form data" in {
      val res = caravansTotalSiteCapacityController.submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
      status(res) shouldBe BAD_REQUEST
    }
  }

}
