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
import form.aboutthetradinghistory.AdditionalBarsClubsForm
import play.api.http.Status._
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentAsString, contentType, redirectLocation, status, stubMessagesControllerComponents}
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec

class AdditionalBarsClubsControllerSpec extends TestBaseSpec {
  val mockAudit: Audit = mock[Audit]

  val years                                                         = Seq("2023", "2022", "2021")
  private def validFormDataPerYear(idx: Int): Seq[(String, String)] =
    Seq(
      s"additionalBarsClubs[$idx].grossBar"            -> "10000",
      s"additionalBarsClubs[$idx].costBar"             -> "10000",
      s"additionalBarsClubs[$idx].grossClubMembership" -> "10000",
      s"additionalBarsClubs[$idx].grossFromSeparate"   -> "10000",
      s"additionalBarsClubs[$idx].costOfEntertainment" -> "10000"
    )

  private def validFormData: Seq[(String, String)] =
    validFormDataPerYear(0) ++
      validFormDataPerYear(1) ++
      validFormDataPerYear(2)

  private def invalidFormData: Seq[(String, String)] =
    validFormDataPerYear(0) ++
      validFormDataPerYear(1)

  def controller =
    new AdditionalBarsClubsController(
      stubMessagesControllerComponents(),
      mockAudit,
      aboutYourTradingHistoryNavigator,
      additionalBarsClubsView,
      preEnrichedActionRefiner(
        aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory6045),
        aboutTheTradingHistoryPartOne = Some(prefilledTurnoverSections6045)
      ),
      mockSessionRepo
    )

  "GET /" should {
    "return 200" in {
      val result = controller.show(fakeRequest)
      status(result) shouldBe OK
    }

    "return HTML" in {
      val result = controller.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "render back link to CYA if come from CYA" in {
      val result  = controller.show(fakeRequestFromCYA)
      val content = contentAsString(result)
      content should include("/check-your-answers-additional-activities")
    }

    "render a correct back link to additional catering page if no query parameters in the url " in {
      val result  = controller.show(fakeRequest)
      val content = contentAsString(result)
      content should include("/additional-catering")
    }

  }

  "SUBMIT /" should {
    "return 400 for form with errors" in {
      val res = controller.submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
      status(res) shouldBe BAD_REQUEST
    }

    "save the form data and redirect to the next page" in {
      val res = controller.submit(
        fakePostRequest.withFormUrlEncodedBody(validFormData*)
      )
      status(res)           shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(
        controllers.aboutthetradinghistory.routes.AdditionalAmusementsController.show().url
      )
    }

    "return 400 and error message for invalid form data" in {
      val res = controller.submit(
        fakePostRequest.withFormUrlEncodedBody(invalidFormData*)
      )
      status(res)        shouldBe BAD_REQUEST
      contentAsString(res) should include(
        """<a href="#additionalBarsClubs[2].grossBar">error.additionalBarsClubs.grossBar.required</a>"""
      )
    }

    "return 400 and error message for invalid gross receipts" in {

      val formData = Map("additionalBarsClubs[0].grossBar" -> "xxx")

      val form = AdditionalBarsClubsForm.additionalBarsClubsForm(years)(using messages).bind(formData)
      mustContainError(
        "additionalBarsClubs[0].grossBar",
        messages("error.additionalBarsClubs.grossBar.range", 2023.toString),
        form
      )
    }

    "return 400 and error message for empty gross receipts" in {

      val formData = Map("additionalBarsClubs[1].grossBar" -> "")

      val form = AdditionalBarsClubsForm.additionalBarsClubsForm(years)(using messages).bind(formData)
      mustContainError(
        "additionalBarsClubs[1].grossBar",
        messages("error.additionalBarsClubs.grossBar.required", 2022.toString),
        form
      )
    }

    "return 400 and error message for negative gross receipts" in {

      val formData = Map("additionalBarsClubs[2].grossBar" -> "-1")

      val form = AdditionalBarsClubsForm.additionalBarsClubsForm(years)(using messages).bind(formData)
      mustContainError(
        "additionalBarsClubs[2].grossBar",
        messages("error.additionalBarsClubs.grossBar.negative", 2021.toString),
        form
      )
    }

    "return 400 and error message for invalid cost of purchase" in {

      val formData = Map("additionalBarsClubs[0].costBar" -> "xxx")

      val form = AdditionalBarsClubsForm.additionalBarsClubsForm(years)(using messages).bind(formData)
      mustContainError(
        "additionalBarsClubs[0].costBar",
        messages("error.additionalBarsClubs.costBar.range", 2023.toString),
        form
      )
    }

    "return 400 and error message for empty cost of purchase" in {

      val formData = Map("additionalBarsClubs[1].costBar" -> "")

      val form = AdditionalBarsClubsForm.additionalBarsClubsForm(years)(using messages).bind(formData)
      mustContainError(
        "additionalBarsClubs[1].costBar",
        messages("error.additionalBarsClubs.costBar.required", 2022.toString),
        form
      )
    }

    "return 400 and error message for negative cost of purchase" in {

      val formData = Map("additionalBarsClubs[2].costBar" -> "-1")

      val form = AdditionalBarsClubsForm.additionalBarsClubsForm(years)(using messages).bind(formData)
      mustContainError(
        "additionalBarsClubs[2].costBar",
        messages("error.additionalBarsClubs.costBar.negative", 2021.toString),
        form
      )
    }
  }
}
