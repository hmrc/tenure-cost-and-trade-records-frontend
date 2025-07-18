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
import form.aboutYourLeaseOrTenure.CanRentBeReducedOnReviewForm.canRentBeReducedOnReviewForm
import models.ForType
import models.ForType.*
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartTwo
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

import scala.language.reflectiveCalls

class CanRentBeReducedOnReviewControllerSpec extends TestBaseSpec {

  import TestData._
  import utils.FormBindingTestAssertions._
  val mockAudit: Audit = mock[Audit]

  def canRentBeReducedOnReviewController(
    forType: ForType = FOR6010,
    aboutLeaseOrAgreementPartTwo: Option[AboutLeaseOrAgreementPartTwo] = Some(prefilledAboutLeaseOrAgreementPartTwo)
  ) = new CanRentBeReducedOnReviewController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYourLeaseOrTenureNavigator,
    canRentBeReducedOnReviewView,
    preEnrichedActionRefiner(forType = forType, aboutLeaseOrAgreementPartTwo = aboutLeaseOrAgreementPartTwo),
    mockSessionRepo
  )

  "CanRentBeReducedOnReviewController GET /" should {
    "return 200 and HTML with Can Rent Be Reduced On Review in the session" in {
      val result = canRentBeReducedOnReviewController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.IntervalsOfRentReviewController.show().url
      )
    }

    "return 200 and HTML with Can Rent Be Reduced On Review in the session 6020" in {
      val controller = canRentBeReducedOnReviewController(FOR6020)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.IntervalsOfRentReviewController.show().url
      )
    }

    "return 200 and HTML when no Can Rent Be Reduced On Review in the session 6020" in {
      val controller = canRentBeReducedOnReviewController(FOR6020, None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.IsRentUnderReviewController.show().url
      )
    }

    "return 200 and HTML when no Can Rent Be Reduced On Review in the session" in {
      val controller = canRentBeReducedOnReviewController(aboutLeaseOrAgreementPartTwo = None)
      val result     = controller.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.IsRentReviewPlannedController.show().url
      )
    }
  }

  "CanRentBeReducedOnReviewController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = canRentBeReducedOnReviewController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data canRentBeReducedOnReview submitted" in {
      val res = canRentBeReducedOnReviewController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody("canRentBeReducedOnReview" -> "yes")
      )
      status(res) shouldBe SEE_OTHER
    }
  }

  "Can rent be reduced on review form" should {
    "error if Can rent be reduced on review answer is missing" in {
      val formData = baseFormData - errorKey.canRentBeReducedOnReview
      val form     = canRentBeReducedOnReviewForm.bind(formData)

      mustContainError(errorKey.canRentBeReducedOnReview, "error.canRentBeReducedOnReview.missing", form)
    }
  }

  object TestData {
    val errorKey: ErrorKey                = new ErrorKey
    class ErrorKey {
      val canRentBeReducedOnReview: String = "canRentBeReducedOnReview"
    }
    val baseFormData: Map[String, String] = Map("canRentBeReducedOnReview" -> "yes")
  }
}
