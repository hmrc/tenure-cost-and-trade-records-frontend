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
import form.lettingHistory.WhenWasLastLetForm.theForm
import models.Session
import models.submissions.lettingHistory.LettingHistory
import models.submissions.lettingHistory.LettingHistory.*
import navigation.LettingHistoryNavigator
import navigation.identifiers.WhenWasLastLetPageId
import play.api.i18n.I18nSupport
import play.api.libs.json.Writes
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.http.HeaderCarrier
import util.DateUtilLocalised
import views.html.lettingHistory.whenWasLastLet as WhenWasLastLetView

import java.time.LocalDate
import javax.inject.{Inject, Named}
import scala.concurrent.Future.successful
import scala.concurrent.{ExecutionContext, Future}

class WhenWasLastLetController @Inject (
  mcc: MessagesControllerComponents,
  dateUtil: DateUtilLocalised,
  navigator: LettingHistoryNavigator,
  theView: WhenWasLastLetView,
  sessionRefiner: WithSessionRefiner,
  @Named("session") repository: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport:

  given DateUtilLocalised = dateUtil

  def show: Action[AnyContent] = (Action andThen sessionRefiner).apply { implicit request =>
    val freshForm  = theForm
    val filledForm =
      for
        lettingHistory   <- request.sessionData.lettingHistory
        intendedLettings <- lettingHistory.intendedLettings
        whenWasLastLet   <- intendedLettings.whenWasLastLet
      yield theForm.fill(whenWasLastLet)

    Ok(theView(filledForm.getOrElse(freshForm), backLinkUrl))
  }

  def submit: Action[AnyContent] = (Action andThen sessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[LocalDate](
      theForm,
      theFormWithErrors => successful(BadRequest(theView(theFormWithErrors, backLinkUrl))),
      date =>
        for savedSession <- saveOrUpdateWithWhenWasLastLet(Some(date), request.sessionData)
        yield navigator.redirect(currentPage = WhenWasLastLetPageId, savedSession)
    )
  }

  private def saveOrUpdateWithWhenWasLastLet(
    date: Option[LocalDate],
    session: Session
  )(using ws: Writes[Session], hc: HeaderCarrier, ec: ExecutionContext): Future[Session] =
    given Session = session
    repository.saveOrUpdateSession(withWhenWasLastLet(date))

  private def backLinkUrl(using request: SessionRequest[AnyContent]): Option[String] =
    navigator.backLinkUrl(ofPage = WhenWasLastLetPageId)
