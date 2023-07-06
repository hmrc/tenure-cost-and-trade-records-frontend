/*
 * Copyright 2023 HM Revenue & Customs
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

class CheckYourAnswersAboutYourLeaseOrTenureControllerSpec extends TestBaseSpec {

  def checkYourAnswersAboutYourLeaseOrTenureController(
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(prefilledAboutLeaseOrAgreementPartTwo)
  ) =
    new CheckYourAnswersAboutYourLeaseOrTenureController(
      stubMessagesControllerComponents(),
      aboutYourLeaseOrTenureNavigator,
      checkYourAnswersAboutLeaseAndTenureView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartTwo = aboutLeaseOrAgreementPartTwo),
      mockSessionRepo
    )

  "GET /" should {
    "return 200" in {
      val result = checkYourAnswersAboutYourLeaseOrTenureController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = checkYourAnswersAboutYourLeaseOrTenureController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = checkYourAnswersAboutYourLeaseOrTenureController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
}
