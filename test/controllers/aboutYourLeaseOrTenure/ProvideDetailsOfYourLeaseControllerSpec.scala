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

package controllers.aboutYourLeaseOrTenure

import form.aboutYourLeaseOrTenure.ProvideDetailsOfYourLeaseForm.provideDetailsOfYourLeaseForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec
import scala.language.reflectiveCalls

class ProvideDetailsOfYourLeaseControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}

  def provideDetailsOfYourLeaseController(
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThree
    )
  ) = new ProvideDetailsOfYourLeaseController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    provideDetailsOfYourLeaseView,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartThree = aboutLeaseOrAgreementPartThree),
    mockSessionRepo
  )

  "ConnectedToLandlordDetailsController GET /" should {
    "return 200 and HTML with Connected To Landlord Details in the session" in {
      val result = provideDetailsOfYourLeaseController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.ProvideDetailsOfYourLeaseController.show().url
      )
    }

    "return 200 and HTML Connected To Landlord Details with none in the session" in {
      val controller = provideDetailsOfYourLeaseController(aboutLeaseOrAgreementPartThree = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.ProvideDetailsOfYourLeaseController.show().url
      )
    }
  }

  "ConnectedToLandlordDetailsController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {

      val res = provideDetailsOfYourLeaseController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

  "Connected to landlord details form" should {
    "error if choice is missing" in {
      val formData = baseFormData - errorKey.provideDetailsOfYourLease
      val form     = provideDetailsOfYourLeaseForm.bind(formData)

      mustContainError(
        errorKey.provideDetailsOfYourLease,
        "error.provideDetailsOfYourLease.required",
        form
      )
    }
  }

  object TestData {
    val errorKey: Object {
      val provideDetailsOfYourLease: String
    } = new {
      val provideDetailsOfYourLease: String = "provideDetailsOfYourLease"
    }

    val baseFormData: Map[String, String] = Map(
      "provideDetailsOfYourLease" -> "Test content"
    )
  }
}
