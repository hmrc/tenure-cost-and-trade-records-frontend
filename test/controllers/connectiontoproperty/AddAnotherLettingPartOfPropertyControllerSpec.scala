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

import form.connectiontoproperty.AddAnotherLettingPartOfPropertyForm.addAnotherLettingForm
import models.submissions.connectiontoproperty.StillConnectedDetails
import play.api.http.Status
import play.api.test.Helpers._
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec
import actions.SessionRequest
import scala.language.reflectiveCalls

class AddAnotherLettingPartOfPropertyControllerSpec extends TestBaseSpec {

  import TestData._

  def addAnotherLettingPartOfPropertyController(
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledStillConnectedDetailsYesToAll)
  ) =
    new AddAnotherLettingPartOfPropertyController(
      stubMessagesControllerComponents(),
      connectedToPropertyNavigator,
      addAnotherLettingPartOfPropertyView,
      genericRemoveConfirmationView,
      preEnrichedActionRefiner(stillConnectedDetails = stillConnectedDetails),
      mockSessionRepo
    )

  "GET /" should {
    "return 200" in {
      val result = addAnotherLettingPartOfPropertyController().show(0)(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = addAnotherLettingPartOfPropertyController().show(0)(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val result = addAnotherLettingPartOfPropertyController().submit(0)(fakeRequest)
        status(result) shouldBe BAD_REQUEST
      }
    }

    "REMOVE /" should {
      "redirect if an empty form is submitted" in {
        val result = addAnotherLettingPartOfPropertyController().remove(1)(fakeRequest)
        status(result) shouldBe SEE_OTHER
      }
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
      val form     = addAnotherLettingForm.bind(formData)

      mustContainError(
        errorKey.addAnotherLettingPartOfProperty,
        "error.addAnotherLetting.required",
        form
      )
    }
  }

  object TestData {
    val errorKey: Object {
      val addAnotherLettingPartOfProperty: String
    } = new {
      val addAnotherLettingPartOfProperty: String =
        "addAnotherLettingPartOfProperty"
    }

    val baseFormData: Map[String, String] = Map("addAnotherLettingPartOfProperty" -> "yes")
  }
}
