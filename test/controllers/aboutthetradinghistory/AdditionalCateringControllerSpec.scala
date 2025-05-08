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
import form.aboutthetradinghistory.AdditionalCateringForm
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentAsString, contentType, redirectLocation, status, stubMessagesControllerComponents}
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec

class AdditionalCateringControllerSpec extends TestBaseSpec {

  val mockAudit: Audit = mock[Audit]

  val years                                                         = Seq("2023", "2022", "2021")
  private def validFormDataPerYear(idx: Int): Seq[(String, String)] =
    Seq(
      s"additionalCatering[$idx].grossReceipts"  -> "10000",
      s"additionalCatering[$idx].costOfPurchase" -> "10000"
    )

  private def validFormData: Seq[(String, String)] =
    validFormDataPerYear(0) ++
      validFormDataPerYear(1) ++
      validFormDataPerYear(2)

  private def invalidFormData: Seq[(String, String)] =
    validFormDataPerYear(0) ++
      validFormDataPerYear(1)

  def controller =
    new AdditionalCateringController(
      stubMessagesControllerComponents(),
      mockAudit,
      aboutYourTradingHistoryNavigator,
      additionalCateringView,
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

    "render a correct back link to additional shops page if no query parameters in the url " in {
      val result  = controller.show(fakeRequest)
      val content = contentAsString(result)
      content should include("/additional-shops")
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
        controllers.aboutthetradinghistory.routes.AdditionalBarsClubsController.show().url
      )
    }

    "return 400 and error message for invalid weeks" in {
      val res = controller.submit(
        fakePostRequest.withFormUrlEncodedBody(invalidFormData*)
      )
      status(res)        shouldBe BAD_REQUEST
      contentAsString(res) should include(
        """<a href="#additionalCatering[2].grossReceipts">error.additionalCatering.grossReceipts.required</a>"""
      )
    }

    "return 400 and error message for invalid gross receipts" in {

      val formData = Map("additionalCatering[0].grossReceipts" -> "xxx")

      val form = AdditionalCateringForm.additionalCateringForm(years)(using messages).bind(formData)
      mustContainError(
        "additionalCatering[0].grossReceipts",
        messages("error.additionalCatering.grossReceipts.range", 2023.toString),
        form
      )
    }

    "return 400 and error message for empty gross receipts" in {

      val formData = Map("additionalCatering[1].grossReceipts" -> "")

      val form = AdditionalCateringForm.additionalCateringForm(years)(using messages).bind(formData)
      mustContainError(
        "additionalCatering[1].grossReceipts",
        messages("error.additionalCatering.grossReceipts.required", 2022.toString),
        form
      )
    }

    "return 400 and error message for negative gross receipts" in {

      val formData = Map("additionalCatering[2].grossReceipts" -> "-1")

      val form = AdditionalCateringForm.additionalCateringForm(years)(using messages).bind(formData)
      mustContainError(
        "additionalCatering[2].grossReceipts",
        messages("error.additionalCatering.grossReceipts.negative", 2021.toString),
        form
      )
    }

    "return 400 and error message for invalid cost of purchase" in {

      val formData = Map("additionalCatering[0].costOfPurchase" -> "xxx")

      val form = AdditionalCateringForm.additionalCateringForm(years)(using messages).bind(formData)
      mustContainError(
        "additionalCatering[0].costOfPurchase",
        messages("error.additionalCatering.costOfPurchase.range", 2023.toString),
        form
      )
    }

    "return 400 and error message for empty cost of purchase" in {

      val formData = Map("additionalCatering[1].costOfPurchase" -> "")

      val form = AdditionalCateringForm.additionalCateringForm(years)(using messages).bind(formData)
      mustContainError(
        "additionalCatering[1].costOfPurchase",
        messages("error.additionalCatering.costOfPurchase.required", 2022.toString),
        form
      )
    }

    "return 400 and error message for negative cost of purchase" in {

      val formData = Map("additionalCatering[2].costOfPurchase" -> "-1")

      val form = AdditionalCateringForm.additionalCateringForm(years)(using messages).bind(formData)
      mustContainError(
        "additionalCatering[2].costOfPurchase",
        messages("error.additionalCatering.costOfPurchase.negative", 2021.toString),
        form
      )
    }
  }
}
