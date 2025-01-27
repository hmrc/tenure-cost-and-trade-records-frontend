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
import models.ForType
import models.ForType.*
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartThree
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import utils.TestBaseSpec

/**
  * @author Yuriy Tumakha
  */
class IsRentUnderReviewControllerSpec extends TestBaseSpec {

  val mockAudit: Audit = mock[Audit]

  def isRentUnderReviewController(
    forType: ForType = FOR6010,
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThree
    )
  ) = new IsRentUnderReviewController(
    isRentUnderReviewView,
    mockAudit,
    aboutYourLeaseOrTenureNavigator,
    preEnrichedActionRefiner(forType = forType, aboutLeaseOrAgreementPartThree = aboutLeaseOrAgreementPartThree),
    mockSessionRepo,
    stubMessagesControllerComponents()
  )

  "IsRentUnderReviewController GET /" should {
    "return 200 and HTML with is rent under review is present in session" in {
      val result = isRentUnderReviewController().show(fakeRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.IntervalsOfRentReviewController.show().url
      )
    }

    "return 200 and HTML is rent under review is none in session" in {
      val controller = isRentUnderReviewController(aboutLeaseOrAgreementPartThree = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.IntervalsOfRentReviewController.show().url
      )
    }

    "return 200 and HTML is rent under review is present in session for 6045" in {
      val controller = isRentUnderReviewController(FOR6045)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.MethodToFixCurrentRentController.show().url
      )
    }
  }

  "IsRentUnderReviewController SUBMIT /" should {
    "return BAD_REQUEST if an empty form is submitted" in {
      val res = isRentUnderReviewController().submit(
        FakeRequest().withFormUrlEncodedBody()
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted" in {
      val res = isRentUnderReviewController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody("isRentUnderReview" -> "yes")
      )
      status(res) shouldBe SEE_OTHER
    }
  }

}
