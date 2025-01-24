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

import connectors.Audit
import form.aboutyouandtheproperty.CommercialLettingAvailabilityForm.commercialLettingAvailabilityForm
import models.submissions.aboutyouandtheproperty.AboutYouAndThePropertyPartTwo
import play.api.http.Status
import play.api.http.Status._
import play.api.test.FakeRequest
import play.api.test.Helpers.{POST, charset, contentAsString, contentType, status, stubMessagesControllerComponents}
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec

class CommercialLettingAvailabilityControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}

  val mockAudit: Audit = mock[Audit]
  def controller(
    aboutYouAndThePropertyPartTwo: Option[AboutYouAndThePropertyPartTwo] = Option(
      prefilledAboutYouAndThePropertyPartTwo6048
    )
  ) = new CommercialLettingAvailabilityController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYouAndThePropertyNavigator,
    commercialLettingAvailabilityView,
    preEnrichedActionRefiner(aboutYouAndThePropertyPartTwo = aboutYouAndThePropertyPartTwo),
    mockSessionRepo
  )

  "Commercial letting availability controller" should {
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
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = controller().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted" in {
      val res = controller().submit(
        FakeRequest(POST, "").withFormUrlEncodedBody(
          "commercialLettingAvailability" -> "4"
        )
      )
      status(res) shouldBe SEE_OTHER
    }
  }

  "Form" should {
    "error if empty form submitted" in {
      val formData = baseFormData - errorKey.numberOfDays
      val form     = commercialLettingAvailabilityForm.bind(formData)
      mustContainError(errorKey.numberOfDays, "error.commercialLettingAvailability.required", form)
    }

    "error if non-numeric value is submitted" in {
      val formData = baseFormData.updated(errorKey.numberOfDays, "abc")
      val form     = commercialLettingAvailabilityForm.bind(formData)
      mustContainError(errorKey.numberOfDays, "error.commercialLettingAvailability.range", form)
    }

    "error if number of days is below the minimum range" in {
      val formData = baseFormData.updated(errorKey.numberOfDays, "-1")
      val form     = commercialLettingAvailabilityForm.bind(formData)
      mustContainError(errorKey.numberOfDays, "error.commercialLettingAvailability.range", form)
    }

    "error if number of days is above the maximum range" in {
      val formData = baseFormData.updated(errorKey.numberOfDays, "366")
      val form     = commercialLettingAvailabilityForm.bind(formData)
      mustContainError(errorKey.numberOfDays, "error.commercialLettingAvailability.range", form)
    }

    "bind successfully if number of days is within the valid range" in {
      val formData = baseFormData.updated(errorKey.numberOfDays, "100")
      val form     = commercialLettingAvailabilityForm.bind(formData)
      form.hasErrors shouldBe false
      form.get       shouldBe 100
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val numberOfDays = "commercialLettingAvailability"
    }

    val baseFormData: Map[String, String] = Map(
      "commercialLettingAvailability" -> "9"
    )

  }
}
