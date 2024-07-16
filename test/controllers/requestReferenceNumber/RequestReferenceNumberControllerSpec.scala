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

package controllers.requestReferenceNumber

import form.requestReferenceNumber.RequestReferenceNumberForm.requestReferenceNumberForm
import models.submissions.requestReferenceNumber.RequestReferenceNumberDetails
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec
import scala.language.reflectiveCalls

class RequestReferenceNumberControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}
  import utils.FormBindingTestAssertions.mustContainError

  private val postRequest = FakeRequest("POST", "/")

  def noReferenceNumberController(
    requestReferenceNumberDetails: Option[RequestReferenceNumberDetails] = Some(prefilledRequestRefNumCYA)
  ) = new RequestReferenceNumberController(
    stubMessagesControllerComponents(),
    requestReferenceNumberNavigator,
    requestReferenceAddressView,
    preEnrichedActionRefiner(requestReferenceNumberDetails = requestReferenceNumberDetails),
    mockSessionRepo
  )

  def noReferenceNumberControllerBlank(
    requestReferenceNumberDetails: Option[RequestReferenceNumberDetails] = Some(prefilledRequestRefNumBlank)
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

    "return 200 with empty session" in {
      val result = noReferenceNumberControllerBlank().show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = noReferenceNumberController().submit(
        fakeRequest.withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "return redirect to next page for completed form SEE_OTHER" in {
      val result = noReferenceNumberController().submit()(
        postRequest.withFormUrlEncodedBody(
          "requestReferenceNumberBusinessTradingName"        -> "Wombles Inc",
          "requestReferenceNumberAddress.buildingNameNumber" -> "Building Name Number",
          "requestReferenceNumberAddress.street1"            -> "",
          "requestReferenceNumberAddress.town"               -> "Town",
          "requestReferenceNumberAddress.county"             -> "",
          "requestReferenceNumberAddress.postcode"           -> "BN12 4AX"
        )
      )
      status(result) shouldBe Status.SEE_OTHER
      redirectLocation(result) shouldBe Some(
        controllers.requestReferenceNumber.routes.RequestReferenceNumberContactDetailsController.show().url
      )
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
    val errorKey = new ErrorKey

    class ErrorKey {
      val buildingNameNumber = "requestReferenceNumberAddress.buildingNameNumber"
      val town               = "requestReferenceNumberAddress.town"
      val postcode           = "requestReferenceNumberAddress.postcode"
    }

    val baseFormData: Map[String, String] = Map(
      "requestReferenceNumberAddress.postcode" -> "BN12 4AX"
    )
  }
}
