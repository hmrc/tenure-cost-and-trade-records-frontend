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
import form.aboutthetradinghistory.CustomerCreditAccountsForm.customerCreditAccountsForm
import models.Session
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistory, CustomerCreditAccounts}
import models.submissions.common.AnswerYes
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.CustomerCreditAccountsId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.customerCreditAccounts

import java.time.LocalDate
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CustomerCreditAccountsController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutTheTradingHistoryNavigator,
  view: customerCreditAccounts,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    audit.sendChangeLink("CustomerCreditAccounts")

    request.sessionData.aboutTheTradingHistory
      .filter(_.occupationAndAccountingInformation.isDefined)
      .fold(Redirect(routes.WhenDidYouFirstOccupyController.show())) { aboutTheTradingHistory =>
        Ok(
          view(
            customerCreditAccountsForm(years(aboutTheTradingHistory))
              .fill(aboutTheTradingHistory.customerCreditAccounts.getOrElse(Seq.empty)),
            backLink(request.sessionData),
            request.sessionData.toSummary
          )
        )
      }

  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    request.sessionData.aboutTheTradingHistory
      .filter(_.occupationAndAccountingInformation.isDefined)
      .fold(Future.successful(Redirect(routes.WhenDidYouFirstOccupyController.show()))) { aboutTheTradingHistory =>
        continueOrSaveAsDraft[Seq[CustomerCreditAccounts]](
          customerCreditAccountsForm(years(aboutTheTradingHistory)),
          formWithErrors =>
            BadRequest(
              view(
                formWithErrors,
                backLink(request.sessionData),
                request.sessionData.toSummary
              )
            ),
          success => {
            val accounts =
              (success zip financialYearEndDates(aboutTheTradingHistory)).map { case (account, finYearEnd) =>
                account.copy(financialYearEnd = finYearEnd)
              }

            val updatedData = updateAboutTheTradingHistory(_.copy(customerCreditAccounts = Some(accounts)))
            session
              .saveOrUpdate(updatedData)
              .map(_ => Redirect(navigator.nextPage(CustomerCreditAccountsId, updatedData).apply(updatedData)))
          }
        )
      }
  }

  private def backLink(answers: Session)(implicit request: SessionRequest[AnyContent]): String      =
    navigator.from match {
      case "CYA" =>
        controllers.aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url
      case "TL"  => controllers.routes.TaskListController.show().url + "#customer-credit-accounts"
      case _     =>
        answers.aboutTheTradingHistory.flatMap(_.bunkeredFuelQuestion).map(_.bunkeredFuelQuestion) match {
          case Some(AnswerYes) =>
            routes.AddAnotherBunkerFuelCardsDetailsController.show(0).url
          case _               => routes.BunkeredFuelQuestionController.show().url
        }
    }
  private def financialYearEndDates(aboutTheTradingHistory: AboutTheTradingHistory): Seq[LocalDate] =
    aboutTheTradingHistory.turnoverSections6020.getOrElse(Seq.empty).map(_.financialYearEnd)

  private def years(aboutTheTradingHistory: AboutTheTradingHistory): Seq[String] =
    financialYearEndDates(aboutTheTradingHistory).map(_.getYear.toString)
}
