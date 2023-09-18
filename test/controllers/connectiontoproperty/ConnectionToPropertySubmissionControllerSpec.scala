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

package controllers.connectiontoproperty

import config.ErrorHandler
import models.submissions.connectiontoproperty.StillConnectedDetails
import play.api.http.Status
import play.api.test.Helpers.{status, stubMessagesControllerComponents}
import connectors.{Audit, SubmissionConnector}
import models.submissions.ConnectedSubmission
import org.mockito.ArgumentMatchers.anyString
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec

import scala.concurrent.Future

class ConnectionToPropertySubmissionControllerSpec extends TestBaseSpec {

  val audit               = mock[Audit]
  val submissionConnector = mock[SubmissionConnector]
  val errorHandler        = mock[ErrorHandler]
  //doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(any[HeaderCarrier], any[ExecutionContext])
  def connectionToPropertySubmissionController(
    stillConnectedDetails: Option[StillConnectedDetails] = Some(prefilledNotVacantPropertiesCYA)
  )                       =
    new ConnectionToPropertySubmissionController(
      stubMessagesControllerComponents(),
      confirmationConnectionToProperty,
      audit,
      preEnrichedActionRefiner(stillConnectedDetails = stillConnectedDetails),
      mockSessionRepo
    )

  "SUBMIT /" should {
    "return 302" in {
      when(submissionConnector.submitConnected(anyString(), any[ConnectedSubmission])(any[HeaderCarrier]))
        .thenReturn(Future.successful(()))
      val result = connectionToPropertySubmissionController().submit(fakeRequest)
      status(result) shouldBe Status.FOUND
    }
  }
}
