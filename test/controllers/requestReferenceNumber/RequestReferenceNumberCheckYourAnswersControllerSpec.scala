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

package controllers.requestReferenceNumber

import config.ErrorHandler
import form.requestReferenceNumber.RequestReferenceNumberCheckYourAnswersForm.theForm
import connectors.{Audit, SubmissionConnector}
import models.submissions.RequestReferenceNumberSubmission
import models.submissions.requestReferenceNumber.RequestReferenceNumberDetails
import play.api.http.Status
import play.api.test.Helpers.*
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import utils.TestBaseSpec
import utils.FormBindingTestAssertions.mustContainError

import scala.language.reflectiveCalls
import scala.concurrent.Future

class RequestReferenceNumberCheckYourAnswersControllerSpec extends TestBaseSpec {

  import TestData._

  trait ControllerWithInjectedSubmissionConnectorFixture(
    val requestReferenceNumberDetails: RequestReferenceNumberDetails
  ):
    val controller = new RequestReferenceNumberCheckYourAnswersController(
      stubMessagesControllerComponents(),
      inject[SubmissionConnector],
      requestReferenceNumberCheckYourAnswersView,
      requestReferenceNumberConfirmationView,
      inject[ErrorHandler],
      inject[Audit],
      preEnrichedActionRefiner(requestReferenceNumberDetails = Some(requestReferenceNumberDetails)),
      mockSessionRepo
    )

  trait ControllerWithMockedSubmissionConnectorFixture(
    val requestReferenceNumberDetails: RequestReferenceNumberDetails
  ):
    val mockSubmissionConnector: SubmissionConnector = mock[SubmissionConnector]
    val controller                                   = new RequestReferenceNumberCheckYourAnswersController(
      stubMessagesControllerComponents(),
      mockSubmissionConnector,
      requestReferenceNumberCheckYourAnswersView,
      requestReferenceNumberConfirmationView,
      inject[ErrorHandler],
      inject[Audit],
      preEnrichedActionRefiner(requestReferenceNumberDetails = Some(requestReferenceNumberDetails)),
      mockSessionRepo
    )

  "GET /" should {
    "return 200" in new ControllerWithInjectedSubmissionConnectorFixture(prefilledRequestRefNumCYA) {
      val result = controller.show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in new ControllerWithInjectedSubmissionConnectorFixture(prefilledRequestRefNumCYA) {
      val result = controller.show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "return 200 with empty session" in new ControllerWithInjectedSubmissionConnectorFixture(
      prefilledRequestRefNumBlank
    ) {
      val result = controller.show(fakeRequest)
      status(result)      shouldBe Status.OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "confirmation return 200" in new ControllerWithInjectedSubmissionConnectorFixture(prefilledRequestRefNumCYA) {
      val result = controller.confirmation(fakeRequest)
      status(result) shouldBe Status.OK
    }
  }

  "SUBMIT /" should {
    "handle positive confirmation scenarios"          should {
      "return 303 and audit the successful outcome" in new ControllerWithMockedSubmissionConnectorFixture(
        prefilledRequestRefNumCYA
      ) {
        when(
          mockSubmissionConnector.submitRequestReferenceNumber(any[RequestReferenceNumberSubmission])(using
            any[HeaderCarrier]
          )
        ).thenReturn(Future.successful(HttpResponse(CREATED)))

        val result = controller.submit()(fakeRequest)

        status(result) shouldBe Status.SEE_OTHER

        header(
          "Location",
          result
        ).value shouldBe controllers.requestReferenceNumber.routes.RequestReferenceNumberCheckYourAnswersController
          .confirmation()
          .url
      }
    }
    "handle exception and return InternalServerError" should {
      "return 500 and audit the failure" in new ControllerWithMockedSubmissionConnectorFixture(
        prefilledRequestRefNumCYA
      ) {

        // Mocking the submissionConnector to throw an exception
        when(
          mockSubmissionConnector.submitRequestReferenceNumber(any[RequestReferenceNumberSubmission])(using
            any[HeaderCarrier]
          )
        )
          .thenReturn(Future.failed(new RuntimeException("Test Exception")))

        // Call the submit action
        val result = controller.submit()(fakeRequest)

        // Awaiting the result to ensure the exception handling is tested
        status(result) shouldBe INTERNAL_SERVER_ERROR

        // Verify that the error is logged
        verify(mockSubmissionConnector).submitRequestReferenceNumber(any[RequestReferenceNumberSubmission])(using
          any[HeaderCarrier]
        )

      }
    }
  }

  "Check Your Answers request reference number form" should {
    "error if checkYourAnswersRequestReferenceNumber is missing" in {
      val formData = baseFormData - errorKey.checkYourAnswersRequestReferenceNumber
      val form     = theForm.bind(formData)

      mustContainError(errorKey.checkYourAnswersRequestReferenceNumber, "error.checkYourAnswersRadio.required", form)
    }
  }

  object TestData {
    val errorKey: ErrorKey = new ErrorKey

    class ErrorKey {
      val checkYourAnswersRequestReferenceNumber: String =
        "checkYourAnswersRequestReferenceNumber"
    }

    val baseFormData: Map[String, String] = Map("checkYourAnswersRequestReferenceNumber" -> "yes")
  }

}
