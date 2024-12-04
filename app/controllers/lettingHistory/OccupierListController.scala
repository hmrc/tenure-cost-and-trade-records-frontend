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
import form.lettingHistory.OccupierListForm.theForm as theListForm
import models.Session
import models.submissions.common.{AnswerYes, AnswersYesNo}
import models.submissions.lettingHistory.LettingHistory.*
import models.submissions.lettingHistory.{LettingHistory, OccupierDetail}
import navigation.LettingHistoryNavigator
import navigation.identifiers.{OccupierListPageId, OccupierRemovePageId}
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import views.html.genericRemoveConfirmation as RemoveConfirmationView
import views.html.lettingHistory.occupierList as OccupierListView

import javax.inject.{Inject, Named}
import scala.concurrent.Future.successful
import scala.concurrent.{ExecutionContext, Future}

class OccupierListController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: LettingHistoryNavigator,
  theListView: OccupierListView,
  theConfirmationView: RemoveConfirmationView,
  sessionRefiner: WithSessionRefiner,
  @Named("session") repository: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with RentalPeriodSupport
    with I18nSupport:

  def show: Action[AnyContent] = (Action andThen sessionRefiner).apply { implicit request =>
    Ok(theListView(theListForm, previousRentalPeriod, completedLettings(request.sessionData), backLinkUrl))
  }

  def remove(index: Int): Action[AnyContent] = (Action andThen sessionRefiner).async { implicit request =>
    withOccupierDetailAt(index) { occupierDetails =>
      successful(Ok(renderTheConfirmationViewWith(theRemoveConfirmationForm, occupierDetails, index)))
    }
  }

  def performRemove(index: Int): Action[AnyContent] = (Action andThen sessionRefiner).async { implicit request =>
    withOccupierDetailAt(index) { occupierDetail =>
      theRemoveConfirmationForm
        .bindFromRequest()
        .fold(
          formWithErrors =>
            successful(BadRequest(renderTheConfirmationViewWith(formWithErrors, occupierDetail, index))),
          answer =>
            val eventuallySavedSession =
              if answer == AnswerYes then
                given Session = request.sessionData
                for savedSession <- repository.saveOrUpdateSession(byRemovingPermanentOccupierAt(index))
                yield savedSession
              else
                // AnswerNo
                successful(request.sessionData)

            for savedSession <- eventuallySavedSession
            yield navigator.redirect(currentPage = OccupierRemovePageId, savedSession)
        )
    }
  }

  def submit: Action[AnyContent] = (Action andThen sessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      theListForm,
      theFormWithErrors =>
        successful(
          BadRequest(
            theListView(theFormWithErrors, previousRentalPeriod, completedLettings(request.sessionData), backLinkUrl)
          )
        ),
      hadMoreOccupiers =>
        successful(
          navigator.redirect(
            currentPage = OccupierListPageId,
            updatedSession = request.sessionData,
            navigationData = Map("hadMoreOccupiers" -> hadMoreOccupiers.toBoolean.toString)
          )
        )
    )
  }

  private def renderTheConfirmationViewWith(theForm: Form[AnswersYesNo], occupierDetail: OccupierDetail, index: Int)(
    using request: SessionRequest[AnyContent]
  ) =
    theConfirmationView(
      theForm,
      name = occupierDetail.name,
      sectionLabel = "label.section.lettingHistory",
      summary = request.sessionData.toSummary,
      idx = index,
      confirmAction = routes.OccupierListController.performRemove(index),
      cancelAction = routes.OccupierListController.show // backLinkUrl
    )

  private def withOccupierDetailAt(
    index: Int
  )(func: OccupierDetail => Future[Result])(using request: SessionRequest[AnyContent]): Future[Result] =
    LettingHistory
      .completedLettings(request.sessionData)
      .lift(index)
      .fold(successful(Redirect(routes.OccupierListController.show))) { occupierDetail =>
        func.apply(occupierDetail)
      }

  private def backLinkUrl(using request: SessionRequest[AnyContent]): Option[String] =
    navigator.backLinkUrl(ofPage = OccupierListPageId)