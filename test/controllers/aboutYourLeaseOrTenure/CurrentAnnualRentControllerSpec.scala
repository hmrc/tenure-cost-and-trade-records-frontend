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

import models.ForTypes
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class CurrentAnnualRentControllerSpec extends TestBaseSpec {

  def currentAnnualRentController(
    forType: String = ForTypes.for6010,
    aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = Some(prefilledAboutLeaseOrAgreementPartOne)
  ) = new CurrentAnnualRentController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    currentAnnualRentView,
    preEnrichedActionRefiner(forType = forType, aboutLeaseOrAgreementPartOne = aboutLeaseOrAgreementPartOne),
    mockSessionRepo
  )

  "CurrentAnnualRentController GET /" should {
    "return 200 and HTML with Current Annual Rent in the session" in {
      val result = currentAnnualRentController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.PropertyUseLeasebackArrangementController.show().url
      )
    }

    "return 200 and HTML with Connected To Landlord Yes in the session for 6011" in {
      val controller = currentAnnualRentController(ForTypes.for6011)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordDetailsController.show().url
      )
    }

    "return 200 and HTML with Connected To Landlord No in the session for 6011" in {
      val controller = currentAnnualRentController(ForTypes.for6011, Some(prefilledAboutLeaseOrAgreementPartOneNo))
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordController.show().url
      )
    }

    "return 200 and HTML when no Connected To Landlord in the session for 6011" in {
      val controller = currentAnnualRentController(ForTypes.for6011, None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.routes.TaskListController.show().url
      )
    }
  }

  "CurrentAnnualRentController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = currentAnnualRentController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data currentAnnualRent submitted" in {
      val res = currentAnnualRentController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody("currentAnnualRent" -> "1234")
      )
      status(res) shouldBe SEE_OTHER
    }
  }
}
