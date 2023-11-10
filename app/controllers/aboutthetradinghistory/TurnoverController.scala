/*
 * Copyright 2023 HM Revenue & Customs
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

import actions.WithSessionRefiner
import controllers.FORDataCaptureController
import form.aboutthetradinghistory.TurnoverForm.turnoverForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistory, TurnoverSection}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.TurnoverPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.turnover

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TurnoverController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  turnoverView: turnover,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    request.sessionData.aboutTheTradingHistory
      .filter(_.occupationAndAccountingInformation.isDefined)
      .fold(Redirect(routes.AboutYourTradingHistoryController.show())) { aboutTheTradingHistory =>
        val numberOfColumns = aboutTheTradingHistory.turnoverSections.size
        Ok(
          turnoverView(
            turnoverForm(numberOfColumns).fill(aboutTheTradingHistory.turnoverSections),
            request.sessionData.forType
          )
        )
      }
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    request.sessionData.aboutTheTradingHistory
      .filter(_.occupationAndAccountingInformation.isDefined)
      .fold(Future.successful(Redirect(routes.AboutYourTradingHistoryController.show()))) { aboutTheTradingHistory =>
        val numberOfColumns = aboutTheTradingHistory.turnoverSections.size
        continueOrSaveAsDraft[Seq[TurnoverSection]](
          turnoverForm(numberOfColumns),
          formWithErrors => BadRequest(turnoverView(formWithErrors, request.sessionData.forType)),
          success => {
            val turnoverSections =
              (success zip financialYearEndDates(aboutTheTradingHistory)).map { case (turnoverSection, finYearEnd) =>
                turnoverSection.copy(financialYearEnd = finYearEnd)
              }

            val updatedData = updateAboutTheTradingHistory(
              _.copy(
                turnoverSections = turnoverSections
              )
            )
            session
              .saveOrUpdate(updatedData)
              .map(_ =>
                navigator.cyaPage
                  .filter(_ => navigator.from == "CYA" && aboutTheTradingHistory.costOfSales.head.drinks.isDefined)
                  .getOrElse(navigator.nextPage(TurnoverPageId, updatedData).apply(updatedData))
              )
              .map(Redirect)
          }
        )
      }
  }

  private def financialYearEndDates(aboutTheTradingHistory: AboutTheTradingHistory) =
    aboutTheTradingHistory.turnoverSections.map(_.financialYearEnd)

}
