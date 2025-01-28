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
import form.aboutthetradinghistory.VariableOperatingExpensesForm.variableOperatingExpensesForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistory, VariableOperatingExpenses, VariableOperatingExpensesSections}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.VariableOperatingExpensesId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.variableOperatingExpenses

import java.time.LocalDate
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VariableOperatingExpensesController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutTheTradingHistoryNavigator,
  variableOperativeExpensesView: variableOperatingExpenses,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("VariableOperatingExpenses")

    runWithSessionCheck { tradingHistory =>
      val yearEndDates = financialYearEndDates(tradingHistory)
      val years        = yearEndDates.map(_.getYear.toString)

      val existingVoeSeq = tradingHistory.variableOperatingExpenses
        .fold(Seq.empty[VariableOperatingExpenses])(_.variableOperatingExpenses)

      val voeSeq =
        if (existingVoeSeq.size == tradingHistory.turnoverSections.size) {
          (existingVoeSeq zip yearEndDates).map { case (voe, finYearEnd) =>
            voe.copy(financialYearEnd = finYearEnd)
          }
        } else {
          yearEndDates.map(VariableOperatingExpenses(_, None, None, None, None, None, None, None, None))
        }

      val voeSections = tradingHistory.variableOperatingExpenses
        .fold(VariableOperatingExpensesSections(voeSeq))(_.copy(variableOperatingExpenses = voeSeq))

      val updatedData = updateAboutTheTradingHistory(_.copy(variableOperatingExpenses = Some(voeSections)))
      session
        .saveOrUpdate(updatedData)
        .flatMap(_ =>
          Ok(
            variableOperativeExpensesView(
              variableOperatingExpensesForm(years).fill(voeSections),
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

      continueOrSaveAsDraft[VariableOperatingExpensesSections](
        variableOperatingExpensesForm(years),
        formWithErrors => {
          val updatedErrors         = formWithErrors.errors.map { error =>
            if (error.key.isEmpty && error.message == "error.variableExpenses.otherExpensesDetails.required") {
              error.copy(key = "otherExpensesDetails")
            } else {
              error
            }
          }
          val updatedFormWithErrors = formWithErrors.copy(errors = updatedErrors)
          BadRequest(variableOperativeExpensesView(updatedFormWithErrors, navigator.from))
        },
        data => {
          val voeSeq = (data.variableOperatingExpenses zip yearEndDates).map { case (voe, finYearEnd) =>
            voe.copy(financialYearEnd = finYearEnd)
          }

          val voeSections = data.copy(variableOperatingExpenses = voeSeq)

          val updatedData = updateAboutTheTradingHistory(_.copy(variableOperatingExpenses = Some(voeSections)))
          session
            .saveOrUpdate(updatedData)
            .map(_ =>
              navigator.cyaPage
                .filter(_ => navigator.from == "CYA" && tradingHistory.fixedOperatingExpensesSections.nonEmpty)
                .getOrElse(navigator.nextPage(VariableOperatingExpensesId, updatedData).apply(updatedData))
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
      .fold(Future.successful(Redirect(routes.AboutYourTradingHistoryController.show())))(action)

  private def financialYearEndDates(aboutTheTradingHistory: AboutTheTradingHistory): Seq[LocalDate] =
    aboutTheTradingHistory.turnoverSections.map(_.financialYearEnd)

}
