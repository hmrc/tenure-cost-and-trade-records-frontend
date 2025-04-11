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
import form.aboutthetradinghistory.TotalPayrollCostForm.totalPayrollCostForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistory, TotalPayrollCost}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.TotalPayrollCostId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.totalPayrollCosts

import java.time.LocalDate
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TotalPayrollCostsController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutTheTradingHistoryNavigator,
  totalPayrollCostsView: totalPayrollCosts,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    audit.sendChangeLink("TotalPayrollCosts")

    request.sessionData.aboutTheTradingHistory
      .filter(_.occupationAndAccountingInformation.isDefined)
      .fold(Redirect(routes.WhenDidYouFirstOccupyController.show())) { aboutTheTradingHistory =>
        val numberOfColumns                = aboutTheTradingHistory.turnoverSections.size
        val financialYears: Seq[LocalDate] = aboutTheTradingHistory.turnoverSections.foldLeft(Seq.empty[LocalDate])(
          (sequence, turnoverSection) => sequence :+ turnoverSection.financialYearEnd
        )
        Ok(
          totalPayrollCostsView(
            totalPayrollCostForm(years(aboutTheTradingHistory)).fill(aboutTheTradingHistory.totalPayrollCostSections),
            numberOfColumns,
            financialYears,
            request.sessionData.toSummary,
            navigator.from
          )
        )
      }

  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    request.sessionData.aboutTheTradingHistory
      .filter(_.occupationAndAccountingInformation.isDefined)
      .fold(Future.successful(Redirect(routes.WhenDidYouFirstOccupyController.show()))) { aboutTheTradingHistory =>
        val numberOfColumns                = aboutTheTradingHistory.turnoverSections.size
        val financialYears: Seq[LocalDate] = aboutTheTradingHistory.turnoverSections.foldLeft(Seq.empty[LocalDate])(
          (sequence, turnoverSection) => sequence :+ turnoverSection.financialYearEnd
        )
        continueOrSaveAsDraft[Seq[TotalPayrollCost]](
          totalPayrollCostForm(years(aboutTheTradingHistory)),
          formWithErrors =>
            BadRequest(
              totalPayrollCostsView(
                formWithErrors,
                numberOfColumns,
                financialYears,
                request.sessionData.toSummary,
                navigator.from
              )
            ),
          success => {
            val totalPaytollCosts =
              (success zip financialYearEndDates(aboutTheTradingHistory)).map { case (totalPaytollCost, finYearEnd) =>
                totalPaytollCost.copy(financialYearEnd = finYearEnd)
              }

            val updatedData = updateAboutTheTradingHistory(_.copy(totalPayrollCostSections = totalPaytollCosts))
            session
              .saveOrUpdate(updatedData)
              .map(_ => Redirect(navigator.nextPage(TotalPayrollCostId, updatedData).apply(updatedData)))
          }
        )
      }
  }

  private def financialYearEndDates(aboutTheTradingHistory: AboutTheTradingHistory): Seq[LocalDate] =
    aboutTheTradingHistory.turnoverSections.map(_.financialYearEnd)
  private def years(aboutTheTradingHistory: AboutTheTradingHistory): Seq[String]                    =
    financialYearEndDates(aboutTheTradingHistory).map(_.getYear.toString)

}
