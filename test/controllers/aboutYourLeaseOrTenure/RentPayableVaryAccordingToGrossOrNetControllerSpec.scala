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

import form.aboutYourLeaseOrTenure.RentPayableVaryAccordingToGrossOrNetForm.rentPayableVaryAccordingToGrossOrNetForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec

class RentPayableVaryAccordingToGrossOrNetControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}

  def rentPayableVaryAccordingToGrossOrNetController(
    aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = Some(prefilledAboutLeaseOrAgreementPartOne)
  ) =
    new RentPayableVaryAccordingToGrossOrNetController(
      stubMessagesControllerComponents(),
      aboutYourLeaseOrTenureNavigator,
      rentPayableVaryAccordingToGrossOrNetView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartOne = aboutLeaseOrAgreementPartOne),
      mockSessionRepo
    )

  def rentPayableVaryAccordingToGrossOrNetControllerNone = new RentPayableVaryAccordingToGrossOrNetController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    rentPayableVaryAccordingToGrossOrNetView,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartOne = None),
    mockSessionRepo
  )

  "RentPayableVaryAccordingToGrossOrNet controller GET /" should {
    "return 200" in {
      val result = rentPayableVaryAccordingToGrossOrNetController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = rentPayableVaryAccordingToGrossOrNetController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and HTML with None in the session" in {
      val result = rentPayableVaryAccordingToGrossOrNetControllerNone.show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "RentPayableVaryAccordingToGrossOrNetControllerSUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = rentPayableVaryAccordingToGrossOrNetController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
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
    val errorKey: Object {
      val rentPayableVaryAccordingToGrossOrNet: String
    } = new {
      val rentPayableVaryAccordingToGrossOrNet: String = "rentPayableVaryAccordingToGrossOrNet"
    }

    val baseFormData: Map[String, String] = Map(
      "rentPayableVaryAccordingToGrossOrNet" -> "true"
    )
  }
}
