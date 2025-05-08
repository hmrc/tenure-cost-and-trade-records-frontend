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
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class RentPayableVaryOnQuantityOfBeersDetailsControllerSpec extends TestBaseSpec {

  val mockAudit: Audit = mock[Audit]

  def rentPayableVaryOnQuantityOfBeersDetailsController(
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(prefilledAboutLeaseOrAgreementPartTwo)
  ) =
    new RentPayableVaryOnQuantityOfBeersDetailsController(
      stubMessagesControllerComponents(),
      mockAudit,
      aboutYourLeaseOrTenureNavigator,
      rentPayableVaryOnQuantityOfBeersDetailsView,
      preEnrichedActionRefiner(aboutLeaseOrAgreementPartTwo = aboutLeaseOrAgreementPartTwo),
      mockSessionRepo
    )

  "RentPayableVaryOnQuantityOfBeersDetailsController GET /" should {
    "return 200" in {
      val result = rentPayableVaryOnQuantityOfBeersDetailsController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryOnQuantityOfBeersController.show().url
      )
    }

    "return 200 and HTML with none in the session" in {
      val controller = rentPayableVaryOnQuantityOfBeersDetailsController(aboutLeaseOrAgreementPartTwo = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryOnQuantityOfBeersController.show().url
      )
    }
  }

  "RentPayableVaryOnQuantityOfBeersDetailsController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = rentPayableVaryOnQuantityOfBeersDetailsController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data submitted" in {
      val res = rentPayableVaryOnQuantityOfBeersDetailsController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody(
          "rentPayableVaryOnQuantityOfBeersDetails" -> "text"
        )
      )
      status(res) shouldBe SEE_OTHER
    }

    "throw a BAD_REQUEST if describeFittingsTextArea is greater than max length is submitted" in {
      val res = rentPayableVaryOnQuantityOfBeersDetailsController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody(
          "rentPayableVaryOnQuantityOfBeersDetails" -> "x" * 2001
        )
      )
      status(res) shouldBe BAD_REQUEST
    }
  }
}
