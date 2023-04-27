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
import form.aboutthetradinghistory.OccupationalAndAccountingInformationForm.occupationalAndAccountingInformationForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.aboutthetradinghistory.{OccupationalAndAccountingInformation, TurnoverSection}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.AboutYourTradingHistoryPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.aboutYourTradingHistory

import java.time.LocalDate
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class AboutYourTradingHistoryController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  aboutYourTradingHistoryView: aboutYourTradingHistory,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    Ok(
      aboutYourTradingHistoryView(
        request.sessionData.aboutTheTradingHistory.flatMap(_.occupationAndAccountingInformation) match {
          case Some(tradingHistory) => occupationalAndAccountingInformationForm.fillAndValidate(tradingHistory)
          case _                    => occupationalAndAccountingInformationForm
        }
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[OccupationalAndAccountingInformation](
      occupationalAndAccountingInformationForm,
      formWithErrors => BadRequest(aboutYourTradingHistoryView(formWithErrors)),
      data =>
        if (request.sessionData.aboutTheTradingHistory.flatMap(_.occupationAndAccountingInformation).contains(data)) {
          Redirect(navigator.nextPage(AboutYourTradingHistoryPageId).apply(request.sessionData))
        } else {
          val updatedData = updateAboutTheTradingHistory(
            _.copy(
              occupationAndAccountingInformation = Some(data),
              turnoverSections = financialYearsRequired(data).map { finYearEnd =>
                TurnoverSection(
                  financialYearEnd = finYearEnd,
                  tradingPeriod = 52,
                  alcoholicDrinks = 0,
                  food = 0,
                  otherReceipts = 0,
                  accommodation = 0,
                  averageOccupancyRate = 0
                )
              }
            )
          )
          session
            .saveOrUpdate(updatedData)
            .map(_ => Redirect(navigator.nextPage(AboutYourTradingHistoryPageId).apply(updatedData)))
        }
    )
  }

  private def financialYearsRequired(accountingInfo: OccupationalAndAccountingInformation): Seq[LocalDate] = {
    val now: LocalDate            = LocalDate.now
    val currentFinancialYear: Int =
      if (
        now.isBefore(LocalDate.of(now.getYear, accountingInfo.financialYear.months, accountingInfo.financialYear.days))
      ) {
        now.getYear
      } else now.getYear + 1
    val yearDifference            = currentFinancialYear - accountingInfo.firstOccupy.years
    (1 to yearDifference.min(3)).map(yearsAgo =>
      LocalDate
        .of(currentFinancialYear - yearsAgo, accountingInfo.financialYear.months, accountingInfo.financialYear.days)
    )
  }
}
