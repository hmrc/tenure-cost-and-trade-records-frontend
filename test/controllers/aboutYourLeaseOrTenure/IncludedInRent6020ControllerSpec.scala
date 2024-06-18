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

import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentAsString, contentType, redirectLocation, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

/**
  * @author Yuriy Tumakha
  */
class IncludedInRent6020ControllerSpec extends TestBaseSpec {

  def includedInRent6020Controller(
    aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = Some(prefilledAboutLeaseOrAgreementPartOne)
  ) = new IncludedInRent6020Controller(
    includedInRent6020View,
    aboutYourLeaseOrTenureNavigator,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartOne = aboutLeaseOrAgreementPartOne),
    mockSessionRepo,
    stubMessagesControllerComponents()
  )

  "IncludedInRent6020Controller GET /" should {
    "return 200 and HTML with Rented Equipment Details in the session" in {
      val result = includedInRent6020Controller().show(fakeRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentedEquipmentDetailsController.show().url
      )
    }

    "return 200 and HTML with no Rented Equipment Details in the session" in {
      val controller = includedInRent6020Controller(aboutLeaseOrAgreementPartOne = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsController.show().url
      )
    }
  }

  "IncludedInRent6020Controller SUBMIT /" should {
    "redirect to the next page if an empty form is submitted" in {
      val res = includedInRent6020Controller().submit(
        FakeRequest("POST", "/").withFormUrlEncodedBody()
      )
      status(res)           shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(
        controllers.aboutYourLeaseOrTenure.routes.RentOpenMarketValueController.show().url
      )
    }

    "return BAD_REQUEST if any rentPayable items selected but description is empty" in {
      val res = includedInRent6020Controller().submit(
        FakeRequest("POST", "/").withFormUrlEncodedBody("rentPayable[0]" -> "landOnly")
      )
      status(res) shouldBe BAD_REQUEST
      redirectLocation(res) shouldBe None
    }
  }

}
