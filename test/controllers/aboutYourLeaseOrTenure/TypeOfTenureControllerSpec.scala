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
import play.api.http.Status.BAD_REQUEST
import play.api.test.FakeRequest
import play.api.test.Helpers.{charset, contentType, status, stubMessagesControllerComponents}
import utils.TestBaseSpec

class TypeOfTenureControllerSpec extends TestBaseSpec {

  def typeOfTenureController(
    aboutLeaseOrAgreementPartThree: Option[AboutLeaseOrAgreementPartThree] = Some(
      prefilledAboutLeaseOrAgreementPartThree
    )
  ) =
    new TypeOfTenureController(
      stubMessagesControllerComponents(),
      aboutYourLeaseOrTenureNavigator,
      typeOfTenureView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartThree = aboutLeaseOrAgreementPartThree),
      mockSessionRepo
    )

  def typeOfTenureControllerNone =
    new TypeOfTenureController(
      stubMessagesControllerComponents(),
      aboutYourLeaseOrTenureNavigator,
      typeOfTenureView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartThree = None),
      mockSessionRepo
    )

  " TypeOfTenureController GET /" should {
    "return 200 with data in session" in {
      val result = typeOfTenureController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML with data in session" in {
      val result = typeOfTenureController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 with no data in session" in {
      val result = typeOfTenureControllerNone.show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {

      val res = typeOfTenureController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
}
