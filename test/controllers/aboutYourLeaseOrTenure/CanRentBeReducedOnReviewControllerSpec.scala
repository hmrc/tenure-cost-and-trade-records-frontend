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

import form.aboutYourLeaseOrTenure.CanRentBeReducedOnReviewForm.canRentBeReducedOnReviewForm
import models.ForTypes
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec

class CanRentBeReducedOnReviewControllerSpec extends TestBaseSpec {

  import TestData._
  import utils.FormBindingTestAssertions._
  def canRentBeReducedOnReviewController = new CanRentBeReducedOnReviewController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    canRentBeReducedOnReviewView,
    preEnrichedActionRefiner(),
    mockSessionRepo
  )

  def canRentBeReducedOnReviewController6020 = new CanRentBeReducedOnReviewController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    canRentBeReducedOnReviewView,
    preEnrichedActionRefiner(forType = ForTypes.for6020),
    mockSessionRepo
  )

  def canRentBeReducedOnReviewControllerNone = new CanRentBeReducedOnReviewController(
    stubMessagesControllerComponents(),
    aboutYourLeaseOrTenureNavigator,
    canRentBeReducedOnReviewView,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartTwo = None),
    mockSessionRepo
  )

  "CanRentBeReducedOnReviewController GET /" should {
    "return 200 and HTML with Can Rent Be Reduced On Review in the session" in {
      val result = canRentBeReducedOnReviewController.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.IntervalsOfRentReviewController.show().url
      )
    }

    "return 200 and HTML with Can Rent Be Reduced On Review in the session 6020" in {
      val result = canRentBeReducedOnReviewController6020.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.IntervalsOfRentReviewController.show().url
      )
    }

    "return 200 and HTML when no Can Rent Be Reduced On Review in the session" in {
      val result = canRentBeReducedOnReviewControllerNone.show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.IntervalsOfRentReviewController.show().url
      )
    }
  }

  "CanRentBeReducedOnReviewController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {
      val res = canRentBeReducedOnReviewController.submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe BAD_REQUEST
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
    val errorKey: Object {
      val canRentBeReducedOnReview: String
    } = new {
      val canRentBeReducedOnReview: String = "canRentBeReducedOnReview"
    }

    val baseFormData: Map[String, String] = Map("canRentBeReducedOnReview" -> "yes")
  }
}
