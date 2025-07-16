/*
 * Copyright 2025 HM Revenue & Customs
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
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import utils.TestBaseSpec

/**
  * @author Yuriy Tumakha
  */
class IsRentReviewPlannedSpec extends TestBaseSpec:

  val mockAudit: Audit = mock[Audit]

  def isRentReviewPlannedController(
    forType: ForType = FOR6030,
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(
      prefilledAboutLeaseOrAgreementPartTwo
    )
  ) = new IsRentReviewPlannedController(
    isRentReviewPlannedView,
    mockAudit,
    aboutYourLeaseOrTenureNavigator,
    preEnrichedActionRefiner(forType = forType, aboutLeaseOrAgreementPartTwo = aboutLeaseOrAgreementPartTwo),
    mockSessionRepo,
    stubMessagesControllerComponents()
  )

  "IsRentReviewPlannedController GET /" should {
    "return 200 and HTML with isRentReviewPlanned is present in session" in {
      val result = isRentReviewPlannedController().show(fakeRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.MethodToFixCurrentRentController.show().url
      )
    }

    "return 200 and HTML isRentReviewPlanned is none in session" in {
      val controller = isRentReviewPlannedController(aboutLeaseOrAgreementPartTwo = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.MethodToFixCurrentRentController.show().url
      )
    }

  }

  "IsRentReviewPlannedController SUBMIT /" should {
    "return BAD_REQUEST if an empty form is submitted" in {
      val res = isRentReviewPlannedController().submit(
        FakeRequest().withFormUrlEncodedBody()
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted" in {
      val res = isRentReviewPlannedController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody("isRentReviewPlanned" -> "yes")
      )
      status(res) shouldBe SEE_OTHER
      redirectLocation(res) shouldBe Some(
        controllers.aboutYourLeaseOrTenure.routes.IntervalsOfRentReviewController.show().url
      )
    }
  }
