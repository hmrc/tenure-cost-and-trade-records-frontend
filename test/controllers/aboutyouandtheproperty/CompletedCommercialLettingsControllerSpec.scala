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

import form.aboutyouandtheproperty.CompletedCommercialLettingsForm.completedCommercialLettingsForm
import models.submissions.aboutyouandtheproperty.AboutYouAndThePropertyPartTwo
import play.api.http.Status
import play.api.http.Status.{BAD_REQUEST, SEE_OTHER}
import play.api.test.FakeRequest
import play.api.test.Helpers.{POST, charset, contentAsString, contentType, status, stubMessagesControllerComponents}
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec

class CompletedCommercialLettingsControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}

  def controller(
    aboutYouAndThePropertyPartTwo: Option[AboutYouAndThePropertyPartTwo] = Option(
      prefilledAboutYouAndThePropertyPartTwo6048
    )
  ) = new CompletedCommercialLettingsController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    completedCommercialLettingsView,
    preEnrichedActionRefiner(aboutYouAndThePropertyPartTwo = aboutYouAndThePropertyPartTwo),
    mockSessionRepo
  )

  "Completed commercial letting controller" should {
    "return 200" in {
      val result = controller().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = controller().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and HTML when the session is None" in {
      val controllerNone = controller(aboutYouAndThePropertyPartTwo = None)
      val result         = controllerNone.show()(fakeRequest)
      status(result)      shouldBe Status.OK
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

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = controller().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted" in {
      val res = controller().submit(
        FakeRequest(POST, "").withFormUrlEncodedBody(
          "completedCommercialLettings" -> "4"
        )
      )
      status(res) shouldBe SEE_OTHER
    }
  }

  "Form" should {
    "error if empty form submitted" in {
      val formData = baseFormData - errorKey.numberOfDays
      val form     = completedCommercialLettingsForm.bind(formData)
      mustContainError(errorKey.numberOfDays, "error.completedCommercialLettings.required", form)
    }

    "error if non-numeric value is submitted" in {
      val formData = baseFormData.updated(errorKey.numberOfDays, "abc")
      val form     = completedCommercialLettingsForm.bind(formData)
      mustContainError(errorKey.numberOfDays, "error.completedCommercialLettings.range", form)
    }

    "error if number of days is below the minimum range" in {
      val formData = baseFormData.updated(errorKey.numberOfDays, "-1")
      val form     = completedCommercialLettingsForm.bind(formData)
      mustContainError(errorKey.numberOfDays, "error.completedCommercialLettings.range", form)
    }

    "error if number of days is above the maximum range" in {
      val formData = baseFormData.updated(errorKey.numberOfDays, "366")
      val form     = completedCommercialLettingsForm.bind(formData)
      mustContainError(errorKey.numberOfDays, "error.completedCommercialLettings.range", form)
    }

    "bind successfully if number of days is within the valid range" in {
      val formData = baseFormData.updated(errorKey.numberOfDays, "100")
      val form     = completedCommercialLettingsForm.bind(formData)
      form.hasErrors shouldBe false
      form.get       shouldBe 100
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val numberOfDays = "completedCommercialLettings"
    }

    val baseFormData: Map[String, String] = Map(
      "completedCommercialLettings" -> "9"
    )

  }
}
