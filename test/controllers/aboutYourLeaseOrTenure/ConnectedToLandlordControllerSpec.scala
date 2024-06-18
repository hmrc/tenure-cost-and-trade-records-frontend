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

import form.aboutYourLeaseOrTenure.ConnectedToLandlordForm.connectedToLandlordForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class ConnectedToLandlordControllerSpec extends TestBaseSpec {

  import TestData._
  import utils.FormBindingTestAssertions._

  def connectedToLandlordController(
    aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = Some(prefilledAboutLeaseOrAgreementPartOne)
  ) = new ConnectedToLandlordController(
    stubMessagesControllerComponents(),
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
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
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
    val errorKey: Object {
      val connectedToLandlord: String
    } = new {
      val connectedToLandlord: String = "connectedToLandlord"
    }

    val baseFormData: Map[String, String] = Map("connectedToLandlord" -> "yes")
  }
}
