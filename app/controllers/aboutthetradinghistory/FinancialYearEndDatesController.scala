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
import form.aboutthetradinghistory.FinancialYearEndDatesForm.financialYearEndDatesForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.aboutthetradinghistory.CostOfSales
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.FinancialYearEndDatesPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import util.AccountingInformationUtil.newFinancialYears
import views.html.aboutthetradinghistory.financialYearEndDates

import java.time.{LocalDate, MonthDay}
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
      .filter(_.occupationAndAccountingInformation.map(_.financialYear).isDefined)
      .filter(_.turnoverSections.nonEmpty)
      .fold(Redirect(routes.AboutYourTradingHistoryController.show())) { aboutTheTradingHistory =>
        val occupationAndAccounting = aboutTheTradingHistory.occupationAndAccountingInformation.get
        Ok(
          financialYearEndDatesView(
            financialYearEndDatesForm().fill(aboutTheTradingHistory.turnoverSections.map(_.financialYearEnd)),
            newFinancialYears(occupationAndAccounting)
          )
        )
      }
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    request.sessionData.aboutTheTradingHistory
      .filter(_.occupationAndAccountingInformation.map(_.financialYear).isDefined)
      .filter(_.turnoverSections.nonEmpty)
      .fold(Future.successful(Redirect(routes.AboutYourTradingHistoryController.show()))) { aboutTheTradingHistory =>
        val occupationAndAccounting = aboutTheTradingHistory.occupationAndAccountingInformation.get
        continueOrSaveAsDraft[Seq[LocalDate]](
          financialYearEndDatesForm(Some(newFinancialYears(occupationAndAccounting))),
          formWithErrors =>
            BadRequest(
              financialYearEndDatesView(
                formWithErrors,
                newFinancialYears(occupationAndAccounting)
              )
            ),
          data => {
            val occupationAndAccounting = aboutTheTradingHistory.occupationAndAccountingInformation.get
            val financialYearEnd        = occupationAndAccounting.financialYear.get.toMonthDay

            val newOccupationAndAccounting =
              if (data.forall(d => MonthDay.of(d.getMonthValue, d.getDayOfMonth) == financialYearEnd)) {
                occupationAndAccounting
              } else {
                occupationAndAccounting.copy(yearEndChanged = Some(true))
              }

            val turnoverSections =
              (aboutTheTradingHistory.turnoverSections zip data).map { case (turnoverSection, finYearEnd) =>
                turnoverSection.copy(financialYearEnd = finYearEnd)
              }

            val costOfSales = if (aboutTheTradingHistory.costOfSales.size == turnoverSections.size) {
              (aboutTheTradingHistory.costOfSales zip turnoverSections.map(_.financialYearEnd)).map {
                case (costOfSales, finYearEnd) => costOfSales.copy(financialYearEnd = finYearEnd)
              }
            } else {
              turnoverSections.map(_.financialYearEnd).map(CostOfSales(_, None, None, None, None))
            }

            val updatedData = updateAboutTheTradingHistory(
              _.copy(
                occupationAndAccountingInformation = Some(newOccupationAndAccounting),
                turnoverSections = turnoverSections,
                costOfSales = costOfSales
              )
            )

            session
              .saveOrUpdate(updatedData)
              .map(_ =>
                navigator.cyaPage
                  .filter(_ => navigator.from == "CYA" && turnoverSections.head.alcoholicDrinks.isDefined)
                  .getOrElse(navigator.nextPage(FinancialYearEndDatesPageId, updatedData).apply(updatedData))
              )
              .map(Redirect)
          }
        )
      }
  }

}
