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
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory
import form.aboutthetradinghistory.AddAnotherBunkerFuelCardsDetailsForm.theForm
import play.api.http.Status
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.test.FakeRequest
import play.api.test.Helpers
import play.api.test.Helpers.{POST, contentType, status, stubMessagesControllerComponents}
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec

import scala.language.reflectiveCalls

class AddAnotherBunkerFuelCardsDetailsControllerSpec extends TestBaseSpec {

  import TestData._

  val mockAudit: Audit = mock[Audit]

  def createAddAnotherBunkerFuelCardsDetailsController(
    aboutTheTradingHistory: Option[AboutTheTradingHistory] = Some(prefilledAboutTheTradingHistory)
  ) = new AddAnotherBunkerFuelCardsDetailsController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYourTradingHistoryNavigator,
    addAnotherBunkerFuelCardsDetailsView,
    genericRemoveConfirmationView,
    preEnrichedActionRefiner(aboutTheTradingHistory = aboutTheTradingHistory),
    mockSessionRepo
  )

  "GET /" should {
    "return 200" in {
      val result = createAddAnotherBunkerFuelCardsDetailsController().show(0)(fakeRequest)
      status(result) shouldBe Status.OK
    }
  }

  "return HTML" in {
    val result = createAddAnotherBunkerFuelCardsDetailsController().show(0)(fakeRequest)
    contentType(result)     shouldBe Some("text/html")
    Helpers.charset(result) shouldBe Some("utf-8")
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val result = createAddAnotherBunkerFuelCardsDetailsController().submit(1)(fakeRequest)
      status(result) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted" in {
      val res = createAddAnotherBunkerFuelCardsDetailsController().submit(1)(
        FakeRequest(POST, "/").withFormUrlEncodedBody(
          "addAnotherBunkerFuelCardsDetails" -> "yes"
        )
      )
      status(res) shouldBe SEE_OTHER
    }

  }

  "Add another card details form" should {
    "error if addAnotherBunkerFuelCardsDetails is missing" in {
      val formData = baseFormData - errorKey.addAnotherBunkerFuelCardsDetails
      val form     = theForm.bind(formData)

      mustContainError(
        errorKey.addAnotherBunkerFuelCardsDetails,
        "error.addAnotherBunkerFuelCardsDetails.required",
        form
      )
    }
  }

  "Remove bunker fuel card details" should {
    "render the removal confirmation page on remove" in {
      val controller     =
        createAddAnotherBunkerFuelCardsDetailsController(prefilledAboutTheTradingHistoryForBunkerFuelCardsDetails)
      val idxToRemove    = 0
      val sessionRequest = SessionRequest(aboutYourTradingHistoryWithBunkerFuelCardsDetailsSession, fakeRequest)
      val result         = controller.remove(idxToRemove)(sessionRequest)
      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
    }

    "handle form submission with 'Yes' and perform removal" in {
      val controller      =
        createAddAnotherBunkerFuelCardsDetailsController(prefilledAboutTheTradingHistoryForBunkerFuelCardsDetails)
      val idxToRemove     = 0
      val requestWithForm = fakeRequest.withFormUrlEncodedBody("genericRemoveConfirmation" -> "yes")
      val sessionRequest  = SessionRequest(aboutYourTradingHistoryWithBunkerFuelCardsDetailsSession, requestWithForm)
      val result          = controller.performRemove(idxToRemove)(sessionRequest)
      status(result) shouldBe BAD_REQUEST

    }

    "handle form submission with 'No' and cancel removal" in {
      val controller      =
        createAddAnotherBunkerFuelCardsDetailsController(prefilledAboutTheTradingHistoryForBunkerFuelCardsDetails)
      val idxToRemove     = 0
      val requestWithForm = fakeRequest.withFormUrlEncodedBody("genericRemoveConfirmation" -> "no")
      val result          = controller.performRemove(idxToRemove)(requestWithForm)
      status(result) shouldBe BAD_REQUEST
    }

  }
  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val addAnotherBunkerFuelCardsDetails: String =
        "addAnotherBunkerFuelCardsDetails"
    }

    val baseFormData: Map[String, String] = Map("addAnotherBunkerFuelCardsDetails" -> "yes")
  }
}
