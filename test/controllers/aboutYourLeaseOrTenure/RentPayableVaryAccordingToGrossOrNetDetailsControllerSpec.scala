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

package controllers.aboutYourLeaseOrTenure

import form.aboutYourLeaseOrTenure.RentPayableVaryAccordingToGrossOrNetDetailsForm.rentPayableVaryAccordingToGrossOrNetInformationForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec

class RentPayableVaryAccordingToGrossOrNetDetailsControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}

  def rentPayableVaryAccordingToGrossOrNetDetailsController(
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(prefilledAboutLeaseOrAgreementPartTwo)
  ) =
    new RentPayableVaryAccordingToGrossOrNetDetailsController(
      stubMessagesControllerComponents(),
      aboutYourLeaseOrTenureNavigator,
      rentPayableVaryAccordingToGrossOrNetDetailsView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartTwo = aboutLeaseOrAgreementPartTwo),
      mockSessionRepo
    )

  "RentPayableVaryAccordingToGrossOrNetDetailsController GET /" should {
    "return 200 with Rent Payable Vary Gross or Net Details in the session" in {
      val result = rentPayableVaryAccordingToGrossOrNetDetailsController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryAccordingToGrossOrNetController.show().url
      )
    }

    "return 200 with Rent Payable Vary Gross or Net Details with none in the session" in {
      val controller = rentPayableVaryAccordingToGrossOrNetDetailsController(aboutLeaseOrAgreementPartTwo = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryAccordingToGrossOrNetController.show().url
      )
    }
  }

  "RentPayableVaryAccordingToGrossOrNetDetailsController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = rentPayableVaryAccordingToGrossOrNetDetailsController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

  "Rent payable vary according to gross or net details form" should {
    "error if choice is missing" in {
      val formData = baseFormData - errorKey.rentPayableVaryAccordingToGrossOrNetDetails
      val form     = rentPayableVaryAccordingToGrossOrNetInformationForm.bind(formData)

      mustContainError(
        errorKey.rentPayableVaryAccordingToGrossOrNetDetails,
        "error.rentPayableVaryAccordingToGrossOrNetDetails.required",
        form
      )
    }
  }

  object TestData {
    val errorKey: Object {
      val rentPayableVaryAccordingToGrossOrNetDetails: String
    } = new {
      val rentPayableVaryAccordingToGrossOrNetDetails: String = "rentPayableVaryAccordingToGrossOrNetDetails"
    }

    val baseFormData: Map[String, String] = Map(
      "rentPayableVaryAccordingToGrossOrNetDetails" -> "Test content"
    )
  }
}
