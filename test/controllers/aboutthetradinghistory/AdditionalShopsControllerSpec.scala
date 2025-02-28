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
import form.aboutthetradinghistory.AdditionalShopsForm
import play.api.http.Status
import play.api.http.Status.{BAD_REQUEST, SEE_OTHER}
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentAsString, contentType, redirectLocation, status, stubMessagesControllerComponents}
import utils.TestBaseSpec
import utils.FormBindingTestAssertions._

class AdditionalShopsControllerSpec extends TestBaseSpec {
  val mockAudit: Audit = mock[Audit]

  val years                                                                     = Seq("2023", "2022", "2021")
  private def validFormDataPerYear(idx: Int, weeks: Int): Seq[(String, String)] =
    Seq(
      s"additionalShops[$idx].weeks"          -> weeks.toString,
      s"additionalShops[$idx].grossReceipts"  -> "10000",
      s"additionalShops[$idx].costOfPurchase" -> "10000"
    )

  private def formData(weeks: Int): Seq[(String, String)] =
    validFormDataPerYear(0, weeks) ++
      validFormDataPerYear(1, weeks) ++
      validFormDataPerYear(2, weeks)

  def controller =
    new AdditionalShopsController(
      stubMessagesControllerComponents(),
      mockAudit,
      aboutYourTradingHistoryNavigator,
      additionalShopsView,
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

    "render a correct back link to additional activities on site page" in {
      val result  = controller.show(fakeRequest)
      val content = contentAsString(result)
      content should include("/additional-activities-on-site")
    }

  }

  "SUBMIT /" should {
    "return 400 for form with errors" in {
      val res = controller.submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
      status(res) shouldBe BAD_REQUEST
    }

    "save the form data and redirect to the next page" in {
      val res = controller.submit(
        fakePostRequest.withFormUrlEncodedBody(formData(52)*)
      )
      status(res)           shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(
        controllers.aboutthetradinghistory.routes.AdditionalCateringController.show().url
      )
    }

    "return 400 and error message for invalid weeks" in {
      val res = controller.submit(
        fakePostRequest.withFormUrlEncodedBody(formData(53)*)
      )
      status(res)        shouldBe BAD_REQUEST
      contentAsString(res) should include("""<a href="#additionalShops[0].weeks">error.weeksMapping.invalid</a>""")
    }

    "return 400 and error message for invalid gross receipts" in {

      val formData = Map("additionalShops[0].grossReceipts" -> "xxx")

      val form = AdditionalShopsForm.additionalShopsForm(years)(messages).bind(formData)
      mustContainError(
        "additionalShops[0].grossReceipts",
        messages("error.additionalShops.grossReceipts.range", 2023.toString),
        form
      )
    }

    "return 400 and error message for empty gross receipts" in {

      val formData = Map("additionalShops[1].grossReceipts" -> "")

      val form = AdditionalShopsForm.additionalShopsForm(years)(messages).bind(formData)
      mustContainError(
        "additionalShops[1].grossReceipts",
        messages("error.additionalShops.grossReceipts.required", 2022.toString),
        form
      )
    }

    "return 400 and error message for negative gross receipts" in {

      val formData = Map("additionalShops[2].grossReceipts" -> "-1")

      val form = AdditionalShopsForm.additionalShopsForm(years)(messages).bind(formData)
      mustContainError(
        "additionalShops[2].grossReceipts",
        messages("error.additionalShops.grossReceipts.negative", 2021.toString),
        form
      )
    }

    "return 400 and error message for invalid cost of purchase" in {

      val formData = Map("additionalShops[0].costOfPurchase" -> "xxx")

      val form = AdditionalShopsForm.additionalShopsForm(years)(messages).bind(formData)
      mustContainError(
        "additionalShops[0].costOfPurchase",
        messages("error.additionalShops.costOfPurchase.range", 2023.toString),
        form
      )
    }

    "return 400 and error message for empty cost of purchase" in {

      val formData = Map("additionalShops[1].costOfPurchase" -> "")

      val form = AdditionalShopsForm.additionalShopsForm(years)(messages).bind(formData)
      mustContainError(
        "additionalShops[1].costOfPurchase",
        messages("error.additionalShops.costOfPurchase.required", 2022.toString),
        form
      )
    }

    "return 400 and error message for negative cost of purchase" in {

      val formData = Map("additionalShops[2].costOfPurchase" -> "-1")

      val form = AdditionalShopsForm.additionalShopsForm(years)(messages).bind(formData)
      mustContainError(
        "additionalShops[2].costOfPurchase",
        messages("error.additionalShops.costOfPurchase.negative", 2021.toString),
        form
      )
    }
  }
}
