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
import form.lettingHistory.ResidentDetailForm.theForm
import models.Session
import models.submissions.lettingHistory.LettingHistory.{MaxNumberOfPermanentResidents, byAddingPermanentResident}
import models.submissions.lettingHistory.{LettingHistory, ResidentDetail}
import navigation.LettingHistoryNavigator
import navigation.identifiers.ResidentDetailPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.lettingHistory.residentDetail as ResidentDetailView

import javax.inject.{Inject, Named}
import scala.concurrent.Future.successful
import scala.concurrent.{ExecutionContext, Future}

class ResidentDetailController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: LettingHistoryNavigator,
  theView: ResidentDetailView,
  sessionRefiner: WithSessionRefiner,
  @Named("session") repository: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport:

  def show(maybeIndex: Option[Int] = None): Action[AnyContent] = (Action andThen sessionRefiner).apply {
    implicit request =>
      val permanentResidents = (
        for lettingHistory <- request.sessionData.lettingHistory.toList
        yield lettingHistory.permanentResidents
      ).flatten

      if maybeIndex.isEmpty && permanentResidents.size == MaxNumberOfPermanentResidents
      then Redirect(routes.ResidentListController.show)
      else
        val freshForm  = theForm
        val filledForm =
          for
            index          <- maybeIndex
            residentDetail <- permanentResidents.lift(index)
          yield freshForm.fill(residentDetail)

        Ok(theView(filledForm.getOrElse(freshForm), backLinkUrl))
  }

  def submit: Action[AnyContent] = (Action andThen sessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[ResidentDetail](
      theForm,
      theFormWithErrors => successful(BadRequest(theView(theFormWithErrors, backLinkUrl))),
      residentDetail =>
        given Session = request.sessionData
        for savedSession <- repository.saveOrUpdateSession(byAddingPermanentResident(residentDetail))
        yield navigator.redirect(currentPage = ResidentDetailPageId, savedSession)
    )
  }

  private def backLinkUrl(using request: SessionRequest[AnyContent]): Option[String] =
    navigator.backLinkUrl(ofPage = ResidentDetailPageId)
