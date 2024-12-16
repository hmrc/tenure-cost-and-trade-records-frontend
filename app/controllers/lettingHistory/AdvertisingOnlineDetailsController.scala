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
import form.lettingHistory.AdvertisingOnlineDetailsForm.theForm
import models.Session
import models.submissions.lettingHistory.LettingHistory.{MaxNumberOfAdvertisingOnline, byAddingAdvertisingOnlineDetails, byAddingPermanentResident}
import models.submissions.lettingHistory.{AdvertisingOnline, ResidentDetail}
import navigation.LettingHistoryNavigator
import navigation.identifiers.{AdvertisingOnlineDetailsPageId, ResidentDetailPageId}
import play.api.i18n.I18nSupport
import play.api.mvc.*
import repositories.SessionRepo
import views.html.lettingHistory.advertisingOnlineDetails as AdvertisingOnlineDetails

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext
import scala.concurrent.Future.successful

@Singleton
class AdvertisingOnlineDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: LettingHistoryNavigator,
  theView: AdvertisingOnlineDetails,
  sessionRefiner: WithSessionRefiner,
  @Named("session") repository: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport:

  def show(index: Option[Int] = None): Action[AnyContent] = (Action andThen sessionRefiner).apply { implicit request =>
    val advertisingOnlineList = (
      for lettingHistory <- request.sessionData.lettingHistory.toList
      yield lettingHistory.advertisingOnlineDetails
    ).flatten

    if index.isEmpty && advertisingOnlineList.sizeIs == MaxNumberOfAdvertisingOnline
    then Redirect(controllers.routes.TaskListController.show())
    else
      val freshForm  = theForm
      val filledForm =
        for
          idx          <- index
          detailsOnIdx <- advertisingOnlineList.lift(idx)
        yield theForm.fill(detailsOnIdx)

      Ok(theView(filledForm.getOrElse(theForm), backLinkUrl, request.sessionData.toSummary))
  }

  def submit: Action[AnyContent] = (Action andThen sessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AdvertisingOnline](
      theForm,
      theFormWithErrors =>
        successful(BadRequest(theView(theFormWithErrors, backLinkUrl, request.sessionData.toSummary))),
      details =>
        given Session = request.sessionData
        for savedSession <- repository.saveOrUpdateSession(byAddingAdvertisingOnlineDetails(details))
        yield navigator.redirect(currentPage = AdvertisingOnlineDetailsPageId, savedSession)
    )
  }

  private def backLinkUrl(using request: SessionRequest[AnyContent]): Option[String] =
    navigator.backLinkUrl(ofPage = AdvertisingOnlineDetailsPageId)
