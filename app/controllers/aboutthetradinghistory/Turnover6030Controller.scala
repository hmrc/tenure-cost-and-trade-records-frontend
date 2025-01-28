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
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutthetradinghistory.TurnoverForm6030.turnoverForm6030
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistory, TurnoverSection6030}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.TurnoverPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.turnover6030

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class Turnover6030Controller @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutTheTradingHistoryNavigator,
  turnoverView: turnover6030,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    audit.sendChangeLink("Turnover6030")
    request.sessionData.aboutTheTradingHistory
      .filter(_.occupationAndAccountingInformation.isDefined)
      .fold(Redirect(routes.AboutYourTradingHistoryController.show())) { aboutTheTradingHistory =>
        val numberOfColumns = aboutTheTradingHistory.turnoverSections6030.size

        Ok(
          turnoverView(
            turnoverForm6030(numberOfColumns, financialYearEndDates(aboutTheTradingHistory))
              .fill(aboutTheTradingHistory.turnoverSections6030),
            navigator.from
          )
        )
      }
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    request.sessionData.aboutTheTradingHistory
      .filter(_.occupationAndAccountingInformation.isDefined)
      .fold(Future.successful(Redirect(routes.AboutYourTradingHistoryController.show()))) { aboutTheTradingHistory =>
        val numberOfColumns = aboutTheTradingHistory.turnoverSections6030.size
        continueOrSaveAsDraft[Seq[TurnoverSection6030]](
          turnoverForm6030(numberOfColumns, financialYearEndDates(aboutTheTradingHistory)),
          formWithErrors => BadRequest(turnoverView(formWithErrors)),
          success => {
            val turnoverSections6030 =
              (success zip financialYearEndDates(aboutTheTradingHistory)).map {
                case (turnoverSection6030, finYearEnd) =>
                  turnoverSection6030.copy(financialYearEnd = finYearEnd)
              }

            val updatedData = updateAboutTheTradingHistory(
              _.copy(
                turnoverSections6030 = turnoverSections6030
              )
            )
            session
              .saveOrUpdate(updatedData)
              .map { _ =>
                navigator.cyaPage
                  .filter(_ => navigator.from == "CYA" && aboutTheTradingHistory.unusualCircumstances.isDefined)
                  .getOrElse(navigator.nextPage(TurnoverPageId, updatedData).apply(updatedData))
              }
              .map(Redirect)
          }
        )
      }
  }

  private def financialYearEndDates(aboutTheTradingHistory: AboutTheTradingHistory) =
    aboutTheTradingHistory.turnoverSections6030.map(_.financialYearEnd)
}
