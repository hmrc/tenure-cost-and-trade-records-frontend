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

import connectors.Audit
import form.aboutYourLeaseOrTenure.ConnectedToLandlordForm.connectedToLandlordForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

import scala.language.reflectiveCalls

class ConnectedToLandlordControllerSpec extends TestBaseSpec {

  import TestData._
  import utils.FormBindingTestAssertions._

  val mockAudit: Audit = mock[Audit]

  def connectedToLandlordController(
    aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = Some(prefilledAboutLeaseOrAgreementPartOne)
  ) = new ConnectedToLandlordController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYourLeaseOrTenureNavigator,
    connectedToLandlordView,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartOne = aboutLeaseOrAgreementPartOne),
    mockSessionRepo
  )

  "ConnectedToLandlordController GET /" should {
    "return 200 and HTML with Connected To Landlord in the session" in {
      val result = connectedToLandlordController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.AboutYourLandlordController.show().url
      )
    }

    "return 200 and HTML when no Connected To Landlord in the session" in {
      val controller = connectedToLandlordController(None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.AboutYourLandlordController.show().url
      )
    }
  }

  "ConnectedToLandlordController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {

      val res = connectedToLandlordController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data connectedToLandlord submitted" in {
      val res = connectedToLandlordController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody("connectedToLandlord" -> "yes")
      )
      status(res) shouldBe SEE_OTHER
    }
  }

  "Connected to landlord form" should {
    "error if connected to landlord answer is missing" in {
      val formData = baseFormData - errorKey.connectedToLandlord
      val form     = connectedToLandlordForm.bind(formData)

      mustContainError(errorKey.connectedToLandlord, "error.connectedToLandlord.missing", form)
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val connectedToLandlord: String = "connectedToLandlord"
    }

    val baseFormData: Map[String, String] = Map("connectedToLandlord" -> "yes")
  }
}
