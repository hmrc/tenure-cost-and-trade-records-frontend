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
import models.submissions.Form6010.{DayMonthsDuration, MonthsYearDuration}
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.aboutthetradinghistory.{OccupationalAndAccountingInformation, TurnoverSection}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.FinancialYearEndPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import util.AccountingInformationUtil
import views.html.aboutthetradinghistory.financialYearEnd

import java.time.LocalDate
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

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
    Ok(
      financialYearEndView(
        request.sessionData.aboutTheTradingHistory
          .flatMap(_.occupationAndAccountingInformation.flatMap(_.financialYear)) match {
          case Some(financialYear) =>
            val yearEndChanged = request.sessionData.aboutTheTradingHistory
              .flatMap(_.occupationAndAccountingInformation.flatMap(_.yearEndChanged))
              .getOrElse(false)
            accountingInformationForm.fill((financialYear, yearEndChanged))
          case _                   => accountingInformationForm
        }
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[(DayMonthsDuration, Boolean)](
      accountingInformationForm,
      formWithErrors => BadRequest(financialYearEndView(formWithErrors)),
      data => {
        val sessionTradingHistory      = request.sessionData.aboutTheTradingHistory
        val previousFinancialYearsList =
          sessionTradingHistory.fold(Seq.empty[LocalDate])(_.turnoverSections.map(_.financialYearEnd))
        val firstOccupy                = sessionTradingHistory
          .flatMap(_.occupationAndAccountingInformation.map(_.firstOccupy))
          .getOrElse(MonthsYearDuration(1, 2000))

        val newFinancialYearsList = AccountingInformationUtil.financialYearsRequired(firstOccupy, data._1)

//        if (newFinancialYearsList.equals(previousFinancialYearsList) && !data._2) {
//          Redirect(navigator.nextPage(FinancialYearEndPageId, request.sessionData).apply(request.sessionData))
//        } else {
          val updatedData = updateAboutTheTradingHistory(
            _.copy(
              occupationAndAccountingInformation =
                Some(OccupationalAndAccountingInformation(firstOccupy, Some(data._1), Some(data._2))),
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
              }
            )
          )
          session
            .saveOrUpdate(updatedData)
            .map(_ => Redirect(navigator.nextPage(FinancialYearEndPageId, updatedData).apply(updatedData)))
//        }
      }
    )
  }

}
