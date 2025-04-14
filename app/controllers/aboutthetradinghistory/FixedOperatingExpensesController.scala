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

import actions.{SessionRequest, WithSessionRefiner}
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutthetradinghistory.FixedOperatingExpensesForm.fixedOperatingExpensesForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistory, FixedOperatingExpenses}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.FixedOperatingExpensesId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.fixedOperatingExpenses

import java.time.LocalDate
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FixedOperatingExpensesController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutTheTradingHistoryNavigator,
  fixedOperatingExpensesView: fixedOperatingExpenses,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("FixedOperatingExpenses")

    runWithSessionCheck { tradingHistory =>
      val yearEndDates = financialYearEndDates(tradingHistory)
      val years        = yearEndDates.map(_.getYear.toString)

      val fixedOperatingExpenses =
        if (tradingHistory.fixedOperatingExpensesSections.size == tradingHistory.turnoverSections.size) {
          (tradingHistory.fixedOperatingExpensesSections zip yearEndDates).map { case (foe, finYearEnd) =>
            foe.copy(financialYearEnd = finYearEnd)
          }
        } else {
          yearEndDates.map(FixedOperatingExpenses(_, None, None, None, None, None))
        }

      val updatedData = updateAboutTheTradingHistory(_.copy(fixedOperatingExpensesSections = fixedOperatingExpenses))
      session
        .saveOrUpdate(updatedData)
        .flatMap(_ =>
          Ok(
            fixedOperatingExpensesView(
              fixedOperatingExpensesForm(years).fill(fixedOperatingExpenses),
              navigator.from
            )
          )
        )
    }
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    runWithSessionCheck { tradingHistory =>
      val yearEndDates = financialYearEndDates(tradingHistory)
      val years        = yearEndDates.map(_.getYear.toString)

      continueOrSaveAsDraft[Seq[FixedOperatingExpenses]](
        fixedOperatingExpensesForm(years),
        formWithErrors => BadRequest(fixedOperatingExpensesView(formWithErrors, navigator.from)),
        data => {
          val fixedOperatingExpenses = (data zip yearEndDates).map { case (foe, finYearEnd) =>
            foe.copy(financialYearEnd = finYearEnd)
          }

          val updatedData =
            updateAboutTheTradingHistory(_.copy(fixedOperatingExpensesSections = fixedOperatingExpenses))
          session
            .saveOrUpdate(updatedData)
            .map(_ =>
              navigator.cyaPage
                .filter(_ => navigator.from == "CYA" && tradingHistory.otherCosts.nonEmpty)
                .getOrElse(navigator.nextPage(FixedOperatingExpensesId, updatedData).apply(updatedData))
            )
            .map(Redirect)
        }
      )
    }
  }

  private def runWithSessionCheck(
    action: AboutTheTradingHistory => Future[Result]
  )(implicit request: SessionRequest[AnyContent]): Future[Result] =
    request.sessionData.aboutTheTradingHistory
      .filter(_.occupationAndAccountingInformation.isDefined)
      .filter(_.turnoverSections.nonEmpty)
      .fold(Future.successful(Redirect(routes.WhenDidYouFirstOccupyController.show())))(action)

  private def financialYearEndDates(aboutTheTradingHistory: AboutTheTradingHistory): Seq[LocalDate] =
    aboutTheTradingHistory.turnoverSections.map(_.financialYearEnd)

}
