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
import models.submissions.aboutYourLeaseOrTenure.{AboutLeaseOrAgreementPartOne, AboutLeaseOrAgreementPartThree}
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import utils.TestBaseSpec

class RentIncludeFixtureAndFittingsControllerSpec extends TestBaseSpec {

  def rentIncludeFixtureAndFittingsController(
    forType: String = ForTypes.for6010,
    aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = Some(prefilledAboutLeaseOrAgreementPartOne),
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThree
    )
  ) = new RentIncludeFixtureAndFittingsController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    rentIncludeFixtureAndFittingsView,
    preEnrichedActionRefiner(
      forType = forType,
      aboutLeaseOrAgreementPartOne = aboutLeaseOrAgreementPartOne,
      aboutLeaseOrAgreementPartThree = aboutLeaseOrAgreementPartThree
    ),
    mockSessionRepo
  )

  "RentIncludeFixtureAndFittings controller" should {
    "return 200 and HTML with Rent Includes trade services with yes in the session" in {
      val result = rentIncludeFixtureAndFittingsController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentIncludeTradeServicesDetailsController.show().url
      )
    }

    "return 200 and HTML with Rent Includes trade services with no in the session" in {
      val controller = rentIncludeFixtureAndFittingsController(
        aboutLeaseOrAgreementPartOne = Some(prefilledAboutLeaseOrAgreementPartOneNo)
      )
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentIncludeTradeServicesController.show().url
      )
    }

    "return 200 and HTML Rent Includes trade services with none in the session" in {
      val controller = rentIncludeFixtureAndFittingsController(aboutLeaseOrAgreementPartOne = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.routes.TaskListController.show().url
      )
    }

    "return 200 and HTML with Rent Paid Separately with yes in the session for 6020" in {
      val result = rentIncludeFixtureAndFittingsController(forType = ForTypes.for6020).show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.CarParkingAnnualRentController.show().url
      )
    }

    "return 200 and HTML Rent Paid Separately with one in the session for 6020" in {
      val result = rentIncludeFixtureAndFittingsController(
        forType = ForTypes.for6020,
        aboutLeaseOrAgreementPartThree = None
      ).show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.IsParkingRentPaidSeparatelyController.show().url
      )
    }

    "return 200 and HTML with Payment For Trade Services with yes in the session for 6030" in {
      val result = rentIncludeFixtureAndFittingsController(forType = ForTypes.for6030).show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.ServicePaidSeparatelyListController.show(1).url
      )
    }

    "return 200 and HTML with Payment For Trade Services with no in the session for 6030" in {
      val result = rentIncludeFixtureAndFittingsController(
        forType = ForTypes.for6030,
        aboutLeaseOrAgreementPartThree = Some(prefilledAboutLeaseOrAgreementPartThreeNo)
      ).show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.PaymentForTradeServicesController.show().url
      )
    }

    "return 200 and HTML Payment For Trade Services with none in the session for 6030" in {
      val result = rentIncludeFixtureAndFittingsController(
        forType = ForTypes.for6030,
        aboutLeaseOrAgreementPartThree = None
      ).show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.routes.TaskListController.show().url
      )
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = rentIncludeFixtureAndFittingsController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted" in {
      val res = rentIncludeFixtureAndFittingsController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody("rentIncludeFixturesAndFittings" -> "yes")
      )
      status(res) shouldBe SEE_OTHER
    }
  }
}
