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

import form.aboutyouandtheproperty.CommercialLettingAvailabilityWelshForm
import models.submissions.aboutyouandtheproperty.{AboutYouAndThePropertyPartTwo, LettingAvailability}
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentAsString, contentType, redirectLocation, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

import java.time.LocalDate

class CommercialLettingAvailabilityWelshControllerSpec extends TestBaseSpec {

  import utils.FormBindingTestAssertions._

  val years = Seq("2024", "2023", "2022")

  private def validFormDataPerYear(idx: Int): Seq[(String, String)] =
    Seq(
      s"lettingAvailability-$idx" -> "100"
    )

  private def formData(idx: Int): Seq[(String, String)] =
    validFormDataPerYear(0) ++
      validFormDataPerYear(1) ++
      validFormDataPerYear(2)

  def controller(
    aboutYouAndThePropertyPartTwo: Option[AboutYouAndThePropertyPartTwo] = Option(
      prefilledAboutYouAndThePropertyPartTwo6048
    )
  ) = new CommercialLettingAvailabilityWelshController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    commercialLettingAvailabilityWelshView,
    preEnrichedActionRefiner(aboutYouAndThePropertyPartTwo = aboutYouAndThePropertyPartTwo),
    mockSessionRepo
  )

  "Commercial letting availability welsh controller GET" should {
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
        controllers.aboutyouandtheproperty.routes.CommercialLettingQuestionController.show().url
      )
    }
  }

  "Commercial letting availability welsh controller SUBMIT /" should {
    "return 400 for form with errors" in {
      val res = controller().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
      status(res) shouldBe BAD_REQUEST
    }

    "save the form data and redirect to the next page" in {
      val res = controller().submit(
        fakePostRequest.withFormUrlEncodedBody(formData(1)*)
      )
      status(res)           shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(
        controllers.aboutyouandtheproperty.routes.CompletedCommercialLettingsWelshController.show().url
      )
    }

    "return 400 and error message for invalid character" in {

      val formData = Map("lettingAvailability-0" -> "xxx")

      val form =
        CommercialLettingAvailabilityWelshForm.commercialLettingAvailabilityWelshForm(years)(messages).bind(formData)
      mustContainError(
        "lettingAvailability-0",
        messages("error.commercialLettingAvailability.welsh.range", 2024.toString),
        form
      )
    }

    "return 400 and error message for empty input" in {

      val formData = Map("lettingAvailability-1" -> "")

      val form =
        CommercialLettingAvailabilityWelshForm.commercialLettingAvailabilityWelshForm(years)(messages).bind(formData)
      mustContainError(
        "lettingAvailability-1",
        messages("error.commercialLettingAvailability.welsh.required", 2023.toString),
        form
      )
    }

    "return 400 and error message for invalid number" in {

      val formData = Map("lettingAvailability-2" -> "366")

      val form =
        CommercialLettingAvailabilityWelshForm.commercialLettingAvailabilityWelshForm(years)(messages).bind(formData)
      mustContainError(
        "lettingAvailability-2",
        messages("error.commercialLettingAvailability.welsh.range", 2022.toString),
        form
      )
    }

  }

  "LettingAvailability" should {
    "serialize and deserialize correctly" in {
      val lettingAvailability = LettingAvailability(
        financialYearEnd = LocalDate.of(2024, 3, 31),
        numberOfNights = 120
      )

      val json = Json.toJson(lettingAvailability)
      json.as[LettingAvailability] shouldBe lettingAvailability
    }
  }
}
