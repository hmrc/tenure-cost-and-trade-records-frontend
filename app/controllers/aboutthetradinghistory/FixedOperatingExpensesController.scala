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
import form.aboutthetradinghistory.FixedOperatingExpensesForm.fixedOperatingExpensesForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.aboutthetradinghistory.FixedOperatingExpenses
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.FixedOperatingExpensesId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.fixedOperatingExpenses

import java.time.LocalDate
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FixedOperatingExpensesController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  fixedOperatingExpensesView: fixedOperatingExpenses,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    request.sessionData.aboutTheTradingHistory
      .filter(_.occupationAndAccountingInformation.isDefined)
      .fold(Redirect(routes.AboutYourTradingHistoryController.show())) { aboutTheTradingHistory =>
        val numberOfColumns                = aboutTheTradingHistory.turnoverSections.size
        val financialYears: Seq[LocalDate] = aboutTheTradingHistory.turnoverSections.foldLeft(Seq.empty[LocalDate])(
          (sequence, turnoverSection) => sequence :+ turnoverSection.financialYearEnd
        )
        Ok(
          fixedOperatingExpensesView(
            fixedOperatingExpensesForm(numberOfColumns)
              .fill(aboutTheTradingHistory.fixedOperatingExpensesSections),
            numberOfColumns,
            financialYears,
            request.sessionData.toSummary
          )
        )
      }
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    request.sessionData.aboutTheTradingHistory
      .filter(_.occupationAndAccountingInformation.isDefined)
      .fold(Future.successful(Redirect(routes.AboutYourTradingHistoryController.show()))) { aboutTheTradingHistory =>
        val numberOfColumns                = aboutTheTradingHistory.turnoverSections.size
        val financialYears: Seq[LocalDate] = aboutTheTradingHistory.turnoverSections.foldLeft(Seq.empty[LocalDate])(
          (sequence, turnoverSection) => sequence :+ turnoverSection.financialYearEnd
        )
        continueOrSaveAsDraft[Seq[FixedOperatingExpenses]](
          fixedOperatingExpensesForm(numberOfColumns),
          formWithErrors =>
            BadRequest(
              fixedOperatingExpensesView(formWithErrors, numberOfColumns, financialYears, request.sessionData.toSummary)
            ),
          success => {
            val updatedData = updateAboutTheTradingHistory(_.copy(fixedOperatingExpensesSections = success))
            session
              .saveOrUpdate(updatedData)
              .map(_ => Redirect(navigator.nextPage(FixedOperatingExpensesId, updatedData).apply(updatedData)))
          }
        )
      }

  }

}
