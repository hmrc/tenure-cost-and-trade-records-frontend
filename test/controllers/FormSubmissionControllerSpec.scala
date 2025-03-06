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

package controllers

import actions.WithSessionRefiner
import config.ErrorHandler
import connectors.{Audit, SubmissionConnector}
import models.submissions.ConnectedSubmission
import org.mockito.Mockito.spy
import play.api.libs.json.JsObject
import play.api.mvc.{Request, Result}
import play.api.test.Helpers.*
import stub.StubSessionRepo
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import utils.TestBaseSpec
import views.html.confirmation

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Yuriy Tumakha
  */
class FormSubmissionControllerSpec extends TestBaseSpec {

  private val sessionRepo = StubSessionRepo()
  val submissionConnector = mock[SubmissionConnector]
  val errorHandler        = mock[ErrorHandler]
  val audit               = spy(inject[Audit])

  private def formSubmissionController = new FormSubmissionController(
    stubMessagesControllerComponents(),
    submissionConnector,
    errorHandler,
    inject[confirmation],
    audit,
    WithSessionRefiner(sessionRepo),
    sessionRepo
  )

  "FormSubmissionController" should {
    "handle submit form with all sections" in {
      sessionRepo.saveOrUpdate(prefilledBaseSession)
      when(submissionConnector.submitConnected(anyString, any[ConnectedSubmission])(any[HeaderCarrier]))
        .thenReturn(Future.successful(HttpResponse(CREATED)))
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

    "handle errors in submit form" in {
      sessionRepo.saveOrUpdate(prefilledBaseSession)
      val exception = new RuntimeException("Test exception")
      when(submissionConnector.submitConnected(anyString, any[ConnectedSubmission])(any[HeaderCarrier]))
        .thenReturn(Future.failed(exception))
      when(errorHandler.internalServerErrorTemplate(any[Request[?]]))
        .thenReturn(Future.successful(play.twirl.api.HtmlFormat.empty))

      val result: Future[Result] = formSubmissionController.submit(fakeRequest)

      status(result) shouldBe INTERNAL_SERVER_ERROR
      verify(audit, times(2)).sendExplicitAudit(anyString, any[JsObject])(any[HeaderCarrier], any[ExecutionContext])
      verify(errorHandler, times(1)).internalServerErrorTemplate(any[Request[?]])
    }
  }

}
