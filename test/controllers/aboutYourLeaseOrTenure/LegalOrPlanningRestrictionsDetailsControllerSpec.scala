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

import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class LegalOrPlanningRestrictionsDetailsControllerSpec extends TestBaseSpec {

  def legalOrPlanningRestrictionsDetailsController(
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(prefilledAboutLeaseOrAgreementPartTwo)
  ) = new LegalOrPlanningRestrictionsDetailsController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    legalOrPlanningRestrictionsDetailsView,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartTwo = aboutLeaseOrAgreementPartTwo),
    mockSessionRepo
  )

  "LegalOrPlanningRestrictionsDetailsController GET" should {
    "return 200 and HTML with legal or planing restrictions details in the session" in {
      val result = legalOrPlanningRestrictionsDetailsController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.LegalOrPlanningRestrictionsController.show().url
      )
    }

    "return 200 and HTML legal or planing restrictions details with none in the session" in {
      val controller = legalOrPlanningRestrictionsDetailsController(aboutLeaseOrAgreementPartTwo = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.LegalOrPlanningRestrictionsController.show().url
      )
    }
  }

  "LegalOrPlanningRestrictionsDetailsController SUBMIT" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = legalOrPlanningRestrictionsDetailsController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
}
