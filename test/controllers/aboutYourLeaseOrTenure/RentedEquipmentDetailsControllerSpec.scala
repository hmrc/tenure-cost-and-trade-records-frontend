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

import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType, redirectLocation, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

/**
  * @author Yuriy Tumakha
  */
class RentedEquipmentDetailsControllerSpec extends TestBaseSpec {

  def rentedEquipmentDetailsController =
    new RentedEquipmentDetailsController(
      rentedEquipmentDetailsView,
      aboutYourLeaseOrTenureNavigator,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartThree = Some(prefilledAboutLeaseOrAgreementPartThree)),
      mockSessionRepo,
      stubMessagesControllerComponents()
    )

  def rentedEquipmentDetailsControllerNone =
    new RentedEquipmentDetailsController(
      rentedEquipmentDetailsView,
      aboutYourLeaseOrTenureNavigator,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartThree = None),
      mockSessionRepo,
      stubMessagesControllerComponents()
    )

  "RentedEquipmentDetailsController GET /" should {
    "return 200 and HTML with Rented Equipment Details in the session" in {
      val result = rentedEquipmentDetailsController.show(fakeRequest)
      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and HTML when no Rented Equipment Details in the session" in {
      val result = rentedEquipmentDetailsControllerNone.show(fakeRequest)
      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "SUBMIT /" should {
    "return BAD_REQUEST if an empty form is submitted" in {
      val res = rentedEquipmentDetailsController.submit(
        FakeRequest().withFormUrlEncodedBody()
      )
      status(res) shouldBe BAD_REQUEST
    }

    "redirect to the next page if field `rentedEquipmentDetails` is filled" in {
      val res = rentedEquipmentDetailsController.submit(
        FakeRequest("POST", "/").withFormUrlEncodedBody("rentedEquipmentDetails" -> "Equipment details")
      )
      status(res) shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(
        controllers.aboutYourLeaseOrTenure.routes.IncludedInRent6020Controller.show().url
      )
    }

  }

}
