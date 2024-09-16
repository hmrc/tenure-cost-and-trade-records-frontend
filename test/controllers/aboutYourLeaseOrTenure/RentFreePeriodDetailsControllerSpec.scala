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

import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartFour
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentAsString, contentType, redirectLocation, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

/**
  * @author Yuriy Tumakha
  */
class RentFreePeriodDetailsControllerSpec extends TestBaseSpec {

  def rentFreePeriodDetailsController(
    aboutLeaseOrAgreementPartFour: Option[AboutLeaseOrAgreementPartFour] = Some(
      prefilledAboutLeaseOrAgreementPartFour
    )
  ) = new RentFreePeriodDetailsController(
    rentFreePeriodDetailsView,
    aboutYourLeaseOrTenureNavigator,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartFour = aboutLeaseOrAgreementPartFour),
    mockSessionRepo,
    stubMessagesControllerComponents()
  )

  "RentFreePeriodDetailsController GET /" should {
    "return 200 and HTML with rentFreePeriodDetails in the session" in {
      val result = rentFreePeriodDetailsController().show(fakeRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.IsGivenRentFreePeriodController.show().url
      )
    }

    "return 200 and HTML when no rentFreePeriodDetails in the session" in {
      val controller = rentFreePeriodDetailsController(aboutLeaseOrAgreementPartFour = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.IsGivenRentFreePeriodController.show().url
      )
    }
  }

  "RentFreePeriodDetailsController SUBMIT /" should {
    "return BAD_REQUEST if an empty form is submitted" in {
      val res = rentFreePeriodDetailsController().submit(
        FakeRequest().withFormUrlEncodedBody()
      )
      status(res) shouldBe BAD_REQUEST
    }

    "redirect to the next page if field `rentFreePeriodDetails` is filled" in {
      val res = rentFreePeriodDetailsController().submit(
        FakeRequest("POST", "/").withFormUrlEncodedBody("rentFreePeriodDetails" -> "Rent-free period details")
      )
      status(res) shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(
        controllers.aboutYourLeaseOrTenure.routes.PayACapitalSumController.show().url
      )
    }

  }

}
