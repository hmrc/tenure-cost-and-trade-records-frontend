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
import form.lettingHistory.MaxNumberReachedForm.theForm
import models.Session
import models.submissions.lettingHistory.LettingHistory.withMayHaveMoreOf
import navigation.LettingHistoryNavigator
import navigation.identifiers.MaxNumberReachedPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.lettingHistory.maxNumberReached as MaxNumberReachedView

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext
import scala.concurrent.Future.successful

@Singleton
class MaxNumberReachedController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: LettingHistoryNavigator,
  theView: MaxNumberReachedView,
  sessionRefiner: WithSessionRefiner,
  @Named("session") repository: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport:

  def show(kind: String): Action[AnyContent] = (Action andThen sessionRefiner).async { implicit request =>
    val freshForm      = theForm
    val filledForm     = hasEvenMoreEntries(kind).map(bool => freshForm.fill(bool))
    val alreadyChecked = filledForm.map(_.data("understood").toBoolean).getOrElse(false)
    successful(Ok(theView(filledForm.getOrElse(freshForm), alreadyChecked, kind, backLinkUrl(kind))))
  }

  def submit(kind: String): Action[AnyContent] = (Action andThen sessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[Boolean](
      theForm,
      theFormWithErrors =>
        successful(BadRequest(theView(theFormWithErrors, alreadyChecked = false, kind, backLinkUrl(kind)))),
      understand => {
        given Session = request.sessionData
        for
          newSession    <- withMayHaveMoreOf(kind, understand)
          savedSession  <- repository.saveOrUpdateSession(newSession)
          navigationData = Map("kind" -> kind)
        yield navigator.redirect(currentPage = MaxNumberReachedPageId, savedSession, navigationData)
      }
    )
  }

  private def backLinkUrl(kind: String)(using request: SessionRequest[AnyContent]): Option[String] =
    val navigationData = Map("kind" -> kind)
    navigator.backLinkUrl(ofPage = MaxNumberReachedPageId, navigationData)

  private def hasEvenMoreEntries(kind: String)(using request: SessionRequest[AnyContent]): Option[Boolean] =
    request.sessionData.lettingHistory.flatMap { lettingHistory =>
      kind match {
        case "permanentResidents" => lettingHistory.mayHaveMorePermanentResidents
        case "temporaryOccupiers" => lettingHistory.mayHaveMoreCompletedLettings
        case "onlineAdvertising"  => lettingHistory.mayHaveMoreOnlineAdvertising
        case _                    => None
      }
    }
