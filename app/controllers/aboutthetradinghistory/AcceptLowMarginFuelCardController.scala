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

package controllers.aboutthetradinghistory

import actions.{SessionRequest, WithSessionRefiner}
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutthetradinghistory.AcceptLowMarginFuelCardForm.acceptLowMarginFuelCardForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.common.{AnswerNo, AnswerYes, AnswersYesNo}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.AcceptLowMarginFuelCardsId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.acceptLowMarginFuelCard

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

/**
  * @author Yuriy Tumakha
  */
@Singleton
class AcceptLowMarginFuelCardController @Inject() (
  acceptLowMarginFuelCardView: acceptLowMarginFuelCard,
  audit: Audit,
  navigator: AboutTheTradingHistoryNavigator,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo,
  mcc: MessagesControllerComponents
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("AcceptLowMarginFuelCard")

    Ok(
      acceptLowMarginFuelCardView(
        tradingHistory
          .flatMap(_.doYouAcceptLowMarginFuelCard)
          .fold(acceptLowMarginFuelCardForm)(acceptLowMarginFuelCardForm.fill),
        getBackLink
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      acceptLowMarginFuelCardForm,
      formWithErrors => BadRequest(acceptLowMarginFuelCardView(formWithErrors, getBackLink)),
      data => {
        val updatedData = updateAboutTheTradingHistory(_.copy(doYouAcceptLowMarginFuelCard = Some(data)))

        session
          .saveOrUpdate(updatedData)
          .map { _ =>
            navigator.cyaPage
              .filter(_ =>
                navigator.from == "CYA" && (data == AnswerNo || tradingHistory
                  .flatMap(_.doYouAcceptLowMarginFuelCard)
                  .contains(AnswerYes))
              )
              .getOrElse(navigator.nextPage(AcceptLowMarginFuelCardsId, updatedData).apply(updatedData))
          }
          .map(Redirect)
      }
    )
  }

  private def tradingHistory(implicit
    request: SessionRequest[AnyContent]
  ): Option[AboutTheTradingHistory] = request.sessionData.aboutTheTradingHistory

  private def getBackLink: String =
    controllers.aboutthetradinghistory.routes.CustomerCreditAccountsController.show().url

}
