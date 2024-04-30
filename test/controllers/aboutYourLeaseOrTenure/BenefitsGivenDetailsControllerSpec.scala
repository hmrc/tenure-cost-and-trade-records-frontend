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
import play.api.http.Status
import play.api.http.Status.SEE_OTHER
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

class BenefitsGivenDetailsControllerSpec extends TestBaseSpec {

  def benefitsGivenDetailsController(
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThree
    )
  ) =
    new BenefitsGivenDetailsController(
      stubMessagesControllerComponents(),
      benefitsGivenDetailsView,
      aboutYourLeaseOrTenureNavigator,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartThree = aboutLeaseOrAgreementPartThree),
      mockSessionRepo
    )

  def benefitsGivenDetailsControllerNone =
    new BenefitsGivenDetailsController(
      stubMessagesControllerComponents(),
      benefitsGivenDetailsView,
      aboutYourLeaseOrTenureNavigator,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartThree = None),
      mockSessionRepo
    )

  "BenefitsGivenDetailsController GET /" should {
    "return 200 and HTML with Benefits Given Details in the session" in {
      val result = benefitsGivenDetailsController().show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return HTML and HTML when no Benefits Given Details in the session" in {
      val result = benefitsGivenDetailsControllerNone.show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }
  "SUBMIT /"                             should {
    "accept an empty form when submitted" in {
      val res = benefitsGivenDetailsController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe SEE_OTHER
    }
  }
}
