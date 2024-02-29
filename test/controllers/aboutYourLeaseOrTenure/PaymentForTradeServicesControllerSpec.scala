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
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree
import navigation.AboutYourLeaseOrTenureNavigator
import play.api.http.Status
import play.api.http.Status.BAD_REQUEST
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

class PaymentForTradeServicesControllerSpec extends TestBaseSpec {

  import TestData._
  import utils.FormBindingTestAssertions._

  val mockAboutYourLeaseOrTenureNavigator = mock[AboutYourLeaseOrTenureNavigator]

  def paymentForTradeServicesController(
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThree
    )
  ) =
    new PaymentForTradeServicesController(
      stubMessagesControllerComponents(),
      mockAboutYourLeaseOrTenureNavigator,
      paymentForTradeServicesView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartThree = aboutLeaseOrAgreementPartThree),
      mockSessionRepo
    )

  "GET /"    should {
    "return 200" in {
      val result = paymentForTradeServicesController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = paymentForTradeServicesController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }
  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {

      val res = paymentForTradeServicesController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
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
    val errorKey: Object {
      val paymentForTradeServices: String
    } = new {
      val paymentForTradeServices: String = "paymentForTradeServices"
    }

    val baseFormData: Map[String, String] = Map("paymentForTradeServices" -> "yes")
  }
}
