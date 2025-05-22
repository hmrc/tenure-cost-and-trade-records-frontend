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

import form.connectiontoproperty.AddAnotherLettingPartOfPropertyForm.theForm
import models.submissions.connectiontoproperty.StillConnectedDetails
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec
import actions.SessionRequest
import connectors.Audit

import scala.language.reflectiveCalls

class AddAnotherLettingPartOfPropertyControllerSpec extends TestBaseSpec {

  import TestData._

  val mockAudit: Audit = mock[Audit]

  def addAnotherLettingPartOfPropertyController(
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledStillConnectedDetailsYesToAll)
  ) =
    new AddAnotherLettingPartOfPropertyController(
      stubMessagesControllerComponents(),
      mockAudit,
      connectedToPropertyNavigator,
      addAnotherLettingPartOfPropertyView,
      genericRemoveConfirmationView,
      preEnrichedActionRefiner(stillConnectedDetails = stillConnectedDetails),
      mockSessionRepo
    )

  "GET /" should {
    "return 200 and HTML with Add another letting part of property in session" in {
      val result = addAnotherLettingPartOfPropertyController().show(0)(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some(UTF8)
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val result = addAnotherLettingPartOfPropertyController().submit(0)(fakeRequest)
      status(result) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted" in {
      val res = addAnotherLettingPartOfPropertyController().submit(0)(
        FakeRequest(POST, "/path?from=CYA").withFormUrlEncodedBody("addAnotherLettingPartOfProperty" -> "yes")
      )
      status(res) shouldBe SEE_OTHER
    }
  }

  "REMOVE /" should {
    "redirect if an empty form is submitted" in {
      val result = addAnotherLettingPartOfPropertyController().remove(1)(fakeRequest)
      status(result) shouldBe SEE_OTHER
    }
  }

  "Remove catering operation" should {
    "render the removal confirmation page on remove" in {
      val controller     = addAnotherLettingPartOfPropertyController()
      val idxToRemove    = 0
      val sessionRequest = SessionRequest(stillConnectedDetailsYesToAllSession, fakeRequest)
      val result         = controller.remove(idxToRemove)(sessionRequest)
      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
    }

    "handle form submission with 'Yes' and perform removal" in {
      val controller      = addAnotherLettingPartOfPropertyController()
      val idxToRemove     = 0
      val requestWithForm = fakeRequest.withFormUrlEncodedBody("genericRemoveConfirmation" -> "yes")
      val sessionRequest  = SessionRequest(stillConnectedDetailsYesToAllSession, requestWithForm)
      val result          = controller.performRemove(idxToRemove)(sessionRequest)
      status(result) shouldBe BAD_REQUEST
    }

    "handle form submission with 'No' and cancel removal" in {
      val controller      = addAnotherLettingPartOfPropertyController()
      val idxToRemove     = 0
      val requestWithForm = fakeRequest.withFormUrlEncodedBody("genericRemoveConfirmation" -> "no")
      val result          = controller.performRemove(idxToRemove)(requestWithForm)
      status(result) shouldBe BAD_REQUEST
    }

  }

  "Letting part of property form" should {
    "error if addAnotherCateringOperationOrLettingAccommodation is missing" in {
      val formData = baseFormData - errorKey.addAnotherLettingPartOfProperty
      val form     = theForm.bind(formData)

      mustContainError(
        errorKey.addAnotherLettingPartOfProperty,
        "error.addAnotherLetting.required",
        form
      )
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val addAnotherLettingPartOfProperty: String =
        "addAnotherLettingPartOfProperty"
    }

    val baseFormData: Map[String, String] = Map("addAnotherLettingPartOfProperty" -> "yes")
  }
}
