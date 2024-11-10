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

import actions.WithSessionRefiner
import controllers.FORDataCaptureController
import form.lettingHistory.PermanentResidentsForm.theForm
import models.Session
import models.submissions.common.AnswersYesNo
import models.submissions.lettingHistory.LettingHistory
import models.submissions.lettingHistory.LettingHistory.sessionWithPermanentResidents
import navigation.LettingHistoryNavigator
import navigation.identifiers.PermanentResidentsPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.lettingHistory.permanentResidents as PermanentResidentsView

import javax.inject.{Inject, Named}
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.Future.successful

class PermanentResidentsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: LettingHistoryNavigator,
  theView: PermanentResidentsView,
  sessionRefiner: WithSessionRefiner,
  @Named("session") repository: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport:

  def show: Action[AnyContent] = (Action andThen sessionRefiner).apply { implicit request =>
    val freshForm = theForm
    val staleForm =
      for lettingHistory <- request.sessionData.lettingHistory
      yield freshForm.fill(lettingHistory.isPermanentResidence)

    Ok(theView(staleForm.getOrElse(freshForm)))
  }

  def submit: Action[AnyContent] = (Action andThen sessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      theForm,
      theFormWithErrors => successful(BadRequest(theView(theFormWithErrors))),
      isPermanentResidence =>
        given Session = request.sessionData
        for updatedSession <- repository.saveOrUpdateSession(sessionWithPermanentResidents(isPermanentResidence))
        yield Redirect(navigator.nextCall(PermanentResidentsPageId, updatedSession))
    )
  }