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

package controllers

import actions.WithSessionRefiner
import config.ErrorHandler
import connectors.Audit
import crypto.MongoHasher
import models.ForType.*
import play.api.http.Status.{BAD_REQUEST, NOT_FOUND, OK, SEE_OTHER, TEMPORARY_REDIRECT}
import play.api.test.Helpers.{POST, contentAsString, redirectLocation, status, stubMessagesControllerComponents}
import stub.{StubBackendConnector, StubSessionRepo}
import util.DateUtilLocalised
import utils.TestBaseSpec
import views.html.{customPasswordSaveAsDraft, saveAsDraftLogin, sessionTimeout, submissionDraftSaved}

/**
  * @author Yuriy Tumakha
  */
class SaveAsDraftControllerSpec extends TestBaseSpec {

  private val sessionRepo      = StubSessionRepo()
  private val backendConnector = StubBackendConnector()
  private val password         = "P@$$word1"
  private val exitPath         = "/exit/path"

  def mongoHasher: MongoHasher = inject[MongoHasher]

  private def saveAsDraftController = new SaveAsDraftController(
    backendConnector,
    inject[customPasswordSaveAsDraft],
    inject[submissionDraftSaved],
    inject[saveAsDraftLogin],
    inject[sessionTimeout],
    inject[DateUtilLocalised],
    inject[MongoHasher],
    WithSessionRefiner(sessionRepo),
    sessionRepo,
    inject[ErrorHandler],
    inject[Audit],
    stubMessagesControllerComponents()
  )

  "SaveAsDraftController.customPassword" should {
    "return 307 if session is empty" in {
      sessionRepo.remove()

      val result = saveAsDraftController.customPassword(exitPath)(fakeRequest)
      status(result) shouldBe TEMPORARY_REDIRECT
    }

    "return customUserPasswordForm if session.saveAsDraftPassword is empty" in {
      sessionRepo.saveOrUpdate(prefilledBaseSession.copy(saveAsDraftPassword = None))

      val result = saveAsDraftController.customPassword(exitPath)(fakeRequest)
      status(result) shouldBe OK
      checkCustomUserPasswordForm(contentAsString(result))
    }

    "save SubmissionDraft and return page displaying saveAsDraftPassword if session.saveAsDraftPassword is defined" in {
      sessionRepo.saveOrUpdate(prefilledBaseSession.copy(saveAsDraftPassword = Some(mongoHasher.hash(password))))

      val result = saveAsDraftController.customPassword(exitPath)(fakeRequest)
      status(result) shouldBe OK
      checkFinalPageDraftSaved(contentAsString(result))
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
          .withFormUrlEncodedBody("password" -> "pass1357", "confirmPassword" -> "pass2468")
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
      checkFinalPageDraftSaved(contentAsString(result))

      val session =
        backendConnector.loadSubmissionDraft(prefilledBaseSession.referenceNumber, hc).futureValue.get.session
      session.address shouldBe submissionDraft.session.address

      val passwordHash = session.saveAsDraftPassword.getOrElse("")
      mongoHasher.verify(password, passwordHash) shouldBe true
    }
  }

  "SaveAsDraftController.loginToResume" should {
    "return saveAsDraftLoginForm" in {
      val result = saveAsDraftController.loginToResume(fakeRequest)
      status(result) shouldBe OK
      checkSaveAsDraftLoginForm(contentAsString(result))
    }
  }

  "SaveAsDraftController.resume" should {
    "return saveAsDraftLoginForm with error on submit empty from" in {
      val result = saveAsDraftController.resume(fakeRequest)

      status(result) shouldBe BAD_REQUEST
      checkSaveAsDraftLoginForm(
        contentAsString(result),
        Seq("<a href=\"#password\">error.required</a>")
      )
    }

    "return 404 if SubmissionDraft doesn't exist" in {
      val refNum = prefilledBaseSession.referenceNumber
      sessionRepo.saveOrUpdate(prefilledBaseSession)
      backendConnector.deleteSubmissionDraft(refNum, hc)
      backendConnector.loadSubmissionDraft(refNum, hc).futureValue shouldBe None // SubmissionDraft deleted

      val result = saveAsDraftController.resume(
        fakeRequest
          .withMethod(POST)
          .withFormUrlEncodedBody("password" -> "Pa$$word1234567890")
      )
      status(result) shouldBe NOT_FOUND
    }

    "return saveAsDraftLoginForm with error `saveAsDraft.error.invalidPassword`" in {
      val refNum = submissionDraft.session.referenceNumber
      sessionRepo.saveOrUpdate(submissionDraft.session)
      backendConnector.saveAsDraft(refNum, submissionDraft, hc)
      backendConnector.loadSubmissionDraft(refNum, hc).futureValue shouldBe Some(
        submissionDraft
      ) // SubmissionDraft exists

      val result = saveAsDraftController.resume(
        fakeRequest
          .withMethod(POST)
          .withFormUrlEncodedBody("password" -> "Pa$$word1234567890")
      )

      status(result) shouldBe BAD_REQUEST
      checkSaveAsDraftLoginForm(
        contentAsString(result),
        Seq("<a href=\"#password\">saveAsDraft.error.invalidPassword</a>")
      )
    }

    "load SubmissionDraft to user session and redirect to exitPath" in {
      val session = prefilledBaseSession.copy(saveAsDraftPassword = Some(mongoHasher.hash(password)))
      val refNum  = session.referenceNumber
      val draft   = submissionDraft.copy(session = session)
      backendConnector.saveAsDraft(refNum, draft, hc)
      backendConnector.loadSubmissionDraft(refNum, hc).futureValue shouldBe Some(draft) // SubmissionDraft exists
      sessionRepo.saveOrUpdate(session.copy(token = "NEW_TOKEN"))

      val sessionBefore = sessionRepo.get.futureValue.value
      mongoHasher.verify(password, sessionBefore.saveAsDraftPassword.getOrElse("")) shouldBe true

      sessionBefore.token shouldBe "NEW_TOKEN"

      val result = saveAsDraftController.resume(
        fakeRequest
          .withMethod(POST)
          .withFormUrlEncodedBody("password" -> password)
      )

      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(draft.exitPath)

      val sessionAfter = sessionRepo.get.futureValue.value
      sessionAfter.saveAsDraftPassword shouldBe None
      sessionAfter.token               shouldBe "NEW_TOKEN"
      sessionAfter.forType             shouldBe FOR6010
    }
  }

  "SaveAsDraftController.startAgain" should {
    "delete SubmissionDraft and redirect to start page" in {
      val refNum = submissionDraft.session.referenceNumber
      backendConnector.saveAsDraft(refNum, submissionDraft, hc)

      backendConnector.loadSubmissionDraft(refNum, hc).futureValue shouldBe Some(
        submissionDraft
      ) // SubmissionDraft exists

      val result = saveAsDraftController.startAgain(fakeRequest)
      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(LoginController.startPage.url)

      backendConnector.loadSubmissionDraft(refNum, hc).futureValue shouldBe None // SubmissionDraft deleted
    }
  }

  "SaveAsDraftController.timeout" should {
    "save SubmissionDraft with generated password and redirect to session timeout page" in {
      val refNum = submissionDraft.session.referenceNumber
      sessionRepo.saveOrUpdate(submissionDraft.session.copy(saveAsDraftPassword = None))

      val result = saveAsDraftController.timeout(exitPath)(fakeRequest)
      status(result)           shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some(routes.SaveAsDraftController.sessionTimeout.url)

      val session = backendConnector.loadSubmissionDraft(refNum, hc).futureValue.get.session
      session.address shouldBe submissionDraft.session.address
    }
  }

  "SaveAsDraftController.sessionTimeout" should {
    "show session timeout page with generated password and draft expiration date" in {
      val generatedPassword = "2345xyz"

      val result =
        saveAsDraftController.sessionTimeout(fakeRequest.withSession("generatedPassword" -> generatedPassword))
      status(result) shouldBe OK

      val content = contentAsString(result)
      content should include("saveAsDraft.preHeaderTimeout")
      content should include(generatedPassword)
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

  private def checkFinalPageDraftSaved(content: String): Unit = {
    content    should include("saveAsDraft.preHeader")
    content    should include("saveAsDraft.logout")
    content shouldNot include(password)
    content shouldNot include("saveAsDraft.createPassword.header")
    content shouldNot include("""name="password"""")
  }

  private def checkSaveAsDraftLoginForm(content: String, expectedErrors: Seq[String] = Seq.empty): Unit = {
    content should include("saveAsDraft.retrieveYourDraft")
    content should include("""name="password"""")
    content should include("saveAsDraft.startAgain")
    expectedErrors.foreach { expectedError =>
      content should include(expectedError)
    }
  }

}
