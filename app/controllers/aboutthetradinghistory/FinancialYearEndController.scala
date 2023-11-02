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
import form.aboutthetradinghistory.AccountingInformationForm.accountingInformationForm
import models.submissions.Form6010.DayMonthsDuration
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.aboutthetradinghistory.{OccupationalAndAccountingInformation, TurnoverSection}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.FinancialYearEndPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import util.AccountingInformationUtil._
import views.html.aboutthetradinghistory.financialYearEnd

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FinancialYearEndController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  financialYearEndView: financialYearEnd,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    request.sessionData.aboutTheTradingHistory
      .filter(_.occupationAndAccountingInformation.isDefined)
      .fold(Redirect(routes.AboutYourTradingHistoryController.show())) { aboutTheTradingHistory =>
        Ok(
          financialYearEndView(
            aboutTheTradingHistory.occupationAndAccountingInformation.flatMap(_.financialYear) match {
              case Some(financialYear) =>
                val yearEndChanged = aboutTheTradingHistory.occupationAndAccountingInformation
                  .flatMap(_.yearEndChanged)
                  .getOrElse(false)
                accountingInformationForm.fill((financialYear, yearEndChanged))
              case _                   => accountingInformationForm
            }
          )
        )
      }
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    request.sessionData.aboutTheTradingHistory
      .filter(_.occupationAndAccountingInformation.isDefined)
      .fold(Future.successful(Redirect(routes.AboutYourTradingHistoryController.show()))) { aboutTheTradingHistory =>
        continueOrSaveAsDraft[(DayMonthsDuration, Boolean)](
          accountingInformationForm,
          formWithErrors => BadRequest(financialYearEndView(formWithErrors)),
          data => {
            val occupationAndAccounting        = aboutTheTradingHistory.occupationAndAccountingInformation.get
            val firstOccupy                    = occupationAndAccounting.firstOccupy
            val newOccupationAndAccounting     =
              OccupationalAndAccountingInformation(firstOccupy, Some(data._1), Some(data._2))
            val isFinancialYearEndDayUnchanged = occupationAndAccounting.financialYear.contains(data._1)
            val isFinancialYearsListUnchanged  = newFinancialYears(newOccupationAndAccounting) == previousFinancialYears

            val turnoverSections = if (isFinancialYearEndDayUnchanged && isFinancialYearsListUnchanged) {
              aboutTheTradingHistory.turnoverSections
            } else if (isFinancialYearsListUnchanged) {
              (aboutTheTradingHistory.turnoverSections zip financialYearsRequired(firstOccupy, data._1)).map {
                case (turnoverSection, finYearEnd) => turnoverSection.copy(financialYearEnd = finYearEnd)
              }
            } else {
              financialYearsRequired(firstOccupy, data._1).map { finYearEnd =>
                TurnoverSection(
                  financialYearEnd = finYearEnd,
                  tradingPeriod = 52,
                  alcoholicDrinks = None,
                  food = None,
                  otherReceipts = None,
                  accommodation = None,
                  averageOccupancyRate = None
                )
              }
            }

            val sectionCompleted = if (isFinancialYearsListUnchanged) {
              aboutTheTradingHistory.checkYourAnswersAboutTheTradingHistory
            } else {
              None
            }

            val updatedData = updateAboutTheTradingHistory(
              _.copy(
                occupationAndAccountingInformation = Some(newOccupationAndAccounting),
                turnoverSections = turnoverSections,
                checkYourAnswersAboutTheTradingHistory = sectionCompleted
              )
            )

            session
              .saveOrUpdate(updatedData)
              .map(_ =>
                navigator.cyaPage
                  .filter(_ => navigator.from == "CYA" && isFinancialYearsListUnchanged && !data._2)
                  .getOrElse(navigator.nextPage(FinancialYearEndPageId, updatedData).apply(updatedData))
              )
              .map(Redirect)
          }
        )
      }
  }

}
