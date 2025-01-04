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
import form.lettingHistory.AdvertisingOnlineForm.theForm
import models.Session
import models.submissions.common.AnswersYesNo
import models.submissions.lettingHistory.LettingHistory.{toAnswer, toBoolean, withAdvertisingOnline}
import navigation.LettingHistoryNavigator
import navigation.identifiers.AdvertisingOnlinePageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.lettingHistory.advertisingOnline

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext
import scala.concurrent.Future.successful

@Singleton
class AdvertisingOnlineController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: LettingHistoryNavigator,
  theView: advertisingOnline,
  sessionRefiner: WithSessionRefiner,
  @Named("session") repository: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen sessionRefiner).apply { implicit request =>
    val filledForm =
      for
        lettingHistory      <- request.sessionData.lettingHistory
        advertisingQuestion <- lettingHistory.advertisingOnline
      yield theForm.fill(advertisingQuestion.toAnswer)

    Ok(
      theView(
        filledForm.getOrElse(theForm),
        request.sessionData.toSummary,
        backLinkUrl
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen sessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      theForm,
      theFormWithErrors =>
        successful(BadRequest(theView(theFormWithErrors, request.sessionData.toSummary, backLinkUrl))),
      answer =>
        given Session = request.sessionData
        for savedSession <- repository.saveOrUpdateSession(withAdvertisingOnline(answer.toBoolean))
        yield navigator.redirect(currentPage = AdvertisingOnlinePageId, savedSession)
    )
  }

  private def backLinkUrl(using request: SessionRequest[AnyContent]): String =
    navigator.backLinkUrl(ofPage = AdvertisingOnlinePageId).getOrElse("")

}
