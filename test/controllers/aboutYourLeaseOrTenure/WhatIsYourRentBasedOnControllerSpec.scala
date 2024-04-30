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

import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne
import navigation.AboutYourLeaseOrTenureNavigator
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class WhatIsYourRentBasedOnControllerSpec extends TestBaseSpec {

  val mockAboutYourLeaseOrTenureNavigator = mock[AboutYourLeaseOrTenureNavigator]

  def whatIsYourRentBasedOnController(
    aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = Some(prefilledAboutLeaseOrAgreementPartOne)
  ) =
    new WhatIsYourRentBasedOnController(
      stubMessagesControllerComponents(),
      mockAboutYourLeaseOrTenureNavigator,
      whatIsYourRentBasedOnView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartOne = aboutLeaseOrAgreementPartOne),
      mockSessionRepo
    )

  def whatIsYourRentBasedOnControllerNone =
    new WhatIsYourRentBasedOnController(
      stubMessagesControllerComponents(),
      mockAboutYourLeaseOrTenureNavigator,
      whatIsYourRentBasedOnView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartOne = None),
      mockSessionRepo
    )

  "WhatIsYourRentBasedOnController GET /" should {
    "return 200 and HTML with What Is Your Rent Based On in the session" in {
      val result = whatIsYourRentBasedOnController().show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 and HTML when no What Is Your Rent Based On in the session" in {
      val result = whatIsYourRentBasedOnControllerNone.show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }

  "SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = whatIsYourRentBasedOnController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
}
