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
import form.aboutthetradinghistory.AccountingInformationForm.accountingInformationForm
import models.ForType.*
import models.Session
import models.submissions.Form6010.{DayMonthsDuration, MonthsYearDuration}
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistory, CheckYourAnswersAboutTheTradingHistory, CostOfSales, OccupationalAndAccountingInformation, TotalPayrollCost, TurnoverSection, TurnoverSection6020, TurnoverSection6030}
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
  audit: Audit,
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
        audit.sendChangeLink("FinancialYearEnd")

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
          (newFinancialYear, newYearEndChanged) => {
            val occupationAndAccounting        = aboutTheTradingHistory.occupationAndAccountingInformation.get
            val firstOccupy                    = occupationAndAccounting.firstOccupy
            val newOccupationAndAccounting     =
              OccupationalAndAccountingInformation(firstOccupy, Some(newFinancialYear), Some(newYearEndChanged))
            val isFinancialYearEndDayUnchanged = occupationAndAccounting.financialYear.contains(newFinancialYear)
            val isFinancialYearsListUnchanged  = newFinancialYears(newOccupationAndAccounting) == previousFinancialYears

            val updatedData: Session = request.sessionData.forType match {
              case FOR6020           =>
                buildUpdateData6020(
                  firstOccupy,
                  newFinancialYear,
                  aboutTheTradingHistory,
                  newOccupationAndAccounting,
                  isFinancialYearEndDayUnchanged,
                  isFinancialYearsListUnchanged
                )
              case FOR6030           =>
                buildUpdateData6030(
                  firstOccupy,
                  newFinancialYear,
                  aboutTheTradingHistory,
                  newOccupationAndAccounting,
                  isFinancialYearEndDayUnchanged,
                  isFinancialYearsListUnchanged
                )
              case FOR6045 | FOR6046 =>
                buildUpdatedData6045(
                  aboutTheTradingHistory,
                  newOccupationAndAccounting,
                  isFinancialYearEndDayUnchanged
                )
              case FOR6048           =>
                buildUpdatedData6048(
                  aboutTheTradingHistory,
                  newOccupationAndAccounting,
                  isFinancialYearEndDayUnchanged
                )
              case FOR6076           =>
                buildUpdatedData6076(
                  aboutTheTradingHistory,
                  newOccupationAndAccounting,
                  isFinancialYearEndDayUnchanged
                )
              case _                 =>
                buildUpdateData(
                  firstOccupy,
                  newFinancialYear,
                  aboutTheTradingHistory,
                  newOccupationAndAccounting,
                  isFinancialYearEndDayUnchanged,
                  isFinancialYearsListUnchanged
                )
            }
            session
              .saveOrUpdate(updatedData)
              .map(_ =>
                navigator.cyaPage
                  .filter(_ =>
                    navigator.from == "CYA" && !newYearEndChanged &&
                      (isFinancialYearsListUnchanged || (
                        request.sessionData.forType == FOR6076 && request.sessionData.aboutTheTradingHistoryPartOne
                          .flatMap(_.turnoverSections6076)
                          .flatMap(_.headOption)
                          .exists(_.electricityGenerated.isDefined)
                      ) || (
                        (request.sessionData.forType == FOR6045 || request.sessionData.forType == FOR6046) &&
                          request.sessionData.aboutTheTradingHistoryPartOne
                            .flatMap(_.turnoverSections6045)
                            .flatMap(_.headOption)
                            .exists(_.grossReceiptsCaravanFleetHire.isDefined)
                      ) || (
                        request.sessionData.forType == FOR6048 &&
                          request.sessionData.aboutTheTradingHistoryPartOne
                            .flatMap(_.turnoverSections6048)
                            .flatMap(_.headOption)
                            .exists(_.income.isDefined)
                      ))
                  )
                  .getOrElse(navigator.nextPage(FinancialYearEndPageId, updatedData).apply(updatedData))
              )
              .map(Redirect)
          }
        )
      }
  }

  private def sectionCompleted(
    isFinancialYearsListUnchanged: Boolean,
    aboutTheTradingHistory: AboutTheTradingHistory
  ): Option[CheckYourAnswersAboutTheTradingHistory] =
    if (isFinancialYearsListUnchanged) {
      aboutTheTradingHistory.checkYourAnswersAboutTheTradingHistory
    } else {
      None
    }

  private def buildUpdateData(
    firstOccupy: MonthsYearDuration,
    financialYear: DayMonthsDuration,
    aboutTheTradingHistory: AboutTheTradingHistory,
    newOccupationAndAccounting: OccupationalAndAccountingInformation,
    isFinancialYearEndDayUnchanged: Boolean,
    isFinancialYearsListUnchanged: Boolean
  )(implicit request: SessionRequest[AnyContent]) = {
    val turnoverSections =
      if (isFinancialYearEndDayUnchanged && isFinancialYearsListUnchanged) {
        if (!newOccupationAndAccounting.yearEndChanged.get) {
          (aboutTheTradingHistory.turnoverSections zip financialYearsRequired(firstOccupy, financialYear)).map {
            case (turnoverSection, finYearEnd) => turnoverSection.copy(financialYearEnd = finYearEnd)
          }
        } else {
          aboutTheTradingHistory.turnoverSections
        }
      } else if (isFinancialYearsListUnchanged) {
        (aboutTheTradingHistory.turnoverSections zip financialYearsRequired(firstOccupy, financialYear)).map {
          case (turnoverSection, finYearEnd) => turnoverSection.copy(financialYearEnd = finYearEnd)
        }
      } else {
        financialYearsRequired(firstOccupy, financialYear).map { finYearEnd =>
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

    val costOfSales = if (aboutTheTradingHistory.costOfSales.size == turnoverSections.size) {
      (aboutTheTradingHistory.costOfSales zip turnoverSections.map(_.financialYearEnd)).map {
        case (costOfSales, finYearEnd) => costOfSales.copy(financialYearEnd = finYearEnd)
      }
    } else {
      turnoverSections.map(_.financialYearEnd).map(CostOfSales(_, None, None, None, None))
    }

    val totalPayrollCosts = if (aboutTheTradingHistory.totalPayrollCostSections.size == turnoverSections.size) {
      (aboutTheTradingHistory.totalPayrollCostSections zip turnoverSections.map(_.financialYearEnd)).map {
        case (totalPayrollCosts, finYearEnd) => totalPayrollCosts.copy(financialYearEnd = finYearEnd)
      }
    } else {
      turnoverSections.map(_.financialYearEnd).map(TotalPayrollCost(_, None, None))
    }

    val updatedData = updateAboutTheTradingHistory(
      _.copy(
        occupationAndAccountingInformation = Some(newOccupationAndAccounting),
        turnoverSections = turnoverSections,
        costOfSales = costOfSales,
        totalPayrollCostSections = totalPayrollCosts,
        checkYourAnswersAboutTheTradingHistory = sectionCompleted(isFinancialYearsListUnchanged, aboutTheTradingHistory)
      )
    )
    updatedData
  }

  private def buildUpdateData6020(
    firstOccupy: MonthsYearDuration,
    financialYear: DayMonthsDuration,
    aboutTheTradingHistory: AboutTheTradingHistory,
    newOccupationAndAccounting: OccupationalAndAccountingInformation,
    isFinancialYearEndDayUnchanged: Boolean,
    isFinancialYearsListUnchanged: Boolean
  )(implicit request: SessionRequest[AnyContent]): Session = {

    val originalTurnoverSections6020 = aboutTheTradingHistory.turnoverSections6020.getOrElse(Seq.empty)

    def turnoverSections6020: Seq[TurnoverSection6020] =
      if (isFinancialYearEndDayUnchanged && isFinancialYearsListUnchanged) {
        originalTurnoverSections6020
      } else if (isFinancialYearsListUnchanged) {
        (originalTurnoverSections6020 zip financialYearsRequired(firstOccupy, financialYear)).map {
          case (turnoverSection, finYearEnd) => turnoverSection.copy(financialYearEnd = finYearEnd)
        }
      } else {
        financialYearsRequired(firstOccupy, financialYear).map { finYearEnd =>
          TurnoverSection6020(financialYearEnd = finYearEnd)
        }
      }

    val updatedData = updateAboutTheTradingHistory(
      _.copy(
        occupationAndAccountingInformation = Some(newOccupationAndAccounting),
        turnoverSections6020 = Some(turnoverSections6020),
        checkYourAnswersAboutTheTradingHistory = sectionCompleted(isFinancialYearsListUnchanged, aboutTheTradingHistory)
      )
    )
    updatedData
  }

  private def buildUpdateData6030(
    firstOccupy: MonthsYearDuration,
    financialYear: DayMonthsDuration,
    aboutTheTradingHistory: AboutTheTradingHistory,
    newOccupationAndAccounting: OccupationalAndAccountingInformation,
    isFinancialYearEndDayUnchanged: Boolean,
    isFinancialYearsListUnchanged: Boolean
  )(implicit request: SessionRequest[AnyContent]) = {
    def turnoverSections6030 =
      if (isFinancialYearEndDayUnchanged && isFinancialYearsListUnchanged) {
        aboutTheTradingHistory.turnoverSections6030
      } else if (isFinancialYearsListUnchanged) {
        (aboutTheTradingHistory.turnoverSections6030 zip financialYearsRequired(firstOccupy, financialYear)).map {
          case (turnoverSection6030, finYearEnd) => turnoverSection6030.copy(financialYearEnd = finYearEnd)
        }
      } else {
        financialYearsRequired(firstOccupy, financialYear).map { finYearEnd =>
          TurnoverSection6030(
            financialYearEnd = finYearEnd,
            tradingPeriod = 52,
            grossIncome = None,
            totalVisitorNumbers = None
          )
        }
      }

    val updatedData = updateAboutTheTradingHistory(
      _.copy(
        occupationAndAccountingInformation = Some(newOccupationAndAccounting),
        turnoverSections6030 = turnoverSections6030,
        checkYourAnswersAboutTheTradingHistory = sectionCompleted(isFinancialYearsListUnchanged, aboutTheTradingHistory)
      )
    )
    updatedData
  }

}
