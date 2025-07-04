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
import form.confirmableActionForm.confirmableActionForm as theRemoveConfirmationForm
import form.lettingHistory.ResidentListForm.theForm as theListForm
import models.Session
import models.submissions.common.AnswersYesNo
import models.submissions.common.AnswersYesNo.*
import models.submissions.lettingHistory.LettingHistory.*
import models.submissions.lettingHistory.{LettingHistory, ResidentDetail, SessionWrapper}
import navigation.LettingHistoryNavigator
import navigation.identifiers.{ResidentListPageId, ResidentRemovePageId}
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import uk.gov.hmrc.http.HeaderCarrier
import views.html.genericRemoveConfirmation as RemoveConfirmationView
import views.html.lettingHistory.residentList as ResidentListView

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future.successful
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ResidentListController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: LettingHistoryNavigator,
  theListView: ResidentListView,
  theConfirmationView: RemoveConfirmationView,
  sessionRefiner: WithSessionRefiner,
  @Named("session") repository: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport:

  def show: Action[AnyContent] = (Action andThen sessionRefiner).apply { implicit request =>
    Ok(theListView(theListForm, permanentResidents(request.sessionData)))
  }

  def remove(index: Int): Action[AnyContent] = (Action andThen sessionRefiner).async { implicit request =>
    withResidentDetailAt(index) { residentialDetail =>
      successful(Ok(renderTheConfirmationViewWith(theRemoveConfirmationForm, residentialDetail, index)))
    }
  }

  def performRemove(index: Int): Action[AnyContent] = (Action andThen sessionRefiner).async { implicit request =>
    withResidentDetailAt(index) { residentialDetail =>
      theRemoveConfirmationForm
        .bindFromRequest()
        .fold(
          formWithErrors =>
            successful(BadRequest(renderTheConfirmationViewWith(formWithErrors, residentialDetail, index))),
          answer =>
            val eventuallySavedSession =
              if answer == AnswerYes then
                given Session = request.sessionData
                for
                  newSession   <- byRemovingPermanentResidentAt(index)
                  savedSession <- repository.saveOrUpdateSession(newSession)
                yield savedSession
              else
                // AnswerNo
                successful(request.sessionData.withChangedData(true))

            for savedSession <- eventuallySavedSession
            yield navigator.redirect(currentPage = ResidentRemovePageId, savedSession)
        )
    }
  }

  def submit: Action[AnyContent] = (Action andThen sessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      theListForm,
      theFormWithErrors =>
        successful(
          BadRequest(
            theListView(theFormWithErrors, permanentResidents(request.sessionData))
          )
        ),
      answer =>
        val answerBool = answer.toBoolean
        given Session  = request.sessionData
        for
          savedSession  <- eventuallySaveOrUpdateSessionWith(hasMoreResidents = answerBool)
          navigationData = Map("hasMoreResidents" -> answerBool)
        yield navigator.redirect(currentPage = ResidentListPageId, savedSession, navigationData)
    )
  }

  private def eventuallySaveOrUpdateSessionWith(
    hasMoreResidents: Boolean
  )(using session: Session, hc: HeaderCarrier, ec: ExecutionContext): Future[SessionWrapper] =
    if !hasMoreResidents && permanentResidents(session).isEmpty
    then {
      val newSession = withHasPermanentResidents(false)
      repository.saveOrUpdateSession(newSession)
    } else successful(session.withChangedData(false))

  private def withResidentDetailAt(
    index: Int
  )(func: ResidentDetail => Future[Result])(using request: SessionRequest[AnyContent]): Future[Result] =
    LettingHistory
      .permanentResidents(request.sessionData)
      .lift(index)
      .fold(successful(Redirect(routes.ResidentListController.show))) { residentialDetail =>
        func.apply(residentialDetail)
      }

  private def renderTheConfirmationViewWith(theForm: Form[AnswersYesNo], residentialDetail: ResidentDetail, index: Int)(
    using request: SessionRequest[AnyContent]
  ) =
    theConfirmationView(
      theForm,
      name = residentialDetail.name,
      confirmAction = routes.ResidentListController.performRemove(index),
      cancelAction = routes.ResidentListController.show
    )
