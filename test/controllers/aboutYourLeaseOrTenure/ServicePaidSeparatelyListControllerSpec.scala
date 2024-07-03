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
import form.aboutYourLeaseOrTenure.ServicePaidSeparatelyListForm.addServicePaidSeparatelyForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree
import navigation.AboutYourLeaseOrTenureNavigator
import play.api.http.Status._
import play.api.test.Helpers.{charset, contentAsString, contentType, status, stubMessagesControllerComponents}
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec

import scala.language.reflectiveCalls

class ServicePaidSeparatelyListControllerSpec extends TestBaseSpec {

  import TestData._

  def servicePaidSeparatelyListController(
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThree
    )
  ) =
    new ServicePaidSeparatelyListController(
      stubMessagesControllerComponents(),
      app.injector.instanceOf[AboutYourLeaseOrTenureNavigator],
      servicePaidSeparatelyListView,
      genericRemoveConfirmationView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartThree = aboutLeaseOrAgreementPartThree),
      mockSessionRepo
    )

  "ServicePaidSeparatelyListController" should {
    "return 200 and HTML with Services Paid Separately List in the session 0" in {
      val result = servicePaidSeparatelyListController().show(0)(fakeRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.ServicePaidSeparatelyChargeController.show(0).url
      )
    }

    "return 200 and HTML with Services Paid Separately List with none in the session 0" in {
      val controller = servicePaidSeparatelyListController(aboutLeaseOrAgreementPartThree = None)
      val result     = controller.show(0)(fakeRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.ServicePaidSeparatelyChargeController.show(0).url
      )
    }

    "SUBMIT /" should {
      "throw a BAD_REQUEST if an empty form is submitted" in {
        val result = servicePaidSeparatelyListController().submit(1)(fakeRequest)
        status(result) shouldBe BAD_REQUEST
      }
    }

    "REMOVE /" should {
      "redirect if an empty form is submitted" in {
        val result = servicePaidSeparatelyListController().remove(1)(fakeRequest)
        status(result) shouldBe SEE_OTHER
      }
    }

  }

  "Remove service" should {
    "render the removal confirmation page on remove" in {
      val controller     = servicePaidSeparatelyListController()
      val idxToRemove    = 0
      val sessionRequest = SessionRequest(stillConnectedDetails6030NoSession, fakeRequest)
      val result         = controller.remove(idxToRemove)(sessionRequest)
      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
    }

    "handle form submission with 'Yes' and perform removal" in {
      val controller      = servicePaidSeparatelyListController()
      val idxToRemove     = 0
      val requestWithForm = fakeRequest.withFormUrlEncodedBody("genericRemoveConfirmation" -> "yes")
      val sessionRequest  = SessionRequest(stillConnectedDetails6030NoSession, requestWithForm)
      val result          = controller.performRemove(idxToRemove)(sessionRequest)
      status(result) shouldBe BAD_REQUEST
    }

    "handle form submission with 'No' and cancel removal" in {
      val controller      = servicePaidSeparatelyListController()
      val idxToRemove     = 0
      val requestWithForm = fakeRequest.withFormUrlEncodedBody("genericRemoveConfirmation" -> "no")
      val result          = controller.performRemove(idxToRemove)(requestWithForm)
      status(result) shouldBe BAD_REQUEST
    }

  }

  "Trade services list form" should {
    "error if answer is missing" in {
      val formData = baseFormData - errorKey.addService
      val form     = addServicePaidSeparatelyForm.bind(formData)

      mustContainError(
        errorKey.addService,
        "error.servicePaidSeparatelyList.required",
        form
      )
    }
  }

  object TestData {
    val errorKey: Object {
      val addService: String
    } = new {
      val addService: String =
        "servicePaidSeparatelyList"
    }

    val baseFormData: Map[String, String] = Map("servicePaidSeparatelyList" -> "yes")
  }
}
