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
import form.aboutthetradinghistory.AdditionalAmusementsForm
import play.api.http.Status
import play.api.http.Status._
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentAsString, contentType, redirectLocation, status, stubMessagesControllerComponents}
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec

class AdditionalAmusementsControllerSpec extends TestBaseSpec {
  val mockAudit: Audit = mock[Audit]
  val years            = Seq("2023", "2022", "2021")

  private def validFormDataPerYear(idx: Int): Seq[(String, String)] =
    Seq(
      s"additionalAmusements[$idx].receipts" -> "10000"
    )

  private def validFormData: Seq[(String, String)] =
    validFormDataPerYear(0) ++
      validFormDataPerYear(1) ++
      validFormDataPerYear(2)

  private def invalidFormData: Seq[(String, String)] =
    validFormDataPerYear(0) ++
      validFormDataPerYear(1)

  def controller =
    new AdditionalAmusementsController(
      stubMessagesControllerComponents(),
      mockAudit,
      aboutYourTradingHistoryNavigator,
      additionalAmusementsView,
      preEnrichedActionRefiner(
        aboutTheTradingHistory = Some(prefilledAboutYourTradingHistory6045),
        aboutTheTradingHistoryPartOne = Some(prefilledTurnoverSections6045)
      ),
      mockSessionRepo
    )

  "GET /" should {
    "return 200" in {
      val result = controller.show(fakeRequest)
      status(result) shouldBe Status.OK
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

    "render a correct back link to additional bars and clubs page if no query parameters in the url " in {
      val result  = controller.show(fakeRequest)
      val content = contentAsString(result)
      content should include("/additional-bars-clubs")
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
        controllers.aboutthetradinghistory.routes.AdditionalMiscController.show().url
      )
    }

    "return 400 and error message for invalid weeks" in {
      val res = controller.submit(
        fakePostRequest.withFormUrlEncodedBody(invalidFormData*)
      )
      status(res)        shouldBe BAD_REQUEST
      contentAsString(res) should include(
        """<a href="#additionalAmusements[2].receipts">error.additionalAmusements.receipts.required</a>"""
      )
    }

    "return 400 and error message for invalid receipts" in {

      val formData = Map("additionalAmusements[0].receipts" -> "xxx")

      val form = AdditionalAmusementsForm.additionalAmusementsForm(years)(using messages).bind(formData)
      mustContainError(
        "additionalAmusements[0].receipts",
        messages("error.additionalAmusements.receipts.range", "2023"),
        form
      )
    }

    "return 400 and error message for empty receipts" in {

      val formData = Map("additionalAmusements[1].receipts" -> "")

      val form = AdditionalAmusementsForm.additionalAmusementsForm(years)(using messages).bind(formData)
      mustContainError(
        "additionalAmusements[1].receipts",
        messages("error.additionalAmusements.receipts.required", "2022"),
        form
      )
    }

    "return 400 and error message for negative receipts" in {

      val formData = Map("additionalAmusements[2].receipts" -> "-1")

      val form = AdditionalAmusementsForm.additionalAmusementsForm(years)(using messages).bind(formData)
      mustContainError(
        "additionalAmusements[2].receipts",
        messages("error.additionalAmusements.receipts.negative", "2021"),
        form
      )
    }

  }
}
