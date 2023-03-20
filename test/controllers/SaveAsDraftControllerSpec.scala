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
import connectors.Audit
import play.api.http.Status.OK
import play.api.test.Helpers.{POST, contentAsString, status, stubMessagesControllerComponents}
import stub.{StubBackendConnector, StubSessionRepo}
import util.DateUtil
import utils.TestBaseSpec
import views.html.customPasswordSaveAsDraft

/**
  * @author Yuriy Tumakha
  */
class SaveAsDraftControllerSpec extends TestBaseSpec {

  private val sessionRepo      = StubSessionRepo()
  private val backendConnector = StubBackendConnector()
  private val password         = "P@$$word"
  private val exitPath         = "/exit/path"

  private def saveAsDraftController = new SaveAsDraftController(
    backendConnector,
    inject[customPasswordSaveAsDraft],
    inject[DateUtil],
    WithSessionRefiner(inject[ErrorHandler], sessionRepo),
    sessionRepo,
    inject[Audit],
    stubMessagesControllerComponents()
  )

  "SaveAsDraftController.customPassword" should {
    "return customUserPasswordForm if session.saveAsDraftPassword is empty" in {
      sessionRepo.saveOrUpdate(prefilledBaseSession.copy(saveAsDraftPassword = None))

      val result = saveAsDraftController.customPassword(exitPath)(fakeRequest)
      status(result) shouldBe OK
      checkCustomUserPasswordForm(contentAsString(result))
    }

    "save SubmissionDraft and return page displaying saveAsDraftPassword if session.saveAsDraftPassword is defined" in {
      sessionRepo.saveOrUpdate(prefilledBaseSession.copy(saveAsDraftPassword = Some(password)))

      val result = saveAsDraftController.customPassword(exitPath)(fakeRequest)
      status(result) shouldBe OK
      checkFinalPageWithPassword(contentAsString(result))
    }
  }

  "SaveAsDraftController.saveAsDraft" should {
    "return customUserPasswordForm with errors on submit empty from" in {
      val result = saveAsDraftController.saveAsDraft(exitPath)(fakeRequest)

      status(result) shouldBe OK
      checkCustomUserPasswordForm(
        contentAsString(result),
        Seq("<a href=\"#password\">error.required</a>", "<a href=\"#confirmPassword\">error.required</a>")
      )
    }

    "return customUserPasswordForm with saveAsDraft.error.passwordsDontMatch" in {
      val result = saveAsDraftController.saveAsDraft(exitPath)(
        fakeRequest
          .withMethod(POST)
          .withFormUrlEncodedBody("password" -> "pass135", "confirmPassword" -> "pass2468")
      )

      status(result) shouldBe OK
      checkCustomUserPasswordForm(
        contentAsString(result),
        Seq("saveAsDraft.error.passwordsDontMatch")
      )
    }

    "save SubmissionDraft and return page displaying saveAsDraftPassword on submit valid form" in {
      val result = saveAsDraftController.saveAsDraft(exitPath)(
        fakeRequest
          .withMethod(POST)
          .withFormUrlEncodedBody("password" -> password, "confirmPassword" -> password)
      )

      status(result) shouldBe OK
      checkFinalPageWithPassword(contentAsString(result))
    }
  }

  private def checkCustomUserPasswordForm(content: String, expectedErrors: Seq[String] = Seq.empty): Unit = {
    content should include("saveAsDraft.createPassword.header")
    content should include("""name="password"""")
    content should include("""name="confirmPassword"""")
    content should include(exitPath)
    expectedErrors.foreach { expectedError =>
      content should include(expectedError)
    }
  }

  private def checkFinalPageWithPassword(content: String): Unit = {
    content    should include(password)
    content shouldNot include("saveAsDraft.createPassword.header")
    content shouldNot include("""name="password"""")
  }

}
