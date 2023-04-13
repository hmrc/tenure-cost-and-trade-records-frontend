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

import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.FormBindingTestAssertions.mustContainRequiredErrorFor
import utils.TestBaseSpec

class PremisesLicenseGrantedDetailsControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}
  import form.aboutyouandtheproperty.PremisesLicenseGrantedDetailsForm.premisesLicenseGrantedInformationDetailsForm

  def premisesLicenseGrantedDetailsController(
    aboutYouAndTheProperty: Option[AboutYouAndTheProperty] = Some(prefilledAboutYouAndThePropertyYes)
  ) = new PremisesLicenseGrantedDetailsController(
    stubMessagesControllerComponents(),
    aboutYouAndThePropertyNavigator,
    premisesLicenceGrantedDetailsView,
    preEnrichedActionRefiner(aboutYouAndTheProperty = aboutYouAndTheProperty),
    mockSessionRepo
  )

  "GET /" should {
    "return 200" in {
      val result = premisesLicenseGrantedDetailsController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = premisesLicenseGrantedDetailsController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = premisesLicenseGrantedDetailsController().submit(FakeRequest().withFormUrlEncodedBody(Seq.empty: _*))
      status(res) shouldBe BAD_REQUEST
    }
  }

  "Premises licence granted form" should {
    "error if choice is missing " in {
      val formData = baseFormData - errorKey.premisesLicenseGrantedInformation
      val form     = premisesLicenseGrantedInformationDetailsForm.bind(formData)

      mustContainRequiredErrorFor(errorKey.premisesLicenseGrantedInformation, form)
    }
  }

  object TestData {
    val errorKey: Object {
      val premisesLicenseGrantedInformation: String
    } = new {
      val premisesLicenseGrantedInformation: String = "premisesLicenseGrantedInformation"
    }

    val baseFormData: Map[String, String] = Map(
      "premisesLicenseGrantedInformation" -> "Test content"
    )
  }
}
