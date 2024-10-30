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

package controllers.lettingHistory

import actions.{SessionRequest, WithSessionRefiner}
import controllers.FORDataCaptureController
import form.lettingHistory.CheckYourAnswerForm.theForm
import models.Session
import models.submissions.common.AnswersYesNo
import models.submissions.lettingHistory.LettingHistory.*
import navigation.LettingHistoryNavigator
import navigation.identifiers.CheckYourAnswersPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.lettingHistory.checkYourAnswers.template as CheckYourAnswersView

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future.successful
import scala.concurrent.ExecutionContext

@Singleton
class CheckYourAnswersController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: LettingHistoryNavigator,
  theView: CheckYourAnswersView,
  sessionRefiner: WithSessionRefiner,
  @Named("session") repository: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging:

  def show: Action[AnyContent] = (Action andThen sessionRefiner).async { implicit request =>
    val filledForm =
      for
        lettingHistory   <- request.sessionData.lettingHistory
        sectionCompleted <- lettingHistory.sectionCompleted
      yield theForm.fill(sectionCompleted.toAnswer)

    Ok(theView(filledForm.getOrElse(theForm), backLinkUrl))
  }

  def submit: Action[AnyContent] = (Action andThen sessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      theForm,
      theFormWithErrors => successful(BadRequest(theView(theFormWithErrors, backLinkUrl))),
      answer =>
        given Session = request.sessionData
        for
          newSession   <- successful(withSectionCompleted(answer.toBoolean))
          savedSession <- repository.saveOrUpdateSession(newSession)
        yield navigator.redirect(currentPage = CheckYourAnswersPageId, savedSession)
    )
  }

  private def backLinkUrl(using request: SessionRequest[AnyContent]): Option[String] =
    navigator.backLinkUrl(ofPage = CheckYourAnswersPageId)
