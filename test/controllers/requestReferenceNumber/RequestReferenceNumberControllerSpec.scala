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

package controllers.requestReferenceNumber

import form.requestReferenceNumber.RequestReferenceNumberForm.requestReferenceNumberForm
import models.submissions.requestReferenceNumber.RequestReferenceNumberDetails
import play.api.http.Status
import play.api.test.Helpers._
import utils.TestBaseSpec

class RequestReferenceNumberControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}
  import utils.FormBindingTestAssertions.mustContainError

  def noReferenceNumberController(
    requestReferenceNumberDetails: Option[RequestReferenceNumberDetails] = Some(prefilledRequestRefNumCYA)
  ) = new RequestReferenceNumberController(
    stubMessagesControllerComponents(),
    requestReferenceNumberNavigator,
    requestReferenceAddressView,
    preEnrichedActionRefiner(requestReferenceNumberDetails = requestReferenceNumberDetails),
    mockSessionRepo
  )

  def noReferenceNumberControllerEmpty(
    requestReferenceNumberDetails: Option[RequestReferenceNumberDetails] = None
  ) = new RequestReferenceNumberController(
    stubMessagesControllerComponents(),
    requestReferenceNumberNavigator,
    requestReferenceAddressView,
    preEnrichedActionRefiner(requestReferenceNumberDetails = requestReferenceNumberDetails),
    mockSessionRepo
  )

  "GET /" should {
    "return 200" in {
      val result = noReferenceNumberController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = noReferenceNumberController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 303" in {
      val result = noReferenceNumberController().startWithSession(fakeRequest)
      status(result) shouldBe Status.SEE_OTHER
    }
  }

  "GET empty session" should {
    "return 200" in {
      val result = noReferenceNumberControllerEmpty().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = noReferenceNumberControllerEmpty().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = noReferenceNumberController().submit(
        fakeRequest.withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

  "Edit address form" should {
    "error if building number is missing" in {
      val formDataWithEmptyBuildingNameNumber = baseFormData.updated(TestData.errorKey.buildingNameNumber, "")
      val form                                = requestReferenceNumberForm.bind(formDataWithEmptyBuildingNameNumber)

      mustContainError(errorKey.buildingNameNumber, "error.buildingNameNumber.required", form)
    }

    "error if town or city is missing" in {
      val formDataWithEmptyTown = baseFormData.updated(TestData.errorKey.town, "")
      val form                  = requestReferenceNumberForm.bind(formDataWithEmptyTown)

      mustContainError(errorKey.town, "error.townCity.required", form)
    }

    "error if postcode is missing" in {
      val formData = baseFormData - errorKey.postcode
      val form     = requestReferenceNumberForm.bind(formData)

      mustContainError(errorKey.postcode, "error.postcode.required", form)
    }
  }

  object TestData {
    val errorKey = new {
      val buildingNameNumber = "requestReferenceNumberAddress.buildingNameNumber"
      val town               = "requestReferenceNumberAddress.town"
      val postcode           = "requestReferenceNumberAddress.postcode"
    }

    val baseFormData: Map[String, String] = Map(
      "requestReferenceNumberAddress.postcode" -> "BN12 4AX"
    )
  }
}
