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
import form.lettingHistory.HasOnlineAdvertisingForm.theForm
import models.Session
import models.submissions.common.AnswersYesNo
import models.submissions.common.AnswersYesNo.*
import models.submissions.lettingHistory.LettingHistory.withHasOnlineAdvertising
import navigation.LettingHistoryNavigator
import navigation.identifiers.HasOnlineAdvertisingPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.lettingHistory.hasOnlineAdvertising as HasOnlineAdvertisingView

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext
import scala.concurrent.Future.successful

@Singleton
class HasOnlineAdvertisingController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: LettingHistoryNavigator,
  theView: HasOnlineAdvertisingView,
  sessionRefiner: WithSessionRefiner,
  @Named("session") repository: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen sessionRefiner).apply { implicit request =>
    val filledForm =
      for
        lettingHistory      <- request.sessionData.lettingHistory
        advertisingQuestion <- lettingHistory.hasOnlineAdvertising
      yield theForm.fill(advertisingQuestion.toAnswer)

    Ok(theView(filledForm.getOrElse(theForm), backLinkUrl))
  }

  def submit: Action[AnyContent] = (Action andThen sessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      theForm,
      theFormWithErrors => successful(BadRequest(theView(theFormWithErrors, backLinkUrl))),
      answer =>
        given Session = request.sessionData
        for
          newSession   <- successful(withHasOnlineAdvertising(answer.toBoolean))
          savedSession <- repository.saveOrUpdateSession(newSession)
        yield navigator.redirect(currentPage = HasOnlineAdvertisingPageId, savedSession)
    )
  }

  private def backLinkUrl(using request: SessionRequest[AnyContent]): Option[String] =
    navigator.backLinkUrl(ofPage = HasOnlineAdvertisingPageId)

}
