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

package controllers.aboutYourLeaseOrTenure

import actions.SessionRequest
import form.aboutYourLeaseOrTenure.TradeServicesListForm.addAnotherServiceForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree
import navigation.AboutYourLeaseOrTenureNavigator
import play.api.http.Status
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.test.Helpers.{charset, contentType, status, stubMessagesControllerComponents}
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec

class TradeServicesListControllerSpec extends TestBaseSpec {

  import TestData._

  def tradeServicesListController(
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThree
    )
  ) =
    new TradeServicesListController(
      stubMessagesControllerComponents(),
      app.injector.instanceOf[AboutYourLeaseOrTenureNavigator],
      tradeServicesListView,
      genericRemoveConfirmationView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartThree = aboutLeaseOrAgreementPartThree),
      mockSessionRepo
    )

  "GET /" should {
    "return 200" in {
      val result = tradeServicesListController().show(1)(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = tradeServicesListController().show(1)(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val result = tradeServicesListController().submit(1)(fakeRequest)
        status(result) shouldBe BAD_REQUEST
      }
    }

    "REMOVE /" should {
      "redirect if an empty form is submitted" in {
        val result = tradeServicesListController().remove(1)(fakeRequest)
        status(result) shouldBe SEE_OTHER
      }
    }

  }

  "Remove service" should {
    "render the removal confirmation page on remove" in {
      val controller     = tradeServicesListController()
      val idxToRemove    = 0
      val sessionRequest = SessionRequest(stillConnectedDetails6030NoSession, fakeRequest)
      val result         = controller.remove(idxToRemove)(sessionRequest)
      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
    }

    "handle form submission with 'Yes' and perform removal" in {
      val controller      = tradeServicesListController()
      val idxToRemove     = 0
      val requestWithForm = fakeRequest.withFormUrlEncodedBody("genericRemoveConfirmation" -> "yes")
      val sessionRequest  = SessionRequest(stillConnectedDetails6030NoSession, requestWithForm)
      val result          = controller.performRemove(idxToRemove)(sessionRequest)
      status(result) shouldBe BAD_REQUEST
    }

    "handle form submission with 'No' and cancel removal" in {
      val controller      = tradeServicesListController()
      val idxToRemove     = 0
      val requestWithForm = fakeRequest.withFormUrlEncodedBody("genericRemoveConfirmation" -> "no")
      val result          = controller.performRemove(idxToRemove)(requestWithForm)
      status(result) shouldBe BAD_REQUEST
    }

  }

  "Trade services list form" should {
    "error if answer is missing" in {
      val formData = baseFormData - errorKey.addTradeService
      val form     = addAnotherServiceForm.bind(formData)

      mustContainError(
        errorKey.addTradeService,
        "error.addTradeService.required",
        form
      )
    }
  }

  object TestData {
    val errorKey: Object {
      val addTradeService: String
    } = new {
      val addTradeService: String =
        "addTradeService"
    }

    val baseFormData: Map[String, String] = Map("addTradeService" -> "yes")
  }
}
