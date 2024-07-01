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
import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, charset, contentAsString, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

/**
  * @author Yuriy Tumakha
  */
class RentedSeparatelyParkingSpacesControllerSpec extends TestBaseSpec {

  def rentedSeparatelyParkingSpacesController(
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThree
    )
  ) = new RentedSeparatelyParkingSpacesController(
    rentedSeparatelyParkingSpacesView,
    aboutYourLeaseOrTenureNavigator,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartThree = Some(prefilledAboutLeaseOrAgreementPartThree)),
    mockSessionRepo,
    stubMessagesControllerComponents()
  )

  "RentedSeparatelyParkingSpacesController GET /" should {
    "return 200 and HTML with Rented Separate parking spaces in the session" in {
      val result = rentedSeparatelyParkingSpacesController().show(fakeRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.IsParkingRentPaidSeparatelyController.show().url
      )
    }

    "return 200 and HTML Rented Equipment Details with none in the session" in {
      val controller = rentedSeparatelyParkingSpacesController(aboutLeaseOrAgreementPartThree = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.IsParkingRentPaidSeparatelyController.show().url
      )
    }

    "return correct backLink when 'from=TL' query param is present" in {
      val result = rentedSeparatelyParkingSpacesController().show()(FakeRequest(GET, "/path?from=TL"))
      contentAsString(result) should include(controllers.routes.TaskListController.show().url)
    }
  }

  "RentedSeparatelyParkingSpacesController SUBMIT /" should {
    "return BAD_REQUEST if an empty form is submitted" in {
      val res = rentedSeparatelyParkingSpacesController().submit(
        FakeRequest().withFormUrlEncodedBody()
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

}
