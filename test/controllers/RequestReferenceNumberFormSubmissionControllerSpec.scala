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
import play.api.http.Status.{FOUND, OK}
import play.api.test.Helpers.{contentType, redirectLocation, status, stubMessagesControllerComponents}
import stub.StubSessionRepo
import utils.TestBaseSpec
import views.html.confirmation

class RequestReferenceNumberFormSubmissionControllerSpec extends TestBaseSpec {

  private val sessionRepo              = StubSessionRepo()
  val submissionConnector              = mock[SubmissionConnector]
  val errorHandler                     = mock[ErrorHandler]
  private def requestReferenceNumberFormSubmissionController = new RequestReferenceNumberFormSubmissionController(
    stubMessagesControllerComponents(),
    inject[confirmation],
    inject[Audit],
    WithSessionRefiner(inject[ErrorHandler], sessionRepo),
    sessionRepo
  )

    "RequestReferenceNumberFormSubmissionController" should {
      "handle submit form with all sections" in {
        sessionRepo.saveOrUpdate(prefilledBaseSession)
        val result = requestReferenceNumberFormSubmissionController.submit(fakeRequest)
        status(result) shouldBe FOUND
        redirectLocation(result) shouldBe Some(controllers.routes.RequestReferenceNumberFormSubmissionController.confirmation().url)
      }

      "show submission confirmation" in {
        sessionRepo.saveOrUpdate(baseFilled6011Session)

        val result = requestReferenceNumberFormSubmissionController.confirmation(fakeRequest)
        status(result) shouldBe OK
        contentType(result) shouldBe Some("text/html")
      }
  }
}