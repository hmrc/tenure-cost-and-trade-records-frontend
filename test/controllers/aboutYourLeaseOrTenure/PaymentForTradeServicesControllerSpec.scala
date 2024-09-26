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

import form.aboutYourLeaseOrTenure.PaymentForTradeServicesForm.paymentForTradeServicesForm
import models.ForTypes
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree
import play.api.http.Status
import play.api.http.Status.BAD_REQUEST
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec
import scala.language.reflectiveCalls

class PaymentForTradeServicesControllerSpec extends TestBaseSpec {

  import TestData._
  import utils.FormBindingTestAssertions._

  def paymentForTradeServicesController(
    forType: String = ForTypes.for6010,
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThree
    )
  ) =
    new PaymentForTradeServicesController(
      stubMessagesControllerComponents(),
      aboutYourLeaseOrTenureNavigator,
      paymentForTradeServicesView,
      preEnrichedActionRefiner(forType = forType, aboutLeaseOrAgreementPartThree = aboutLeaseOrAgreementPartThree),
      mockSessionRepo
    )

  "PaymentForTradeServicesController GET /" should {
    "return 200 and HTML with trade services in the session" in {
      val result = paymentForTradeServicesController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.TradeServicesListController.show(0).url
      )
    }

    "return 200 and HTML trade services with none in the session" in {
      val controller = paymentForTradeServicesController(aboutLeaseOrAgreementPartThree = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentIncludeTradeServicesController.show().url
      )
    }

    "return 200 and HTML with trade services in the session for 6030" in {
      val controller = paymentForTradeServicesController(forType = ForTypes.for6030)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.TradeServicesListController.show(0).url
      )
    }

    "return 200 and HTML trade services with none in the session for 6030" in {
      val controller = paymentForTradeServicesController(ForTypes.for6030, None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentIncludeTradeServicesController.show().url
      )
    }

    "return correct backLink when 'from=TL' query param is present" in {
      val result = paymentForTradeServicesController().show()(FakeRequest(GET, "/path?from=TL"))
      contentAsString(result) should include(
        controllers.routes.TaskListController.show().url + "#payment-for-trade-services"
      )
    }
  }

  "PaymentForTradeServicesController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = paymentForTradeServicesController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted" in {
      val res = paymentForTradeServicesController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody("paymentForTradeServices" -> "yes")
      )
      status(res) shouldBe SEE_OTHER
    }
  }

  "Payment for trade services" should {
    "error if Payment for trade services answer is missing" in {
      val formData = baseFormData - errorKey.paymentForTradeServices
      val form     = paymentForTradeServicesForm.bind(formData)

      mustContainError(errorKey.paymentForTradeServices, "error.paymentForTradeServices.missing", form)
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val paymentForTradeServices: String = "paymentForTradeServices"
    }

    val baseFormData: Map[String, String] = Map("paymentForTradeServices" -> "yes")
  }
}
