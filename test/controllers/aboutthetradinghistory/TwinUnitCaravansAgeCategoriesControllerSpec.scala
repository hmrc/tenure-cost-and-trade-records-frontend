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
import models.submissions.aboutthetradinghistory.Caravans.CaravanHireType
import models.submissions.aboutthetradinghistory.Caravans.CaravanHireType.{FleetHire, PrivateSublet}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import utils.TestBaseSpec

class TwinUnitCaravansAgeCategoriesControllerSpec extends TestBaseSpec {

  private val previousPage = aboutthetradinghistory.routes.TwinUnitCaravansSubletController.show().url

  private val nextPage = aboutthetradinghistory.routes.CaravansTotalSiteCapacityController.show().url

  val mockAudit: Audit                        = mock[Audit]
  def twinUnitCaravansAgeCategoriesController =
    new TwinUnitCaravansAgeCategoriesController(
      caravansAgeCategoriesView,
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
      val result = twinUnitCaravansAgeCategoriesController.show(fakeRequest)
      status(result) shouldBe OK
    }

    "return HTML" in {
      val result = twinUnitCaravansAgeCategoriesController.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")

      val content = contentAsString(result)
      content should include(previousPage)
      content should not include "/check-your-answers-about-the-trading-history"

    }

    "render back link to CYA if come from CYA" in {
      val result  = twinUnitCaravansAgeCategoriesController.show(fakeRequestFromCYA)
      val content = contentAsString(result)
      content should include("/check-your-answers-about-the-trading-history")
      content should not include previousPage
    }
  }

  private def validDataPerHireType(hireType: CaravanHireType, valuePrefix: String = ""): Seq[(String, String)] =
    Seq(
      s"$hireType.years0_5"    -> s"${valuePrefix}111",
      s"$hireType.years6_10"   -> s"${valuePrefix}222",
      s"$hireType.years11_15"  -> s"${valuePrefix}333",
      s"$hireType.years15plus" -> s"${valuePrefix}444"
    )

  private def validFormData: Seq[(String, String)] =
    validDataPerHireType(FleetHire, "10") ++
      validDataPerHireType(PrivateSublet, "20")

  private def invalidNumberFormData: Seq[(String, String)] =
    validDataPerHireType(FleetHire) ++
      validDataPerHireType(PrivateSublet, "abc")

  "SUBMIT /" should {
    "save the form data and redirect to the next page" in {
      val res = twinUnitCaravansAgeCategoriesController.submit(
        fakePostRequest.withFormUrlEncodedBody(validFormData*)
      )
      status(res)           shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(nextPage)
    }

    "return 400 and error message for invalid number" in {
      val res = twinUnitCaravansAgeCategoriesController.submit(
        fakePostRequest.withFormUrlEncodedBody(invalidNumberFormData*)
      )
      status(res)        shouldBe BAD_REQUEST
      contentAsString(res) should include(
        """<a href="#privateSublet.years0_5">error.caravans.twin.privateSublet.years0_5.nonNumeric</a>"""
      )
    }

    "return 400 for empty form data" in {
      val res = twinUnitCaravansAgeCategoriesController.submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
      status(res) shouldBe BAD_REQUEST
    }
  }

}
