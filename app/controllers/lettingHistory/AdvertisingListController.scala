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
import form.lettingHistory.AdvertisingListForm.theForm as theForm
import models.Session
import models.submissions.common.{AnswerYes, AnswersYesNo}
import models.submissions.lettingHistory.{AdvertisingOnline, LettingHistory}
import models.submissions.lettingHistory.LettingHistory.{byRemovingAdvertisingDetailsAt, toBoolean}
import navigation.LettingHistoryNavigator
import navigation.identifiers.{AdvertisingListPageId, AdvertisingRemovePageId}
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import views.html.genericRemoveConfirmation as RemoveConfirmationView
import views.html.lettingHistory.advertisingList as AdvertisingListView

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future.successful
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AdvertisingListController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: LettingHistoryNavigator,
  theView: AdvertisingListView,
  theConfirmationView: RemoveConfirmationView,
  sessionRefiner: WithSessionRefiner,
  @Named("session") repository: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport:

  def show: Action[AnyContent] = (Action andThen sessionRefiner).apply { implicit request =>
    Ok(theView(theForm, backLinkUrl))
  }

  def remove(index: Int): Action[AnyContent] = (Action andThen sessionRefiner).async { implicit request =>
    withAdvertisingDetailAt(index) { advertising =>
      successful(Ok(renderTheConfirmationViewWith(theRemoveConfirmationForm, advertising, index)))
    }
  }

  def performRemove(index: Int): Action[AnyContent] = (Action andThen sessionRefiner).async { implicit request =>
    withAdvertisingDetailAt(index) { advertising =>
      theRemoveConfirmationForm
        .bindFromRequest()
        .fold(
          formWithErrors => successful(BadRequest(renderTheConfirmationViewWith(formWithErrors, advertising, index))),
          answer =>
            val eventuallySavedSession =
              if answer == AnswerYes then
                given Session = request.sessionData
                for savedSession <- repository.saveOrUpdateSession(byRemovingAdvertisingDetailsAt(index))
                yield savedSession
              else successful(request.sessionData)

            for savedSession <- eventuallySavedSession
            yield navigator.redirect(currentPage = AdvertisingRemovePageId, savedSession)
        )
    }
  }

  def submit: Action[AnyContent] = (Action andThen sessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      theForm,
      theFormWithErrors =>
        successful(
          BadRequest(
            theView(theFormWithErrors, backLinkUrl)
          )
        ),
      answer =>
        successful(
          navigator.redirect(
            currentPage = AdvertisingListPageId,
            updatedSession = request.sessionData,
            navigationData = Map("hasMoreAdvertisingDetails" -> answer.toBoolean)
          )
        )
    )
  }

  private def withAdvertisingDetailAt(
    index: Int
  )(func: AdvertisingOnline => Future[Result])(using request: SessionRequest[AnyContent]): Future[Result] =
    LettingHistory
      .getAdvertisingOnlineDetails(request.sessionData)
      .lift(index)
      .fold(successful(Redirect(routes.AdvertisingListController.show))) { entry =>
        func.apply(entry)
      }

  private def renderTheConfirmationViewWith(theForm: Form[AnswersYesNo], advertising: AdvertisingOnline, index: Int)(
    using request: SessionRequest[AnyContent]
  ) =
    theConfirmationView(
      theForm,
      name = advertising.websiteAddress,
      sectionLabel = "label.section.lettingHistory",
      summary = request.sessionData.toSummary,
      idx = index,
      confirmAction = routes.AdvertisingListController.performRemove(index),
      cancelAction = routes.AdvertisingListController.show
    )

  private def backLinkUrl(using request: SessionRequest[AnyContent]): Option[String] =
    navigator.backLinkUrl(ofPage = AdvertisingListPageId)
