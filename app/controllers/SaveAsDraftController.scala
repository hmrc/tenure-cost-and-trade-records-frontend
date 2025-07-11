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
import connectors.{Audit, BackendConnector}
import crypto.MongoHasher
import form.CustomUserPasswordForm.customUserPasswordForm
import form.SaveAsDraftLoginForm.saveAsDraftLoginForm
import models.{Session, SubmissionDraft}
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request, Result}
import repositories.SessionRepo
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import util.{AlphanumericPasswordGenerator, DateUtilLocalised}
import views.html.{customPasswordSaveAsDraft, saveAsDraftLogin, sessionTimeout, submissionDraftSaved}

import java.time.LocalDate
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Yuriy Tumakha
  */
@Singleton
class SaveAsDraftController @Inject() (
  backendConnector: BackendConnector,
  customPasswordSaveAsDraftView: customPasswordSaveAsDraft,
  submissionDraftSavedView: submissionDraftSaved,
  saveAsDraftLoginView: saveAsDraftLogin,
  sessionTimeoutView: sessionTimeout,
  dateUtil: DateUtilLocalised,
  mongoHasher: MongoHasher,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") sessionRepo: SessionRepo,
  errorHandler: ErrorHandler,
  audit: Audit,
  cc: MessagesControllerComponents
)(implicit ec: ExecutionContext)
    extends FrontendController(cc)
    with I18nSupport {

  private val saveForDays = 90

  def customPassword(exitPath: String): Action[AnyContent] = (Action andThen withSessionRefiner).async {
    implicit request =>
      val session = request.sessionData
      if (session.saveAsDraftPassword.isDefined) {
        saveSubmissionDraft(session, exitPath)
      } else {
        Ok(customPasswordSaveAsDraftView(customUserPasswordForm, expiryDate, exitPath, request.sessionData.toSummary))
      }
  }

  def saveAsDraft(exitPath: String): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    customUserPasswordForm
      .bindFromRequest()
      .fold(
        formWithErrors =>
          Ok(customPasswordSaveAsDraftView(formWithErrors, expiryDate, exitPath, request.sessionData.toSummary)),
        validData => {
          val session = request.sessionData.copy(saveAsDraftPassword = mongoHasher.hash(validData.password))
          sessionRepo.saveOrUpdate(session)
          saveSubmissionDraft(session, exitPath)
        }
      )
  }

  private def expiryDate(implicit messages: Messages): String = dateUtil.formatDate(LocalDate.now.plusDays(saveForDays))

  private def saveSubmissionDraft(session: Session, exitPath: String)(implicit
    hc: HeaderCarrier,
    request: Request[?]
  ): Future[Result] = {
    val forType         = session.forType
    val submissionDraft = SubmissionDraft(forType, session, exitPath)

    backendConnector.saveAsDraft(session.referenceNumberCleaned, submissionDraft, hc).map { _ =>
      audit.sendSavedAsDraft(submissionDraft.toSavedAsDraftEvent)
      Ok(submissionDraftSavedView(expiryDate, exitPath))
    }
  }

  def loginToResume: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Ok(saveAsDraftLoginView(saveAsDraftLoginForm, request.sessionData.toSummary))
  }

  def resume: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val session = request.sessionData

    saveAsDraftLoginForm
      .bindFromRequest()
      .fold(
        formWithErrors => BadRequest(saveAsDraftLoginView(formWithErrors, request.sessionData.toSummary)),
        password =>
          backendConnector.loadSubmissionDraft(session.referenceNumber, hc).flatMap {
            case Some(draft) if draft.session.saveAsDraftPassword.exists(mongoHasher.verify(password, _)) =>
              val restoredSession = draft.session.copy(token = session.token, saveAsDraftPassword = None)
              sessionRepo.saveOrUpdate(restoredSession)

              Redirect(draft.exitPath)
            case None                                                                                     =>
              errorHandler.notFoundTemplate(using request).map(NotFound(_))
            case _                                                                                        =>
              val formWithLoginError = saveAsDraftLoginForm.withError("password", "saveAsDraft.error.invalidPassword")
              BadRequest(saveAsDraftLoginView(formWithLoginError, request.sessionData.toSummary))
          }
      )
  }

  def startAgain: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    backendConnector.deleteSubmissionDraft(request.sessionData.referenceNumberCleaned, hc)
    Redirect(LoginController.startPage)
  }

  def timeout(exitPath: String): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val session = request.sessionData
    if (session.saveAsDraftPassword.isDefined) {
      saveSubmissionDraft(session, exitPath)
    } else {
      val generatedPassword = AlphanumericPasswordGenerator.generatePassword
      val updatedSession    = session.copy(saveAsDraftPassword = mongoHasher.hash(generatedPassword))

      for {
        _ <- saveSubmissionDraft(updatedSession, exitPath)
        _ <- sessionRepo.remove()
      } yield {
        audit.sendExplicitAudit("UserTimeout", updatedSession.toUserData)
        Redirect(routes.SaveAsDraftController.sessionTimeout).withSession("generatedPassword" -> generatedPassword)
      }
    }
  }

  def sessionTimeout: Action[AnyContent] = Action { implicit request =>
    val generatedPassword = request.session.get("generatedPassword").getOrElse("")
    Ok(sessionTimeoutView(generatedPassword, expiryDate))
  }

}
