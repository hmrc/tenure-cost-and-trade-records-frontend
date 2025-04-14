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
import form.lettingHistory.IsYearlyAvailableForm.theForm
import models.Session
import models.submissions.common.AnswersYesNo
import models.submissions.common.AnswersYesNo.*
import models.submissions.lettingHistory.LettingHistory.*
import navigation.LettingHistoryNavigator
import navigation.identifiers.*
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.lettingHistory.isYearlyAvailable as IsYearlyAvailableView

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext
import scala.concurrent.Future.successful

@Singleton
class IsYearlyAvailableController @Inject (
  mcc: MessagesControllerComponents,
  navigator: LettingHistoryNavigator,
  theView: IsYearlyAvailableView,
  sessionRefiner: WithSessionRefiner,
  @Named("session") repository: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport:

  def show: Action[AnyContent] = (Action andThen sessionRefiner).apply { implicit request =>
    val freshForm  = theForm
    val filledForm =
      for
        lettingHistory    <- request.sessionData.lettingHistory
        intendedLettings  <- lettingHistory.intendedLettings
        isYearlyAvailable <- intendedLettings.isYearlyAvailable
      yield freshForm.fill(isYearlyAvailable.toAnswer)

    Ok(theView(filledForm.getOrElse(freshForm), keyFragment, backLinkUrl))
  }

  def submit: Action[AnyContent] = (Action andThen sessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      theForm,
      theFormWithErrors => successful(BadRequest(theView(theFormWithErrors, keyFragment, backLinkUrl))),
      answer =>
        given Session = request.sessionData
        for
          newSession   <- withIsYearlyAvailable(answer.toBoolean)
          savedSession <- repository.saveOrUpdateSession(newSession)
        yield navigator.redirect(currentPage = IsYearlyAvailablePageId, savedSession)
    )
  }

  private def keyFragment(using request: SessionRequest[AnyContent]): String =
    val fragment = for
      intention  <- intendedLettings(request.sessionData)
      hasStopped <- intention.hasStopped
    yield
      if (hasStopped) "hasStoppedLetting"
      else "eitherMeetsCriteriaOrHasNotStopped"
    fragment.getOrElse("eitherMeetsCriteriaOrHasNotStopped")

  private def backLinkUrl(using request: SessionRequest[AnyContent]): Option[String] =
    navigator.backLinkUrl(ofPage = IsYearlyAvailablePageId)
