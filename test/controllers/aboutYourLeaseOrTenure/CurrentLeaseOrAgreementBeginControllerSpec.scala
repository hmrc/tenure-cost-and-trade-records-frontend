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
import form.aboutYourLeaseOrTenure.CurrentLeaseOrAgreementBeginForm.currentLeaseOrAgreementBeginForm
import models.submissions.aboutYourLeaseOrTenure.AboutLeaseOrAgreementPartOne
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.FormBindingTestAssertions.mustContainError
import utils.TestBaseSpec

import scala.language.reflectiveCalls

class CurrentLeaseOrAgreementBeginControllerSpec extends TestBaseSpec {

  import TestData.{baseFormData, errorKey}

  val mockAudit: Audit = mock[Audit]
  def currentLeaseOrAgreementBeginController(
    aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = Some(prefilledAboutLeaseOrAgreementPartOne)
  )                    = new CurrentLeaseOrAgreementBeginController(
    stubMessagesControllerComponents(),
    mockAudit,
    aboutYourLeaseOrTenureNavigator,
    currentLeaseOrAgreementBeginView,
    preEnrichedActionRefiner(aboutLeaseOrAgreementPartOne = aboutLeaseOrAgreementPartOne),
    mockSessionRepo
  )

  "CurrentLeaseOrAgreementBeginController GET /" should {
    "return 200 and HTML with Current Lease Or Agreement Begin in the session" in {
      val result = currentLeaseOrAgreementBeginController().show(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.CurrentRentFirstPaidController.show().url
      )
    }

    "return 200 and HTML vacant property start date is not present in session" in {
      val controller = currentLeaseOrAgreementBeginController(Some(prefilledAboutLeaseOrAgreementPartOneNoStartDate))
      val result     = controller.show()(fakeRequest)
      status(result)        shouldBe Status.OK
      contentType(result)   shouldBe Some("text/html")
      charset(result)       shouldBe Some("utf-8")
      contentAsString(result) should include(
        controllers.aboutYourLeaseOrTenure.routes.CurrentRentFirstPaidController.show().url
      )
    }
  }

  "CurrentLeaseOrAgreementBeginController SUBMIT /" should {
    "throw a BAD_REQUEST if an empty form is submitted" in {

      val res = currentLeaseOrAgreementBeginController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty*)
      )
      status(res) shouldBe BAD_REQUEST
    }

    "Redirect when form data leaseBegin and grantedFor submitted" in {
      val res = currentLeaseOrAgreementBeginController().submit(
        FakeRequest(POST, "/").withFormUrlEncodedBody(
          "leaseBegin.month" -> "09",
          "leaseBegin.year"  -> "2017",
          "grantedFor"       -> "1 year"
        )
      )
      status(res) shouldBe SEE_OTHER
    }
  }

  "About your trading history form" should {
    "error if first occupy month and year are missing " in {
      val formData = baseFormData - errorKey.occupyMonth - errorKey.occupyYear
      val form     = currentLeaseOrAgreementBeginForm(using messages).bind(formData)

      mustContainError(errorKey.occupyMonth, "error.date.required", form)
    }

    "error if first occupy month is missing " in {
      val formData = baseFormData - errorKey.occupyMonth
      val form     = currentLeaseOrAgreementBeginForm(using messages).bind(formData)

      mustContainError(errorKey.occupyMonth, "error.date.mustInclude", form)
    }

    "error if first occupy year is missing" in {
      val formData = baseFormData - errorKey.occupyYear
      val form     = currentLeaseOrAgreementBeginForm(using messages).bind(formData)

      mustContainError(errorKey.occupyYear, "error.date.mustInclude", form)
    }

    "error if grantedFor given is missing" in {
      val formData = baseFormData - errorKey.grantedFor
      val form     = currentLeaseOrAgreementBeginForm(using messages).bind(formData)

      mustContainError(errorKey.grantedFor, "error.grantedFor.required", form)
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val occupyMonth = "leaseBegin.month"
      val occupyYear  = "leaseBegin.year"
      val grantedFor  = "grantedFor"
    }

    val baseFormData: Map[String, String] = Map(
      "leaseBegin.month" -> "9",
      "leaseBegin.year"  -> "2017",
      "grantedFor"       -> "1 year"
    )
  }
}
