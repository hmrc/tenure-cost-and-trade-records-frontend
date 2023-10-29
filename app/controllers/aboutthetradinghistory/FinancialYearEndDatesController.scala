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
import models.submissions.aboutthetradinghistory.{CostOfSales, TurnoverSection}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.FinancialYearEndDatesPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.financialYearEndDates

import java.time.LocalDate
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FinancialYearEndDatesController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  financialYearEndDatesView: financialYearEndDates,
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
          financialYearEndDatesView(
            turnoverForm(numberOfColumns).fill(aboutTheTradingHistory.turnoverSections),
            numberOfColumns
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
          formWithErrors => BadRequest(financialYearEndDatesView(formWithErrors, numberOfColumns)),
          success => {
            val previousFinancialYearsList = request.sessionData.aboutTheTradingHistory
              .fold(Seq.empty[LocalDate])(_.turnoverSections.map(_.financialYearEnd))

            val newFinancialYearsList = success.map(_.financialYearEnd)

            if (newFinancialYearsList.equals(previousFinancialYearsList)) {
              Redirect(navigator.nextPage(FinancialYearEndDatesPageId, request.sessionData).apply(request.sessionData))
            } else {
              val updatedData = updateAboutTheTradingHistory(
                _.copy(
                  turnoverSections = newFinancialYearsList.map { finYearEnd =>
                    TurnoverSection(
                      financialYearEnd = finYearEnd,
                      tradingPeriod = 52,
                      alcoholicDrinks = None,
                      food = None,
                      otherReceipts = None,
                      accommodation = None,
                      averageOccupancyRate = None
                    )
                  },
                  costOfSales = newFinancialYearsList.map(CostOfSales(_, None, None, None, None))
                )
              )
              session
                .saveOrUpdate(updatedData)
                .map(_ => Redirect(navigator.nextPage(FinancialYearEndDatesPageId, updatedData).apply(updatedData)))
            }
          }
        )
      }
  }

}
