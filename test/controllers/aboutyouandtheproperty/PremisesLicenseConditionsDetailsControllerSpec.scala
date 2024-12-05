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

import form.aboutyouandtheproperty.PremisesLicenseConditionsDetailsForm.premisesLicenceDetailsForm
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec
import scala.language.reflectiveCalls

class PremisesLicenseConditionsDetailsControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}

  def premisesLicenseConditionsDetailsController(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes)
  ) = new PremisesLicenseConditionsDetailsController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    premisesLicenceConditionsDetailsView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  def premisesLicenseConditionsDetailsControllerNone() = new PremisesLicenseConditionsDetailsController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    premisesLicenceConditionsDetailsView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = None),
    mockSessionRepo
  )

  "Premises License conditions details controller" should {
    "return 200 license conditions details in the session" in {
      val result = premisesLicenseConditionsDetailsController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = premisesLicenseConditionsDetailsController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 no license conditions details in the session" in {
      val result = premisesLicenseConditionsDetailsControllerNone().show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res =
          premisesLicenseConditionsDetailsController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty*))
        status(res) shouldBe BAD_REQUEST
      }
    }

    "Redirect when form data submitted" in {
      val res = premisesLicenseConditionsDetailsController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody(
          "premisesLicenseConditionsDetails" -> "test"
        )
      )
      status(res) shouldBe SEE_OTHER
    }

  }

  "Premises License conditions details form" should {
    "error if choice is missing" in {
      val formData = baseFormData - errorKey.premisesLicenseConditionsDetails
      val form     = premisesLicenceDetailsForm.bind(formData)

      mustContainError(
        errorKey.premisesLicenseConditionsDetails,
        "error.premisesLicenseConditionsDetails.required",
        form
      )
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val premisesLicenseConditionsDetails: String = "premisesLicenseConditionsDetails"
    }

    val baseFormData: Map[String, String] = Map(
      "premisesLicenseConditionsDetails" -> "Test content"
    )
  }
}
