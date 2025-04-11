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
import form.aboutthetradinghistory.PercentageFromFuelCardsForm.percentageFromFuelCardsForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistory, PercentageFromFuelCards}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.PercentageFromFuelCardsId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.percentageFromFuelCards

import java.time.LocalDate
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PercentageFromFuelCardsController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutTheTradingHistoryNavigator,
  view: percentageFromFuelCards,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    audit.sendChangeLink("PercentageFromFuelCards")

    request.sessionData.aboutTheTradingHistory
      .filter(_.occupationAndAccountingInformation.isDefined)
      .fold(Redirect(routes.WhenDidYouFirstOccupyController.show())) { aboutTheTradingHistory =>
        Ok(
          view(
            percentageFromFuelCardsForm(years(aboutTheTradingHistory))
              .fill(aboutTheTradingHistory.percentageFromFuelCards.getOrElse(Seq.empty)),
            getBackLink,
            request.sessionData.toSummary
          )
        )
      }

  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    request.sessionData.aboutTheTradingHistory
      .filter(_.occupationAndAccountingInformation.isDefined)
      .fold(Future.successful(Redirect(routes.WhenDidYouFirstOccupyController.show()))) { aboutTheTradingHistory =>
        continueOrSaveAsDraft[Seq[PercentageFromFuelCards]](
          percentageFromFuelCardsForm(years(aboutTheTradingHistory)),
          formWithErrors =>
            BadRequest(
              view(
                formWithErrors,
                getBackLink,
                request.sessionData.toSummary
              )
            ),
          success => {
            val cards =
              (success zip financialYearEndDates(aboutTheTradingHistory)).map { case (card, finYearEnd) =>
                card.copy(financialYearEnd = finYearEnd)
              }

            val updatedData = updateAboutTheTradingHistory(_.copy(percentageFromFuelCards = Some(cards)))
            session
              .saveOrUpdate(updatedData)
              .map(_ => Redirect(navigator.nextPage(PercentageFromFuelCardsId, updatedData).apply(updatedData)))
          }
        )
      }
  }

  private def getBackLink(implicit request: SessionRequest[AnyContent]) =
    navigator.from match {
      case "CYA" =>
        controllers.aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url
      case "TL"  => controllers.routes.TaskListController.show().url + "#low-margin-fuel-cards"
      case _     => controllers.aboutthetradinghistory.routes.AcceptLowMarginFuelCardController.show().url
    }

  private def financialYearEndDates(aboutTheTradingHistory: AboutTheTradingHistory): Seq[LocalDate] =
    aboutTheTradingHistory.turnoverSections6020.getOrElse(Seq.empty).map(_.financialYearEnd)

  private def years(aboutTheTradingHistory: AboutTheTradingHistory): Seq[String] =
    financialYearEndDates(aboutTheTradingHistory).map(_.getYear.toString)
}
