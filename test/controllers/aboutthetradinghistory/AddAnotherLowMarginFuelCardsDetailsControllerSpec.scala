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

package controllers.aboutthetradinghistory

import actions.SessionRequest
import connectors.Audit
import form.aboutthetradinghistory.AddAnotherLowMarginFuelCardsDetailsForm.theForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory
import play.api.http.Status
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.test.FakeRequest
import play.api.test.Helpers
import play.api.test.Helpers.{POST, contentType, status, stubMessagesControllerComponents}
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec

import scala.language.reflectiveCalls

class AddAnotherLowMarginFuelCardsDetailsControllerSpec extends TestBaseSpec {

  import TestData._

  val mockAudit: Audit = mock[Audit]

  def createAddAnotherLowMarginFuelCardsDetailsController(
    aboutTheTradingHistory: Option[AboutTheTradingHistory] = Some(prefilledAboutTheTradingHistory)
  ) = new AddAnotherLowMarginFuelCardsDetailsController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYourTradingHistoryNavigator,
    addAnotherLowMarginFuelCardsDetailsView,
    genericRemoveConfirmationView,
    preEnrichedActionRefiner(aboutTheTradingHistory = aboutTheTradingHistory),
    mockSessionRepo
  )

  "AddAnotherLowMarginFuelCardsDetailsController GET /" should {
    "return 200 and HTML with Can Rent Be Reduced On Review in the session  return 200" in {
      val result = createAddAnotherLowMarginFuelCardsDetailsController().show(0)(fakeRequest)
      status(result)          shouldBe Status.OK
      contentType(result)     shouldBe Some("text/html")
      Helpers.charset(result) shouldBe Some("utf-8")
    }
  }

  "AddAnotherLowMarginFuelCardsDetailsController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val result = createAddAnotherLowMarginFuelCardsDetailsController().submit(1)(fakeRequest)
      status(result) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted" in {
      val res = createAddAnotherLowMarginFuelCardsDetailsController().submit(1)(
        FakeRequest(POST, "/").withFormUrlEncodedBody(
          "addAnotherLowMarginFuelCardsDetails" -> "yes"
        )
      )
      status(res) shouldBe SEE_OTHER
    }
  }

  "Add another card details form" should {
    "error if addAnotherLowMarginFuelCardsDetails is missing" in {
      val formData = baseFormData - errorKey.addAnotherLowMarginFuelCardsDetails
      val form     = theForm.bind(formData)

      mustContainError(
        errorKey.addAnotherLowMarginFuelCardsDetails,
        "error.addAnotherLowMarginFuelCardsDetails.required",
        form
      )
    }
  }

  "Remove LowMargin fuel card details" should {
    "render the removal confirmation page on remove" in {
      val controller     =
        createAddAnotherLowMarginFuelCardsDetailsController(prefilledAboutTheTradingHistoryForLowMarginFuelCardsDetails)
      val idxToRemove    = 0
      val sessionRequest = SessionRequest(aboutYourTradingHistoryWithLowMarginFuelCardsDetailsSession, fakeRequest)
      val result         = controller.remove(idxToRemove)(sessionRequest)
      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
    }

    "handle form submission with 'Yes' and perform removal" in {
      val controller      =
        createAddAnotherLowMarginFuelCardsDetailsController(prefilledAboutTheTradingHistoryForLowMarginFuelCardsDetails)
      val idxToRemove     = 0
      val requestWithForm = fakeRequest.withFormUrlEncodedBody("genericRemoveConfirmation" -> "yes")
      val sessionRequest  = SessionRequest(aboutYourTradingHistoryWithLowMarginFuelCardsDetailsSession, requestWithForm)
      val result          = controller.performRemove(idxToRemove)(sessionRequest)
      status(result) shouldBe BAD_REQUEST

    }

    "handle form submission with 'No' and cancel removal" in {
      val controller      =
        createAddAnotherLowMarginFuelCardsDetailsController(prefilledAboutTheTradingHistoryForLowMarginFuelCardsDetails)
      val idxToRemove     = 0
      val requestWithForm = fakeRequest.withFormUrlEncodedBody("genericRemoveConfirmation" -> "no")
      val result          = controller.performRemove(idxToRemove)(requestWithForm)
      status(result) shouldBe BAD_REQUEST
    }

  }
  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val addAnotherLowMarginFuelCardsDetails: String =
        "addAnotherLowMarginFuelCardsDetails"
    }

    val baseFormData: Map[String, String] = Map("addAnotherLowMarginFuelCardsDetails" -> "yes")
  }
}
