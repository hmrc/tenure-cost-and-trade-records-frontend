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

package controllers.connectiontoproperty

import models.submissions.connectiontoproperty.StillConnectedDetails
import form.connectiontoproperty.EditAddressForm.editAddressForm
import play.api.http.Status
import play.api.test.Helpers._
import utils.TestBaseSpec
import scala.language.reflectiveCalls

class EditAddressControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}
  import utils.FormBindingTestAssertions.mustContainError

  def editAddressController(
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledStillConnectedDetailsYes)
  ) = new EditAddressController(
    stubMessagesControllerComponents(),
    connectedToPropertyNavigator,
    editAddressView,
    preEnrichedActionRefiner(stillConnectedDetails = stillConnectedDetails),
    mockSessionRepo
  )

  def editAddressControllerEditAddress(
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledStillConnectedDetailsEdit)
  ) = new EditAddressController(
    stubMessagesControllerComponents(),
    connectedToPropertyNavigator,
    editAddressView,
    preEnrichedActionRefiner(stillConnectedDetails = stillConnectedDetails),
    mockSessionRepo
  )

  "GET /" should {
    "return 200" in {
      val result = editAddressController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = editAddressController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 edit address in session" in {
      val result = editAddressControllerEditAddress().show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = editAddressController().submit(
        fakeRequest.withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

  "calculateBackLink" should {

    "return back link to CYA page when 'from=CYA' query param is present for vacant properties" in {
      val result = editAddressController(
        stillConnectedDetails = Some(prefilledStillConnectedVacantYes)
      ).show(fakeRequestFromCYA)

      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.CheckYourAnswersConnectionToVacantPropertyController.show().url
      )
    }

    "return back link to CYA page when 'from=CYA' query param is present for not vacant properties" in {
      val result = editAddressController(
        stillConnectedDetails = Some(prefilledStillConnectedVacantNo)
      ).show(fakeRequestFromCYA)

      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.CheckYourAnswersConnectionToPropertyController.show().url
      )
    }

    "return back link to Are You Still Connected page when no 'from' query param is present" in {
      val result = editAddressController().show(fakeRequest)

      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.AreYouStillConnectedController.show().url
      )
    }
  }

  "Edit address form" should {
    "error if building number is missing" in {
      val formDataWithEmptybuildingNameNumber = baseFormData.updated(TestData.errorKey.buildingNameNumber, "")
      val form                                = editAddressForm.bind(formDataWithEmptybuildingNameNumber)

      mustContainError(errorKey.buildingNameNumber, "error.buildingNameNumber.required", form)
    }

    "error if postcode is missing" in {
      val formData = baseFormData - errorKey.postcode
      val form     = editAddressForm.bind(formData)

      mustContainError(errorKey.postcode, "error.postcode.required", form)
    }
  }

  object TestData {
    val errorKey = new ErrorKey

    class ErrorKey {
      val buildingNameNumber = "editAddress.buildingNameNumber"
      val postcode           = "editAddress.postcode"
    }

    val baseFormData: Map[String, String] = Map(
      "editAddress.buildingNameNumber" -> "",
      "editAddress.postcode"           -> "BN12 4AX"
    )
  }
}
