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
class DoesRentIncludeParkingControllerSpec extends TestBaseSpec {

  def doesRentIncludeParkingController(
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThree
    )
  ) = new DoesRentIncludeParkingController(
    doesRentIncludeParkingView,
    aboutYourLeaseOrTenureNavigator,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartThree = aboutLeaseOrAgreementPartThree),
    mockSessionRepo,
    stubMessagesControllerComponents()
  )

  "DoesRentIncludeParkingController GET /" should {
    "return 200 and HTML with Services Paid entry in the session" in {
      val result = doesRentIncludeParkingController().show(fakeRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.ServicePaidSeparatelyListController.show(0).url
      )
    }

    "return 200 and HTML with no Services Paid empty in the session" in {
      val controller = doesRentIncludeParkingController(aboutLeaseOrAgreementPartThree = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.ServicePaidSeparatelyListController.show(0).url
      )
    }

    "return correct backLink when 'from=TL' query param is present" in {
      val result = doesRentIncludeParkingController().show()(FakeRequest(GET, "/path?from=TL"))
      contentAsString(result) should include(
        controllers.routes.TaskListController.show().url + "#does-rent-include-parking"
      )
    }
  }

  "DoesRentIncludeParkingController SUBMIT /" should {
    "return BAD_REQUEST if an empty form is submitted" in {
      val res = doesRentIncludeParkingController().submit(FakeRequest().withFormUrlEncodedBody())
      status(res) shouldBe BAD_REQUEST
    }
  }

}
