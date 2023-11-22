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

package controllers

import actions.WithSessionRefiner
import config.ErrorHandler
import connectors.{Audit, SubmissionConnector}
import models.submissions.ConnectedSubmission
import org.mockito.ArgumentMatchers.anyString
import play.api.test.Helpers._
import stub.StubSessionRepo
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec
import views.html.confirmation

import scala.concurrent.Future

/**
  * @author Yuriy Tumakha
  */
class FormSubmissionControllerSpec extends TestBaseSpec {

  private val sessionRepo              = StubSessionRepo()
  val submissionConnector              = mock[SubmissionConnector]
  val errorHandler                     = mock[ErrorHandler]
  private def formSubmissionController = new FormSubmissionController(
    stubMessagesControllerComponents(),
    submissionConnector,
    errorHandler,
    inject[confirmation],
    inject[Audit],
    WithSessionRefiner(sessionRepo),
    sessionRepo
  )

  "FormSubmissionController" should {
    "handle submit form with all sections" in {
      sessionRepo.saveOrUpdate(prefilledBaseSession)
      when(submissionConnector.submitConnected(anyString(), any[ConnectedSubmission])(any[HeaderCarrier]))
        .thenReturn(Future.successful(()))
      val result = formSubmissionController.submit(fakeRequest)
      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(controllers.routes.FormSubmissionController.confirmation().url)
    }

    "show submission confirmation" in {
      sessionRepo.saveOrUpdate(baseFilled6011Session)

      val result = formSubmissionController.confirmation(fakeRequest)
      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")

      val content = contentAsString(result)
      content should include("confirmation.heading")
      content should include("print-link")
    }
  }

}
