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

package controllers.requestReferenceNumber

import config.ErrorHandler
import form.requestReferenceNumber.CheckYourAnswersRequestReferenceNumberForm.checkYourAnswersRequestReferenceNumberForm
import connectors.{Audit, SubmissionConnector}
import models.submissions.requestReferenceNumber.RequestReferenceNumberDetails
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.TestBaseSpec
import utils.FormBindingTestAssertions.mustContainError

class CheckYourAnswersRequestReferenceNumberControllerSpec extends TestBaseSpec {

  import TestData._

  val submissionConnector: SubmissionConnector = mock[SubmissionConnector]

  def checkYourAnswersRequestReferenceController(
    requestReferenceNumberDetails: Option[RequestReferenceNumberDetails] = Some(prefilledRequestRefNumCYA)
  ) =
    new CheckYourAnswersRequestReferenceNumberController(
      stubMessagesControllerComponents(),
      inject[SubmissionConnector],
      checkYourAnswersRequestReferenceNumberView,
      confirmationRequestReferenceNumberView,
      inject[ErrorHandler],
      inject[Audit],
      preEnrichedActionRefiner(requestReferenceNumberDetails = requestReferenceNumberDetails),
      mockSessionRepo
    )

  def checkYourAnswersRequestReferenceControllerBlank(
    requestReferenceNumberDetails: Option[RequestReferenceNumberDetails] = Some(prefilledRequestRefNumBlank)
  ) =
    new CheckYourAnswersRequestReferenceNumberController(
      stubMessagesControllerComponents(),
      inject[SubmissionConnector],
      checkYourAnswersRequestReferenceNumberView,
      confirmationRequestReferenceNumberView,
      inject[ErrorHandler],
      inject[Audit],
      preEnrichedActionRefiner(requestReferenceNumberDetails = requestReferenceNumberDetails),
      mockSessionRepo
    )

  "GET /" should {
    "return 200" in {
      val result = checkYourAnswersRequestReferenceController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = checkYourAnswersRequestReferenceController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 with empty session" in {
      val result = checkYourAnswersRequestReferenceControllerBlank().show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "confirmation return 200" in {
      val result = checkYourAnswersRequestReferenceController().confirmation(fakeRequest)
      status(result) shouldBe Status.OK
    }
  }

  "SUBMIT /" should {
    "throw a FOUND if an empty form is submitted" in {
      val res = checkYourAnswersRequestReferenceController().submit(
        FakeRequest().withFormUrlEncodedBody(Seq.empty: _*)
      )
      status(res) shouldBe FOUND
    }
  }

  "Check Your Answers request reference number form" should {
    "error if checkYourAnswersRequestReferenceNumber is missing" in {
      val formData = baseFormData - errorKey.checkYourAnswersRequestReferenceNumber
      val form     = checkYourAnswersRequestReferenceNumberForm.bind(formData)

      mustContainError(errorKey.checkYourAnswersRequestReferenceNumber, "error.checkYourAnswersRadio.required", form)
    }
  }

  object TestData {
    val errorKey: Object {
      val checkYourAnswersRequestReferenceNumber: String
    } = new {
      val checkYourAnswersRequestReferenceNumber: String =
        "checkYourAnswersRequestReferenceNumber"
    }

    val baseFormData: Map[String, String] = Map("checkYourAnswersRequestReferenceNumber" -> "yes")
  }

}
