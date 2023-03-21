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
import connectors.{Audit, BackendConnector}
import form.CustomUserPasswordForm.customUserPasswordForm
import models.{Session, SubmissionDraft}
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{MessagesControllerComponents, Result}
import repositories.SessionRepo
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import util.DateUtil
import views.html.customPasswordSaveAsDraft

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
  dateUtil: DateUtil,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") sessionRepo: SessionRepo,
  audit: Audit,
  cc: MessagesControllerComponents
)(implicit ec: ExecutionContext)
    extends FrontendController(cc)
    with I18nSupport {

  private val saveForDays = 90

  def customPassword(exitPath: String) = (Action andThen withSessionRefiner).async { implicit request =>
    val session = request.sessionData
    if (session.saveAsDraftPassword.isDefined) {
      saveSubmissionDraft(session, exitPath)
    } else {
      Ok(customPasswordSaveAsDraftView(customUserPasswordForm, expiryDate, exitPath))
    }
  }

  def saveAsDraft(exitPath: String) = (Action andThen withSessionRefiner).async { implicit request =>
    customUserPasswordForm
      .bindFromRequest()
      .fold(
        formWithErrors => Ok(customPasswordSaveAsDraftView(formWithErrors, expiryDate, exitPath)),
        validData => {
          val session = request.sessionData.copy(saveAsDraftPassword = validData.password)
          sessionRepo.saveOrUpdate(session)
          saveSubmissionDraft(session, exitPath)
        }
      )
  }

  private def expiryDate(implicit messages: Messages): String = dateUtil formatDate LocalDate.now.plusDays(saveForDays)

  private def saveSubmissionDraft(session: Session, exitPath: String)(implicit hc: HeaderCarrier): Future[Result] = {
    val forType         = session.userLoginDetails.forType
    val referenceNumber = session.userLoginDetails.referenceNumber
    val submissionDraft = SubmissionDraft(forType, session, exitPath)

    backendConnector.saveAsDraft(referenceNumber, submissionDraft).map { _ =>
      audit.sendSavedAsDraft(submissionDraft)
      Ok(s"Draft saved. Password: ${session.saveAsDraftPassword.getOrElse("")}")
    }
  }

}
