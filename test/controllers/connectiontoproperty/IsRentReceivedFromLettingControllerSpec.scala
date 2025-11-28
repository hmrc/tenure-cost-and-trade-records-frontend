/*
 * Copyright 2025 HM Revenue & Customs
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

import connectors.Audit
import form.connectiontoproperty.isRentReceivedFromLettingForm.isRentReceivedFromLettingForm
import models.submissions.connectiontoproperty.StillConnectedDetails
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec

import scala.language.reflectiveCalls

class IsRentReceivedFromLettingControllerSpec extends TestBaseSpec {
  import TestData._

  val mockAudit: Audit = mock[Audit]
  def isRentReceivedFromLettingController(
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledStillConnectedDetailsYesToAll)
  )                    =
    new IsRentReceivedFromLettingController(
      stubMessagesControllerComponents(),
      mockAudit,
      connectedToPropertyNavigator,
      isRentReceivedFromLettingView,
      preEnrichedActionRefiner(stillConnectedDetails = stillConnectedDetails),
      mockSessionRepo
    )

  "IsRentReceivedFromLettingController GET /" should {

    "return 200 and HTML with Is rent received from letting in session" in {
      val result = isRentReceivedFromLettingController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.VacantPropertiesStartDateController.show().url
      )
    }

    "return 200 when no rent received in session" in {
      val result = isRentReceivedFromLettingController(
        Some(prefilledStillConnectedDetailsNoneOwnProperty)
      ).show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some(UTF8)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.VacantPropertiesStartDateController.show().url
      )
    }
  }

  "IsRentReceivedFromLettingController POST /" should {

    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = isRentReceivedFromLettingController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted without CYA param" in {
      val result = isRentReceivedFromLettingController().submit()(
        FakeRequest(POST, "").withFormUrlEncodedBody("isRentReceivedFromLetting" -> "yes")
      )
      status(result) shouldBe SEE_OTHER
    }

    "Redirect when form data submitted with CYA param" in {
      val result = isRentReceivedFromLettingController().submit()(
        FakeRequest(POST, "/path?from=CYA").withFormUrlEncodedBody("isRentReceivedFromLetting" -> "yes")
      )
      status(result) shouldBe SEE_OTHER
    }

    "Is rent received from letting form" should {
      "error if isRentReceivedFromLetting is missing" in {
        val formData = baseFormData - errorKey.isRentReceivedFromLetting
        val form     = isRentReceivedFromLettingForm.bind(formData)

        mustContainError(errorKey.isRentReceivedFromLetting, "error.isRentReceivedFromLetting.missing", form)
      }
    }
  }

  "getBackLink" should {
    "return back link to CYA page if query param present" in {
      val result = isRentReceivedFromLettingController().show(fakeRequestFromCYA)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.CheckYourAnswersConnectionToVacantPropertyController.show().url
      )
    }
    "return back link to is the property vacant start date page if 'from' query param is not present" in {
      val result = isRentReceivedFromLettingController().show(fakeRequest)
      contentAsString(result) should include(
        controllers.connectiontoproperty.routes.VacantPropertiesStartDateController.show().url
      )
    }
  }

  object TestData {
    val errorKey = new ErrorKey

    class ErrorKey {
      val isRentReceivedFromLetting = "isRentReceivedFromLetting"
    }

    val baseFormData: Map[String, String] = Map(
      "isRentReceivedFromLetting" -> "yes"
    )
  }
}
