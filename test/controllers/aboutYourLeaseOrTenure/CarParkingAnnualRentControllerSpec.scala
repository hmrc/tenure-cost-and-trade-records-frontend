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
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, POST, charset, contentAsString, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

/**
  * @author Yuriy Tumakha
  */
class CarParkingAnnualRentControllerSpec extends TestBaseSpec {

  val mockAudit: Audit = mock[Audit]

  def carParkingAnnualRentController(
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThree
    )
  ) =
    new CarParkingAnnualRentController(
      mockAudit,
      carParkingAnnualRentView,
      aboutYourLeaseOrTenureNavigator,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartThree = aboutLeaseOrAgreementPartThree),
      mockSessionRepo,
      stubMessagesControllerComponents()
    )

  "CarParkingAnnualRentController GET /" should {
    "return 200 and HTML with Car Parking Annual Rent in the session" in {
      val result = carParkingAnnualRentController().show(fakeRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentedSeparatelyParkingSpacesController.show().url
      )
    }

    "return 200 and HTML when no Car Parking Annual Rent in the session" in {
      val controller = carParkingAnnualRentController(None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentedSeparatelyParkingSpacesController.show().url
      )
    }

    "return correct backLink when 'from=TL' query param is present" in {
      val result = carParkingAnnualRentController().show()(FakeRequest(GET, "/path?from=TL"))
      contentAsString(result) should include(controllers.routes.TaskListController.show().url)
    }
  }

  "CarParkingAnnualRentController SUBMIT /" should {
    "return BAD_REQUEST if an empty form is submitted" in {
      val res = carParkingAnnualRentController().submit(
        FakeRequest().withFormUrlEncodedBody()
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data annualRent and fixedRentFrom submitted" in {
      val res = carParkingAnnualRentController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody(
          "annualRent"          -> "2200",
          "fixedRentFrom.day"   -> "27",
          "fixedRentFrom.month" -> "09",
          "fixedRentFrom.year"  -> "2017"
        )
      )
      status(res) shouldBe SEE_OTHER
    }
  }
}
