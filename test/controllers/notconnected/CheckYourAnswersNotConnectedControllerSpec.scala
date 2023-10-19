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

package controllers.notconnected

import config.ErrorHandler
import connectors.{Audit, SubmissionConnector}
import models.submissions.NotConnectedSubmission
import models.submissions.notconnected.RemoveConnectionDetails
import org.mockito.ArgumentMatchers.anyString
import play.api.http.Status
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec

import scala.concurrent.Future

class CheckYourAnswersNotConnectedControllerSpec extends TestBaseSpec {

  val mockAudit: Audit                             = mock[Audit]
  val mockSubmissionConnector: SubmissionConnector = mock[SubmissionConnector]
  val errorHandler: ErrorHandler                   = inject[ErrorHandler]

  def checkYourAdditionalInformationController(
    removeConnectionDetails: Option[RemoveConnectionDetails] = Some(prefilledNotConnectedYes)
  ) = new CheckYourAnswersNotConnectedController(
    stubMessagesControllerComponents(),
    mockSubmissionConnector,
    checkYourAnswersNotConnectedView,
    confirmationNotConnectedView,
    errorHandler,
    mockAudit,
    preEnrichedActionRefiner(removeConnectionDetails = removeConnectionDetails),
    mockSessionRepo
  )

  "Not connected CYA controller" should {
    "return 200" in {
      val result = checkYourAdditionalInformationController().show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = checkYourAdditionalInformationController().show(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

    "handle submit form with all sections" in {
      mockSessionRepo.saveOrUpdate(prefilledBaseSession)
      when(mockSubmissionConnector.submitNotConnected(anyString(), any[NotConnectedSubmission])(any[HeaderCarrier]))
        .thenReturn(Future.successful(()))
      val result = checkYourAdditionalInformationController().submit(fakeRequest)
      status(result) shouldBe FOUND
    }

    "show submission confirmation" in {
      mockSessionRepo.saveOrUpdate(baseFilled6011Session)

      val result = checkYourAdditionalInformationController().confirmation(fakeRequest)
      status(result) shouldBe OK
      contentType(result) shouldBe Some("text/html")

      val content = contentAsString(result)
      content should include("confirmation.heading")
      content should include("print-link")
    }

  }
}
