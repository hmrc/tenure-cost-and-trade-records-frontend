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
import form.lettingHistory.OccupierDetailForm.theForm
import models.Session
import models.submissions.lettingHistory.LettingHistory.{MaxNumberOfCompletedLettings, byAddingOrUpdatingTemporaryOccupier}
import models.submissions.lettingHistory.OccupierDetail
import navigation.LettingHistoryNavigator
import navigation.identifiers.OccupierDetailPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.lettingHistory.occupierDetail as OccupierDetailView

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext
import scala.concurrent.Future.successful

@Singleton
class OccupierDetailController @Inject (
  mcc: MessagesControllerComponents,
  navigator: LettingHistoryNavigator,
  theView: OccupierDetailView,
  sessionRefiner: WithSessionRefiner,
  @Named("session") repository: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with RentalPeriodSupport
    with I18nSupport:

  def show(maybeIndex: Option[Int] = None): Action[AnyContent] = (Action andThen sessionRefiner).apply {
    implicit request =>
      val completedLettings = (
        for lettingHistory <- request.sessionData.lettingHistory.toList
        yield lettingHistory.completedLettings
      ).flatten

      if maybeIndex.isEmpty && completedLettings.size == MaxNumberOfCompletedLettings
      then Redirect(routes.OccupierListController.show)
      else
        val freshForm  = theForm
        val filledForm =
          for
            index          <- maybeIndex
            occupierDetail <- completedLettings.lift(index)
          yield freshForm.fill(occupierDetail)

        Ok(theView(filledForm.getOrElse(freshForm), previousRentalPeriod, backLinkUrl))
  }

  def submit: Action[AnyContent] = (Action andThen sessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[OccupierDetail](
      theForm,
      theFormWithErrors => successful(BadRequest(theView(theFormWithErrors, previousRentalPeriod, backLinkUrl))),
      occupierDetail =>
        given Session                       = request.sessionData
        val (occupierIndex, updatedSession) =
          byAddingOrUpdatingTemporaryOccupier(occupierDetail.name, occupierDetail.address)
        for
          savedSession  <- repository.saveOrUpdateSession(updatedSession)
          navigationData = Map("index" -> occupierIndex)
        yield navigator.redirect(currentPage = OccupierDetailPageId, savedSession, navigationData)
    )
  }

  private def backLinkUrl(using request: SessionRequest[AnyContent]): Option[String] =
    navigator.backLinkUrl(ofPage = OccupierDetailPageId)
