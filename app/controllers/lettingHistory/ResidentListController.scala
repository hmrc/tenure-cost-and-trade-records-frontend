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
import form.confirmableActionForm.confirmableActionForm as theRemoveConfirmationForm
import form.lettingHistory.ResidentListForm.theForm as theListForm
import models.Session
import models.submissions.common.{AnswerYes, AnswersYesNo}
import models.submissions.lettingHistory.LettingHistory.sessionByRemovingPermanentResidentAt
import models.submissions.lettingHistory.{LettingHistory, ResidentDetail}
import navigation.LettingHistoryNavigator
import navigation.identifiers.ResidentListPageId
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import views.html.genericRemoveConfirmation as RemoveConfirmationView
import views.html.lettingHistory.residentList as ResidentListView

import javax.inject.{Inject, Named}
import scala.concurrent.Future.successful
import scala.concurrent.{ExecutionContext, Future}

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
    Ok(theListView(theListForm, backLinkUrl, LettingHistory.permanentResidents(request.sessionData)))
  }

  def remove(index: Int): Action[AnyContent] = (Action andThen sessionRefiner).async { implicit request =>
    this.foldResidentDetailAt(index) { residentialDetail =>
      successful(Ok(renderTheConfirmationViewWith(theRemoveConfirmationForm, residentialDetail, index)))
    }
  }

  def performRemove(index: Int): Action[AnyContent] = (Action andThen sessionRefiner).async { implicit request =>
    this.foldResidentDetailAt(index) { residentialDetail =>
      theRemoveConfirmationForm
        .bindFromRequest()
        .fold(
          formWithErrors =>
            successful(BadRequest(renderTheConfirmationViewWith(formWithErrors, residentialDetail, index))),
          answer =>
            if answer == AnswerYes then
              given Session = request.sessionData
              for updatedSession <- repository.saveOrUpdateSession(sessionByRemovingPermanentResidentAt(index))
              yield Redirect(routes.ResidentListController.show)
            else
              // AnswerNo
              successful(Redirect(routes.ResidentListController.show))
        )
    }
  }

  def submit: Action[AnyContent] = (Action andThen sessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      theListForm,
      theFormWithErrors =>
        successful(
          BadRequest(
            theListView(theFormWithErrors, backLinkUrl, LettingHistory.permanentResidents(request.sessionData))
          )
        ),
      hasMoreResidents =>
        successful(
          navigator.redirect(
            fromPage = ResidentListPageId,
            updatedSession = request.sessionData,
            navigationData = Map("hasMoreResidents" -> hasMoreResidents.toString)
          )
        )
    )
  }

  private def foldResidentDetailAt(
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
      sectionLabel = "label.section.lettingHistory",
      summary = request.sessionData.toSummary,
      idx = index,
      confirmAction = routes.ResidentListController.performRemove(index),
      cancelAction = routes.ResidentListController.show // backLinkUrl
    )

  private def backLinkUrl(using request: SessionRequest[AnyContent]): Option[String] =
    navigator.backLinkUrl(ofPage = ResidentListPageId)
