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
import form.aboutthetradinghistory.AdditionalMiscForm
import play.api.http.Status
import play.api.http.Status._
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentAsString, contentType, redirectLocation, status, stubMessagesControllerComponents}
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec

class AdditionalMiscControllerSpec extends TestBaseSpec {

  val mockAudit: Audit = mock[Audit]
  val years            = Seq("2023", "2022", "2021")

  private def validFormDataPerYear(idx: Int): Seq[(String, String)] =
    Seq(
      s"additionalMisc.[$idx].leisureReceipts"         -> "10000",
      s"additionalMisc.[$idx].winterStorageReceipts"   -> "10000",
      s"additionalMisc.[$idx].numberOfVans"            -> "1",
      s"additionalMisc.[$idx].otherActivitiesReceipts" -> "10000",
      s"additionalMisc.[$idx].otherServicesReceipts"   -> "10000",
      s"additionalMisc.[$idx].bottledGasReceipts"      -> "10000",
      "details.otherActivitiesReceiptsDetails"         -> "some details",
      "details.leisureReceiptsDetails"                 -> "some details"
    )

  private def validFormData: Seq[(String, String)] =
    validFormDataPerYear(0) ++
      validFormDataPerYear(1) ++
      validFormDataPerYear(2)

  private def invalidFormData: Seq[(String, String)] =
    validFormDataPerYear(0) ++
      validFormDataPerYear(1)

  def controller =
    new AdditionalMiscController(
      stubMessagesControllerComponents(),
      mockAudit,
      aboutYourTradingHistoryNavigator,
      additionalMiscView,
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

    "render a correct back link to additional activities all year page if no query parameters in the url " in {
      val result  = controller.show(fakeRequest)
      val content = contentAsString(result)
      content should include("/additional-amusements")
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
        controllers.aboutthetradinghistory.routes.CheckYourAnswersAdditionalActivitiesController.show().url
      )
    }

    "return 400 and error message for invalid weeks" in {
      val res = controller.submit(
        fakePostRequest.withFormUrlEncodedBody(invalidFormData*)
      )
      status(res)        shouldBe BAD_REQUEST
      contentAsString(res) should include(
        """<a href="#additionalMisc.[2].leisureReceipts">error.additionalMisc.leisureReceipts.required</a>"""
      )
    }

    "return 400 and error message for invalid gross leisure Receipts" in {

      val formData = Map("additionalMisc.[0].leisureReceipts" -> "xxx")

      val form = AdditionalMiscForm.additionalMiscForm(years)(using messages).bind(formData)
      mustContainError(
        "additionalMisc.[0].leisureReceipts",
        messages("error.additionalMisc.leisureReceipts.range", 2023.toString),
        form
      )
    }

    "return 400 and error message for empty gross leisure Receipts" in {

      val formData = Map("additionalMisc.[1].leisureReceipts" -> "")

      val form = AdditionalMiscForm.additionalMiscForm(years)(using messages).bind(formData)
      mustContainError(
        "additionalMisc.[1].leisureReceipts",
        messages("error.additionalMisc.leisureReceipts.required", 2022.toString),
        form
      )
    }

    "return 400 and error message for negative gross leisure Receipts" in {

      val formData = Map("additionalMisc.[2].leisureReceipts" -> "-1")

      val form = AdditionalMiscForm.additionalMiscForm(years)(using messages).bind(formData)
      mustContainError(
        "additionalMisc.[2].leisureReceipts",
        messages("error.additionalMisc.leisureReceipts.negative", 2021.toString),
        form
      )
    }

    "return 400 and error message for gross leisure winter storage receipt" in {

      val formData = Map("additionalMisc.[0].winterStorageReceipts" -> "xxx")

      val form = AdditionalMiscForm.additionalMiscForm(years)(using messages).bind(formData)
      mustContainError(
        "additionalMisc.[0].winterStorageReceipts",
        messages("error.additionalMisc.winterStorageReceipts.range", 2023.toString),
        form
      )
    }

    "return 400 and error message for empty gross leisure winter storage receipt" in {

      val formData = Map("additionalMisc.[1].winterStorageReceipts" -> "")

      val form = AdditionalMiscForm.additionalMiscForm(years)(using messages).bind(formData)
      mustContainError(
        "additionalMisc.[1].winterStorageReceipts",
        messages("error.additionalMisc.winterStorageReceipts.required", 2022.toString),
        form
      )
    }

    "return 400 and error message for negative gross leisure winter storage receipt" in {

      val formData = Map("additionalMisc.[2].winterStorageReceipts" -> "-1")

      val form = AdditionalMiscForm.additionalMiscForm(years)(using messages).bind(formData)
      mustContainError(
        "additionalMisc.[2].winterStorageReceipts",
        messages("error.additionalMisc.winterStorageReceipts.negative", 2021.toString),
        form
      )
    }

    "return 400 and error message for gross other activities receipts" in {

      val formData = Map("additionalMisc.[0].otherActivitiesReceipts" -> "xxx")

      val form = AdditionalMiscForm.additionalMiscForm(years)(using messages).bind(formData)
      mustContainError(
        "additionalMisc.[0].otherActivitiesReceipts",
        messages("error.additionalMisc.otherActivitiesReceipts.range", 2023.toString),
        form
      )
    }

    "return 400 and error message for empty gross other activities receipts" in {

      val formData = Map("additionalMisc.[1].otherActivitiesReceipts" -> "")

      val form = AdditionalMiscForm.additionalMiscForm(years)(using messages).bind(formData)
      mustContainError(
        "additionalMisc.[1].otherActivitiesReceipts",
        messages("error.additionalMisc.otherActivitiesReceipts.required", 2022.toString),
        form
      )
    }

    "return 400 and error message for negative gross other activities receipts" in {

      val formData = Map("additionalMisc.[2].otherActivitiesReceipts" -> "-1")

      val form = AdditionalMiscForm.additionalMiscForm(years)(using messages).bind(formData)
      mustContainError(
        "additionalMisc.[2].otherActivitiesReceipts",
        messages("error.additionalMisc.otherActivitiesReceipts.negative", 2021.toString),
        form
      )
    }
    "return 400 and error message for incorrect number of vans" in {

      val formData = Map("additionalMisc.[0].numberOfVans" -> "xxx")

      val form = AdditionalMiscForm.additionalMiscForm(years)(using messages).bind(formData)
      mustContainError(
        "additionalMisc.[0].numberOfVans",
        messages("error.additionalMisc.numberOfVans.nonNumeric", 2023.toString),
        form
      )
    }

    "return 400 and error message for empty number of vans" in {

      val formData = Map("additionalMisc.[1].numberOfVans" -> "")

      val form = AdditionalMiscForm.additionalMiscForm(years)(using messages).bind(formData)
      mustContainError(
        "additionalMisc.[1].numberOfVans",
        messages("error.additionalMisc.numberOfVans.required", 2022.toString),
        form
      )
    }

    "return 400 and error message for negative number of vans" in {

      val formData = Map("additionalMisc.[2].numberOfVans" -> "-1")

      val form = AdditionalMiscForm.additionalMiscForm(years)(using messages).bind(formData)
      mustContainError(
        "additionalMisc.[2].numberOfVans",
        messages("error.additionalMisc.numberOfVans.negative", 2021.toString),
        form
      )
    }

    "return 400 and error message for incorrect other services receipts" in {

      val formData = Map("additionalMisc.[0].otherServicesReceipts" -> "xxx")

      val form = AdditionalMiscForm.additionalMiscForm(years)(using messages).bind(formData)
      mustContainError(
        "additionalMisc.[0].otherServicesReceipts",
        messages("error.additionalMisc.otherServicesReceipts.range", 2023.toString),
        form
      )
    }

    "return 400 and error message for empty other services receipts" in {

      val formData = Map("additionalMisc.[1].otherServicesReceipts" -> "")

      val form = AdditionalMiscForm.additionalMiscForm(years)(using messages).bind(formData)
      mustContainError(
        "additionalMisc.[1].otherServicesReceipts",
        messages("error.additionalMisc.otherServicesReceipts.required", 2022.toString),
        form
      )
    }

    "return 400 and error message for negative other services receipts" in {

      val formData = Map("additionalMisc.[2].otherServicesReceipts" -> "-1")

      val form = AdditionalMiscForm.additionalMiscForm(years)(using messages).bind(formData)
      mustContainError(
        "additionalMisc.[2].otherServicesReceipts",
        messages("error.additionalMisc.otherServicesReceipts.negative", 2021.toString),
        form
      )
    }

    "return 400 and error message for incorrect bottled gas receipts" in {

      val formData = Map("additionalMisc.[0].bottledGasReceipts" -> "xxx")

      val form = AdditionalMiscForm.additionalMiscForm(years)(using messages).bind(formData)
      mustContainError(
        "additionalMisc.[0].bottledGasReceipts",
        messages("error.additionalMisc.bottledGasReceipts.range", 2023.toString),
        form
      )
    }

    "return 400 and error message for empty bottled gas receipts" in {

      val formData = Map("additionalMisc.[1].bottledGasReceipts" -> "")

      val form = AdditionalMiscForm.additionalMiscForm(years)(using messages).bind(formData)
      mustContainError(
        "additionalMisc.[1].bottledGasReceipts",
        messages("error.additionalMisc.bottledGasReceipts.required", 2022.toString),
        form
      )
    }

    "return 400 and error message for negative bottled gas receipts" in {

      val formData = Map("additionalMisc.[2].bottledGasReceipts" -> "-1")

      val form = AdditionalMiscForm.additionalMiscForm(years)(using messages).bind(formData)
      mustContainError(
        "additionalMisc.[2].bottledGasReceipts",
        messages("error.additionalMisc.bottledGasReceipts.negative", 2021.toString),
        form
      )
    }
  }
}
