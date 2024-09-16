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
import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentAsString, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

/**
  * @author Yuriy Tumakha
  */
class IsGivenRentFreePeriodControllerSpec extends TestBaseSpec {

  def isGivenRentFreePeriodController(
    aboutLeaseOrAgreementPartFour: Option[AboutLeaseOrAgreementPartFour] = Some(
      prefilledAboutLeaseOrAgreementPartFour
    )
  ) = new IsGivenRentFreePeriodController(
    isGivenRentFreePeriodView,
    aboutYourLeaseOrTenureNavigator,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartFour = aboutLeaseOrAgreementPartFour),
    mockSessionRepo,
    stubMessagesControllerComponents()
  )

  "IsGivenRentFreePeriodController GET /" should {
    "return 200 and HTML with isGivenRentFreePeriod is present in session" in {
      val result = isGivenRentFreePeriodController().show(fakeRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.WorkCarriedOutConditionController.show().url
      )
    }

    "return 200 and HTML isGivenRentFreePeriod is none in session" in {
      val controller = isGivenRentFreePeriodController(aboutLeaseOrAgreementPartFour = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.WorkCarriedOutConditionController.show().url
      )
    }
  }

  "IsGivenRentFreePeriodController SUBMIT /" should {
    "return BAD_REQUEST if an empty form is submitted" in {
      val res = isGivenRentFreePeriodController().submit(
        FakeRequest().withFormUrlEncodedBody()
      )
      status(res) shouldBe BAD_REQUEST
    }
  }

}
