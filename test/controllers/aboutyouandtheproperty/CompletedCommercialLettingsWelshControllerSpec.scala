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

package controllers.aboutyouandtheproperty

import form.aboutyouandtheproperty.CompletedCommercialLettingsWelshForm
import models.submissions.aboutyouandtheproperty.{AboutYouAndThePropertyPartTwo, CompletedLettings}
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentAsString, contentType, redirectLocation, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

import java.time.LocalDate

class CompletedCommercialLettingsWelshControllerSpec extends TestBaseSpec {

  import utils.FormBindingTestAssertions._

  val years = Seq("2024", "2023", "2022")

  private def validFormDataPerYear(idx: Int): Seq[(String, String)] =
    Seq(
      s"completedLettings-$idx" -> "100"
    )

  private def formData(idx: Int): Seq[(String, String)] =
    validFormDataPerYear(0) ++
      validFormDataPerYear(1) ++
      validFormDataPerYear(2)

  def controller(
    aboutYouAndThePropertyPartTwo: Option[AboutYouAndThePropertyPartTwo] = Option(
      prefilledAboutYouAndThePropertyPartTwo6048
    )
  ) = new CompletedCommercialLettingsWelshController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    completedCommercialLettingsWelshView,
    preEnrichedActionRefiner(aboutYouAndThePropertyPartTwo = aboutYouAndThePropertyPartTwo),
    mockSessionRepo
  )

  "Completed commercial lettings welsh controller GET" should {
    "return 200" in {
      val result = controller().show(fakeRequest)
      status(result) shouldBe OK
    }

    "return HTML" in {
      val result = controller().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return correct backLink when 'from=CYA' query param is present" in {
      val result = controller().show()(fakeRequestFromCYA)
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show().url
      )
    }
    "return correct backLink when no query param is present" in {
      val result = controller().show()(fakeRequest)
      contentAsString(result) should include(
        controllers.aboutyouandtheproperty.routes.CommercialLettingAvailabilityController.show().url
      )
    }
  }

  "Completed commercial lettings welsh controller SUBMIT /" should {
    "return 400 for form with errors" in {
      val res = controller().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
      status(res) shouldBe BAD_REQUEST
    }

    "save the form data and redirect to the next page" in {
      val res = controller().submit(
        fakePostRequest.withFormUrlEncodedBody(formData(12)*)
      )
      status(res)           shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Option(controllers.routes.TaskListController.show().url) // TOD0
    }

    "return 400 and error message for invalid character" in {

      val formData = Map("completedLettings-0" -> "xxx")

      val form =
        CompletedCommercialLettingsWelshForm.completedCommercialLettingsWelshForm(years)(messages).bind(formData)
      mustContainError(
        "completedLettings-0",
        messages("error.completedCommercialLettings.welsh.range", 2024.toString),
        form
      )
    }

    "return 400 and error message for empty input" in {

      val formData = Map("completedLettings-1" -> "")

      val form =
        CompletedCommercialLettingsWelshForm.completedCommercialLettingsWelshForm(years)(messages).bind(formData)
      mustContainError(
        "completedLettings-1",
        messages("error.completedCommercialLettings.welsh.required", 2023.toString),
        form
      )
    }

    "return 400 and error message for invalid number" in {

      val formData = Map("completedLettings-2" -> "366")

      val form =
        CompletedCommercialLettingsWelshForm.completedCommercialLettingsWelshForm(years)(messages).bind(formData)
      mustContainError(
        "completedLettings-2",
        messages("error.completedCommercialLettings.welsh.range", 2022.toString),
        form
      )
    }

    "CompletedLettings" should {
      "serialize and deserialize correctly" in {
        val completedLettings = CompletedLettings(
          financialYearEnd = LocalDate.of(2024, 3, 31),
          numberOfNights = 120
        )

        val json = Json.toJson(completedLettings)
        json.as[CompletedLettings] shouldBe completedLettings
      }
    }

  }

}
