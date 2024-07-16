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

import form.aboutYourLeaseOrTenure.RentPayableVaryAccordingToGrossOrNetForm.rentPayableVaryAccordingToGrossOrNetForm
import models.ForTypes
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec
import scala.language.reflectiveCalls

class RentPayableVaryAccordingToGrossOrNetControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}

  def rentPayableVaryAccordingToGrossOrNetController(
    forType: String = ForTypes.for6010,
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(prefilledAboutLeaseOrAgreementPartTwo)
  ) = new RentPayableVaryAccordingToGrossOrNetController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    rentPayableVaryAccordingToGrossOrNetView,
    preEnrichedActionRefiner(forType = forType, aboutLeaseOrAgreementPartTwo = aboutLeaseOrAgreementPartTwo),
    mockSessionRepo
  )

  "RentPayableVaryAccordingToGrossOrNet controller GET /" should {
    "return 200 and HTML with Rent Payable Vary Gross or Net and 6010 in the sessions" in {
      val result = rentPayableVaryAccordingToGrossOrNetController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentIncreaseAnnuallyWithRPIController.show().url
      )
    }

    "return 200 and HTML with Rent Payable Vary Gross or Net with none and 6010 in the sessions" in {
      val controller = rentPayableVaryAccordingToGrossOrNetController(aboutLeaseOrAgreementPartTwo = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentIncreaseAnnuallyWithRPIController.show().url
      )
    }

    "return 200 and HTML with Rent Payable Vary Gross or Net and form type invalid in the sessions" in {
      val controller = rentPayableVaryAccordingToGrossOrNetController(forType = "6060")
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.routes.TaskListController.show().url
      )
    }

    "return 200 and HTML with Rent Payable Vary Gross or Net and 6030 in the sessions" in {
      val controller = rentPayableVaryAccordingToGrossOrNetController(forType = ForTypes.for6030)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.WhatIsYourRentBasedOnController.show().url
      )
    }
  }

  "RentPayableVaryAccordingToGrossOrNetControllerSUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = rentPayableVaryAccordingToGrossOrNetController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

  "Rent payable vary according to gross or net form" should {
    "error if choice is missing " in {
      val formData = baseFormData - errorKey.rentPayableVaryAccordingToGrossOrNet
      val form     = rentPayableVaryAccordingToGrossOrNetForm.bind(formData)

      mustContainError(
        errorKey.rentPayableVaryAccordingToGrossOrNet,
        "error.rentPayableVaryAccordingToGrossOrNet.missing",
        form
      )
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val rentPayableVaryAccordingToGrossOrNet: String = "rentPayableVaryAccordingToGrossOrNet"
    }

    val baseFormData: Map[String, String] = Map(
      "rentPayableVaryAccordingToGrossOrNet" -> "true"
    )
  }
}
