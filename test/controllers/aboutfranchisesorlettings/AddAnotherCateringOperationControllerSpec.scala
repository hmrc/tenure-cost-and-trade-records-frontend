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

package controllers.aboutfranchisesorlettings

import actions.SessionRequest
import form.aboutfranchisesorlettings.AddAnotherCateringOperationOrLettingAccommodationForm.addAnotherCateringOperationForm
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings
import play.api.http.Status
import play.api.test.Helpers._
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec
class AddAnotherCateringOperationControllerSpec extends TestBaseSpec {

  import TestData._

  def addAnotherCateringOperationOrLettingAccommodationController(
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(prefilledAboutFranchiseOrLettings)
  ) =
    new AddAnotherCateringOperationController(
      stubMessagesControllerComponents(),
      aboutFranchisesOrLettingsNavigator,
      addAnotherOperationConcessionFranchise,
      genericRemoveConfirmationView,
      preEnrichedActionRefiner(aboutFranchisesOrLettings = aboutFranchisesOrLettings),
      mockSessionRepo
    )

  "GET /" should {
    "return 200" in {
      val result = addAnotherCateringOperationOrLettingAccommodationController().show(1)(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = addAnotherCateringOperationOrLettingAccommodationController().show(1)(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val result = addAnotherCateringOperationOrLettingAccommodationController().submit(1)(fakeRequest)
        status(result) shouldBe BAD_REQUEST
      }
    }

    "REMOVE /" should {
      "redirect if an empty form is submitted" in {
        val result = addAnotherCateringOperationOrLettingAccommodationController().remove(1)(fakeRequest)
        status(result) shouldBe SEE_OTHER
      }
    }
  }

  "Catering operation form" should {
    "error if addAnotherCateringOperationOrLettingAccommodation is missing" in {
      val formData = baseFormData - errorKey.addAnotherCateringOperationOrLettingAccommodation
      val form     = addAnotherCateringOperationForm.bind(formData)

      mustContainError(
        errorKey.addAnotherCateringOperationOrLettingAccommodation,
        "error.addAnotherSeparateBusinessOrFranchise.required",
        form
      )
    }
  }

  "Remove catering operation" should {
    "render the removal confirmation page on remove" in {
      val controller     = addAnotherCateringOperationOrLettingAccommodationController()
      val idxToRemove    = 0
      val sessionRequest = SessionRequest(sessionAboutFranchiseOrLetting6010YesSession, fakeRequest)
      val result         = controller.remove(idxToRemove)(sessionRequest)
      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
    }

    "handle form submission with 'Yes' and perform removal" in {
      val controller      = addAnotherCateringOperationOrLettingAccommodationController()
      val idxToRemove     = 0
      val requestWithForm = fakeRequest.withFormUrlEncodedBody("genericRemoveConfirmation" -> "yes")
      val sessionRequest  = SessionRequest(sessionAboutFranchiseOrLetting6010YesSession, requestWithForm)
      val result          = controller.performRemove(idxToRemove)(sessionRequest)
      status(result) shouldBe BAD_REQUEST
    }

    "handle form submission with 'No' and cancel removal" in {
      val controller      = addAnotherCateringOperationOrLettingAccommodationController()
      val idxToRemove     = 0
      val requestWithForm = fakeRequest.withFormUrlEncodedBody("genericRemoveConfirmation" -> "no")
      val result          = controller.performRemove(idxToRemove)(requestWithForm)
      status(result) shouldBe BAD_REQUEST
    }

  }
  object TestData {
    val errorKey: Object {
      val addAnotherCateringOperationOrLettingAccommodation: String
    } = new {
      val addAnotherCateringOperationOrLettingAccommodation: String =
        "addAnotherCateringOperation"
    }

    val baseFormData: Map[String, String] = Map("addAnotherCateringOperation" -> "yes")
  }
}
