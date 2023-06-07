/*
 * Copyright 2023 HM Revenue & Customs
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

  "Premises License conditions details controller" should {
    "return 200" in {
      val result = premisesLicenseConditionsDetailsController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = premisesLicenseConditionsDetailsController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val res =
          premisesLicenseConditionsDetailsController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty: _*))
        status(res) shouldBe BAD_REQUEST
      }
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
    val errorKey: Object {
      val premisesLicenseConditionsDetails: String
    } = new {
      val premisesLicenseConditionsDetails: String = "premisesLicenseConditionsDetails"
    }

    val baseFormData: Map[String, String] = Map(
      "premisesLicenseConditionsDetails" -> "Test content"
    )
  }
}
