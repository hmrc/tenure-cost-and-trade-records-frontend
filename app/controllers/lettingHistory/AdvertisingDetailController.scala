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
import form.lettingHistory.AdvertisingDetailForm.theForm
import models.Session
import models.submissions.lettingHistory.LettingHistory.{MaxNumberOfOnlineAdvertising, byAddingOrUpdatingOnlineAdvertising}
import models.submissions.lettingHistory.AdvertisingDetail
import navigation.LettingHistoryNavigator
import navigation.identifiers.AdvertisingDetailPageId
import play.api.i18n.I18nSupport
import play.api.mvc.*
import repositories.SessionRepo
import views.html.lettingHistory.advertisingDetail as OnlineAdvertisingDetailView

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext
import scala.concurrent.Future.successful

@Singleton
class AdvertisingDetailController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: LettingHistoryNavigator,
  theView: OnlineAdvertisingDetailView,
  sessionRefiner: WithSessionRefiner,
  @Named("session") repository: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport:

  def show(index: Option[Int] = None): Action[AnyContent] = (Action andThen sessionRefiner).apply { implicit request =>
    val onlineAdvertising = (
      for lettingHistory <- request.sessionData.lettingHistory.toList
      yield lettingHistory.onlineAdvertising
    ).flatten

    if index.isEmpty && onlineAdvertising.sizeIs == MaxNumberOfOnlineAdvertising
    then Redirect(controllers.routes.TaskListController.show())
    else
      val filledForm =
        for
          idx          <- index
          detailsOnIdx <- onlineAdvertising.lift(idx)
        yield theForm.fill(detailsOnIdx)

      Ok(theView(filledForm.getOrElse(theForm), backLinkUrl, index, request.sessionData.toSummary))
  }

  def submit(index: Option[Int]): Action[AnyContent] = (Action andThen sessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AdvertisingDetail](
      theForm,
      theFormWithErrors =>
        successful(BadRequest(theView(theFormWithErrors, backLinkUrl, index, request.sessionData.toSummary))),
      details =>
        given Session = request.sessionData
        for
          newSession   <- byAddingOrUpdatingOnlineAdvertising(index, details)
          savedSession <- repository.saveOrUpdateSession(newSession)
        yield navigator.redirect(currentPage = AdvertisingDetailPageId, savedSession)
    )
  }

  private def backLinkUrl(using request: SessionRequest[AnyContent]): Option[String] =
    navigator.backLinkUrl(ofPage = AdvertisingDetailPageId)
