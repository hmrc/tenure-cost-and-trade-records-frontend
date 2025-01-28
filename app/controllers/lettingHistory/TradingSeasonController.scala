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
import form.lettingHistory.TradingSeasonForm.theForm
import models.Session
import models.submissions.lettingHistory.LettingHistory.*
import models.submissions.lettingHistory.LocalPeriod
import navigation.LettingHistoryNavigator
import navigation.identifiers.TradingSeasonLengthPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import util.DateUtilLocalised
import views.html.lettingHistory.tradingSeason as TradingSeasonView

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future.successful
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TradingSeasonController @Inject (
  mcc: MessagesControllerComponents,
  dateUtil: DateUtilLocalised,
  navigator: LettingHistoryNavigator,
  theView: TradingSeasonView,
  sessionRefiner: WithSessionRefiner,
  @Named("session") repository: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport:

  given DateUtilLocalised = dateUtil

  def show: Action[AnyContent] = (Action andThen sessionRefiner).async { implicit request =>
    val freshForm  = theForm
    val filledForm =
      for
        intendedLettings    <- intendedLettings(request.sessionData)
        tradingSeasonLength <- intendedLettings.tradingSeason
      yield theForm.fill(tradingSeasonLength)

    Ok(theView(filledForm.getOrElse(freshForm), backLinkUrl))
  }

  def submit: Action[AnyContent] = (Action andThen sessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[LocalPeriod](
      theForm,
      theFormWithErrors => successful(BadRequest(theView(theFormWithErrors, backLinkUrl))),
      period =>
        given Session = request.sessionData
        for
          newSession   <- successful(withTradingPeriod(period))
          savedSession <- repository.saveOrUpdateSession(newSession)
        yield navigator.redirect(currentPage = TradingSeasonLengthPageId, savedSession)
    )
  }

  private def backLinkUrl(using request: SessionRequest[AnyContent]): Option[String] =
    navigator.backLinkUrl(ofPage = TradingSeasonLengthPageId)
