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

package controllers.aboutfranchisesorlettings

import actions.SessionRequest
import connectors.Audit
import form.aboutfranchisesorlettings.AddAnotherLettingOtherPartOfPropertyForm.addAnotherLettingForm
import models.ForType.*
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings
import play.api.http.Status
import play.api.test.Helpers._
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec

import scala.language.reflectiveCalls

class AddAnotherLettingOtherPartOfPropertyController6030Spec extends TestBaseSpec {

  import TestData._

  val mockAudit: Audit = mock[Audit]
  def addAnotherLettingOtherPartOfPropertyController(
    aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = Some(prefilledAboutFranchiseOrLettings6030)
  ) =
    new AddAnotherLettingOtherPartOfPropertyController(
      stubMessagesControllerComponents(),
      mockAudit,
      aboutFranchisesOrLettingsNavigator,
      addAnotherOperationConcessionFranchise,
      genericRemoveConfirmationView,
      preEnrichedActionRefiner(forType = FOR6030, aboutFranchisesOrLettings = aboutFranchisesOrLettings),
      mockSessionRepo
    )

  "GET /" should {
    "return 200" in {
      val result = addAnotherLettingOtherPartOfPropertyController().show(0)(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = addAnotherLettingOtherPartOfPropertyController().show(0)(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val result = addAnotherLettingOtherPartOfPropertyController().submit(1)(fakeRequest)
        status(result) shouldBe BAD_REQUEST
      }
    }

    "REMOVE /" should {
      "redirect if an empty form is submitted" in {
        val result = addAnotherLettingOtherPartOfPropertyController().remove(1)(fakeRequest)
        status(result) shouldBe SEE_OTHER
      }
    }
  }

  "Add another letting accommodation form" should {
    "error if addAnotherCateringOperationOrLettingAccommodation is missing" in {
      val formData = baseFormData - errorKey.addAnotherLettingOtherPartOfProperty
      val form     = addAnotherLettingForm.bind(formData)

      mustContainError(errorKey.addAnotherLettingOtherPartOfProperty, "error.addAnotherLetting.required", form)
    }
  }

  "Remove catering operation" should {
    "render the removal confirmation page on remove" in {
      val controller     = addAnotherLettingOtherPartOfPropertyController()
      val idxToRemove    = 0
      val sessionRequest = SessionRequest(sessionAboutFranchiseOrLetting6030YesSession, fakeRequest)
      val result         = controller.remove(idxToRemove)(sessionRequest)
      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
    }

    "handle form submission with 'Yes' and perform removal" in {
      val controller      = addAnotherLettingOtherPartOfPropertyController()
      val idxToRemove     = 0
      val requestWithForm = fakeRequest.withFormUrlEncodedBody("genericRemoveConfirmation" -> "yes")
      val sessionRequest  = SessionRequest(sessionAboutFranchiseOrLetting6030YesSession, requestWithForm)
      val result          = controller.performRemove(idxToRemove)(sessionRequest)
      status(result) shouldBe BAD_REQUEST

    }

    "handle form submission with 'No' and cancel removal" in {
      val controller      = addAnotherLettingOtherPartOfPropertyController()
      val idxToRemove     = 0
      val requestWithForm = fakeRequest.withFormUrlEncodedBody("genericRemoveConfirmation" -> "no")
      val result          = controller.performRemove(idxToRemove)(requestWithForm)
      status(result) shouldBe BAD_REQUEST
    }

  }
  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val addAnotherLettingOtherPartOfProperty: String =
        "addAnotherLettingOtherPartOfProperty"
    }

    val baseFormData: Map[String, String] = Map("addAnotherLettingOtherPartOfProperty" -> "yes")
  }
}
