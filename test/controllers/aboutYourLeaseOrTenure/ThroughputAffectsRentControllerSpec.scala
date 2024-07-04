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
import play.api.test.Helpers.{charset, contentAsString, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

/**
  * @author Yuriy Tumakha
  */
class ThroughputAffectsRentControllerSpec extends TestBaseSpec {

  def throughputAffectsRentController(
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThree
    )
  ) = new ThroughputAffectsRentController(
    throughputAffectsRentView,
    aboutYourLeaseOrTenureNavigator,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartThree = aboutLeaseOrAgreementPartThree),
    mockSessionRepo,
    stubMessagesControllerComponents()
  )

  "ThroughputAffectsRentController GET /" should {
    "return 200 and HTML with Through Put Affect Rent in the session" in {
      val result = throughputAffectsRentController().show(fakeRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.CurrentAnnualRentController.show().url
      )
    }

    "return 200 and HTML Through Put Affect Rent with none in the session" in {
      val controller = throughputAffectsRentController(aboutLeaseOrAgreementPartThree = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.CurrentAnnualRentController.show().url
      )
    }
  }

  "ThroughputAffectsRentController SUBMIT /" should {
    "return BAD_REQUEST if an empty form is submitted" in {
      val res = throughputAffectsRentController().submit(
        FakeRequest().withFormUrlEncodedBody()
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

}
