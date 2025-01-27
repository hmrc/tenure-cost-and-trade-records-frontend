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

package controllers.aboutYourLeaseOrTenure

import connectors.Audit
import form.aboutYourLeaseOrTenure.RentPayableVaryAccordingToGrossOrNetDetailsForm.rentPayableVaryAccordingToGrossOrNetInformationForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec

import scala.language.reflectiveCalls

class RentPayableVaryAccordingToGrossOrNetDetailsControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}

  val mockAudit: Audit = mock[Audit]

  def rentPayableVaryAccordingToGrossOrNetDetailsController(
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(prefilledAboutLeaseOrAgreementPartTwo)
  ) =
    new RentPayableVaryAccordingToGrossOrNetDetailsController(
      stubMessagesControllerComponents(),
      mockAudit,
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
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted" in {
      val res = rentPayableVaryAccordingToGrossOrNetDetailsController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody(
          "rentPayableVaryAccordingToGrossOrNetDetails" -> "text"
        )
      )
      status(res) shouldBe SEE_OTHER
    }

    "throw a BAD_REQUEST if describeFittingsTextArea is greater than max length is submitted" in {
      val res = rentPayableVaryAccordingToGrossOrNetDetailsController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody(
          "rentPayableVaryAccordingToGrossOrNetDetails" -> "x" * 501
        )
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
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val rentPayableVaryAccordingToGrossOrNetDetails: String = "rentPayableVaryAccordingToGrossOrNetDetails"
    }

    val baseFormData: Map[String, String] = Map(
      "rentPayableVaryAccordingToGrossOrNetDetails" -> "Test content"
    )
  }
}
