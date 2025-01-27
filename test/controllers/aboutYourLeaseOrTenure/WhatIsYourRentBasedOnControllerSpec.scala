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
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class WhatIsYourRentBasedOnControllerSpec extends TestBaseSpec {

  val mockAudit: Audit = mock[Audit]

  def whatIsYourRentBasedOnController(
    aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = Some(prefilledAboutLeaseOrAgreementPartOne)
  ) =
    new WhatIsYourRentBasedOnController(
      stubMessagesControllerComponents(),
      mockAudit,
      aboutYourLeaseOrTenureNavigator,
      whatIsYourRentBasedOnView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartOne = aboutLeaseOrAgreementPartOne),
      mockSessionRepo
    )

  def whatIsYourRentBasedOnControllerNone =
    new WhatIsYourRentBasedOnController(
      stubMessagesControllerComponents(),
      mockAudit,
      aboutYourLeaseOrTenureNavigator,
      whatIsYourRentBasedOnView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartOne = None),
      mockSessionRepo
    )

  "WhatIsYourRentBasedOnController GET /" should {
    "return 200 and HTML with What Is Your Rent Based On in the session" in {
      val result = whatIsYourRentBasedOnController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentOpenMarketValueController.show().url
      )
    }

    "return 200 and HTML when no What Is Your Rent Based On in the session" in {
      val controller = whatIsYourRentBasedOnController(aboutLeaseOrAgreementPartOne = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentOpenMarketValueController.show().url
      )
    }
  }

  "WhatIsYourRentBasedOnController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = whatIsYourRentBasedOnController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "throw a BAD_REQUEST if other selected but whatIsYourRentBasedOn is empty when submitted" in {
      val res = whatIsYourRentBasedOnController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody(
          "currentRentBasedOn" -> "other"
        )
      )
      status(res) shouldBe BAD_REQUEST
    }

    "throw a BAD_REQUEST if whatIsYourRentBasedOn is greater than max length is submitted" in {
      val res = whatIsYourRentBasedOnController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody(
          "whatIsYourRentBasedOn" -> "x" * 501
        )
      )
      status(res) shouldBe BAD_REQUEST
    }

    "throw a BAD_REQUEST if currentRentBasedOn is other without whatIsYourRentBasedOn" in {
      val res = whatIsYourRentBasedOnController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody(
          "currentRentBasedOn" -> "other"
        )
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data currentRentBasedOn submitted" in {
      val res = whatIsYourRentBasedOnController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody(
          "currentRentBasedOn" -> "fixed"
        )
      )
      status(res) shouldBe SEE_OTHER
    }
  }
}
