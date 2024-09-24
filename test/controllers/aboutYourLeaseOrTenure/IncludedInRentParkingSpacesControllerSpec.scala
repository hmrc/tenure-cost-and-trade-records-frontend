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

import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

/**
  * @author Yuriy Tumakha
  */
class IncludedInRentParkingSpacesControllerSpec extends TestBaseSpec {

  def includedInRentParkingSpacesController(
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThree
    )
  ) = new IncludedInRentParkingSpacesController(
    includedInRentParkingSpacesView,
    aboutYourLeaseOrTenureNavigator,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartThree = aboutLeaseOrAgreementPartThree),
    mockSessionRepo,
    stubMessagesControllerComponents()
  )

  "IncludedInRentParkingSpacesController GET /" should {
    "return 200 and HTML with Included In Rent Parking Spaces in the session" in {
      val result = includedInRentParkingSpacesController().show(fakeRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.DoesRentIncludeParkingController.show().url
      )
    }

    "return 200 and HTML with no Included In Rent Parking Spaces in the session" in {
      val controller = includedInRentParkingSpacesController(aboutLeaseOrAgreementPartThree = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.DoesRentIncludeParkingController.show().url
      )
    }

    "return correct backLink when 'from=TL' query param is present" in {
      val result = includedInRentParkingSpacesController().show()(FakeRequest(GET, "/path?from=TL"))
      contentAsString(result) should include(controllers.routes.TaskListController.show().url)
    }
  }

  "IncludedInRentParkingSpacesController SUBMIT /" should {
    "return BAD_REQUEST if an empty form is submitted" in {
      val res = includedInRentParkingSpacesController().submit(
        FakeRequest().withFormUrlEncodedBody()
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data includedInRentParkingSpaces submitted" in {
      val res = includedInRentParkingSpacesController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody(
          "openSpaces"    -> "30",
          "coveredSpaces" -> "20",
          "garages"       -> "10"
        )
      )
      status(res) shouldBe SEE_OTHER
    }
  }

}
