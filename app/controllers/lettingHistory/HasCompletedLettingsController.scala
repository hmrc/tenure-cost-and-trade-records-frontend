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

package controllers.lettingHistory

import actions.{SessionRequest, WithSessionRefiner}
import controllers.FORDataCaptureController
import form.lettingHistory.HasCompletedLettingsForm.theForm
import models.Session
import models.submissions.common.AnswersYesNo
import models.submissions.lettingHistory.LettingHistory.*
import navigation.LettingHistoryNavigator
import navigation.identifiers.*
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.lettingHistory.hasCompletedLettings as HasCompletedLettingsView

import javax.inject.{Inject, Named}
import scala.concurrent.Future.successful
import scala.concurrent.{ExecutionContext, Future}

class HasCompletedLettingsController @Inject (
  mcc: MessagesControllerComponents,
  navigator: LettingHistoryNavigator,
  theView: HasCompletedLettingsView,
  sessionRefiner: WithSessionRefiner,
  @Named("session") repository: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with RentalPeriodSupport
    with I18nSupport:

  def show: Action[AnyContent] = (Action andThen sessionRefiner).apply { implicit request =>
    val freshForm  = theForm
    val filledForm =
      for
        lettingHistory       <- request.sessionData.lettingHistory
        hasCompletedLettings <- lettingHistory.hasCompletedLettings
      yield freshForm.fill(hasCompletedLettings.toAnswer)

    Ok(theView(filledForm.getOrElse(freshForm), previousRentalPeriod, backLinkUrl))
  }

  def submit: Action[AnyContent] = (Action andThen sessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      theForm,
      theFormWithErrors => successful(BadRequest(theView(theFormWithErrors, previousRentalPeriod, backLinkUrl))),
      answer =>
        given Session = request.sessionData
        for savedSession <- repository.saveOrUpdateSession(withCompletedLettings(answer.toBoolean))
        yield navigator.redirect(currentPage = CompletedLettingsPageId, savedSession)
    )
  }

  private def backLinkUrl(using request: SessionRequest[AnyContent]): Option[String] =
    navigator.backLinkUrl(ofPage = CompletedLettingsPageId)