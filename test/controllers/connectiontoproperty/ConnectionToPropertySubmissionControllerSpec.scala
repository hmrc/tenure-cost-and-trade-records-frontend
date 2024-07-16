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

package controllers.connectiontoproperty

import config.ErrorHandler
import models.submissions.connectiontoproperty.StillConnectedDetails
import play.api.http.Status
import play.api.test.Helpers.{status, stubMessagesControllerComponents}
import connectors.{Audit, SubmissionConnector}
import models.submissions.ConnectedSubmission
import play.api.mvc.Request
import play.twirl.api.Html
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec

import scala.concurrent.Future

class ConnectionToPropertySubmissionControllerSpec extends TestBaseSpec {

  val audit               = mock[Audit]
  val submissionConnector = mock[SubmissionConnector]
  val errorHandler        = mock[ErrorHandler]
  // doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(any[HeaderCarrier], any[ExecutionContext])
  def connectionToPropertySubmissionController(
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledNotVacantPropertiesCYA)
  ) =
    new ConnectionToPropertySubmissionController(
      stubMessagesControllerComponents(),
      submissionConnector,
      errorHandler,
      confirmationVacantProperty,
      audit,
      preEnrichedActionRefiner(stillConnectedDetails = stillConnectedDetails),
      mockSessionRepo
    )

  "submit method" when {

    "submission is successful" should {
      "redirect (HTTP 303)" in {
        when(submissionConnector.submitConnected(anyString, any[ConnectedSubmission])(any[HeaderCarrier]))
          .thenReturn(Future.successful(()))
        val result = connectionToPropertySubmissionController().submit(fakeRequest)
        status(result) shouldBe Status.SEE_OTHER
      }
    }

    "submission fails" should {
      "return InternalServerError" in {
        when(submissionConnector.submitConnected(anyString, any[ConnectedSubmission])(any[HeaderCarrier]))
          .thenReturn(Future.failed(new RuntimeException("Test error")))
        when(errorHandler.internalServerErrorTemplate(org.mockito.ArgumentMatchers.any(classOf[Request[?]])))
          .thenReturn(Future.successful(Html("Some Error Message")))
        val result = connectionToPropertySubmissionController().submit(fakeRequest)
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }

  }
  "confirmation method" should {

    "display the confirmation view" in {
      val result = connectionToPropertySubmissionController().confirmation(fakeRequest)
      status(result) shouldBe Status.OK
    }

  }

}
