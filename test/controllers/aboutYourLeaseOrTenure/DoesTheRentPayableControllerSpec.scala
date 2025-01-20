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

class DoesTheRentPayableControllerSpec extends TestBaseSpec {

  val mockAudit: Audit = mock[Audit]
  def doesTheRentPayableController(
    aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = Some(prefilledAboutLeaseOrAgreementPartOne)
  ) = new DoesTheRentPayableController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYourLeaseOrTenureNavigator,
    doesTheRentPayableView,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartOne = aboutLeaseOrAgreementPartOne),
    mockSessionRepo
  )

  "DoesTheRentPayableController GET /" should {
    "return 200 and HTML with Does The Rent Payable in the session" in {
      val result = doesTheRentPayableController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.IncludedInYourRentController.show().url
      )
    }

    "return 200 and HTML when no Does The Rent Payable in the session" in {
      val controller = doesTheRentPayableController(aboutLeaseOrAgreementPartOne = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.IncludedInYourRentController.show().url
      )
    }

  }

  "DoesTheRentPayableController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {

      val res = doesTheRentPayableController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data rentPayable and detailsToQuestions submitted" in {
      val res = doesTheRentPayableController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody(
          "rentPayable[]"      -> "otherProperty",
          "detailsToQuestions" -> "Test content"
        )
      )
      status(res) shouldBe SEE_OTHER
    }
  }
}
