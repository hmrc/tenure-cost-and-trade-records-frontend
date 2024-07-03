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
import controllers.FORDataCaptureController
import form.aboutthetradinghistory.BunkeredFuelSoldForm.bunkeredFuelSoldForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistory, BunkeredFuelSold}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.BunkeredFuelSoldId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.bunkeredFuelSold

import java.time.LocalDate
import javax.inject.{Inject, Named}
import scala.concurrent.{ExecutionContext, Future}

class BunkeredFuelSoldController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  view: bunkeredFuelSold,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    request.sessionData.aboutTheTradingHistory
      .filter(_.occupationAndAccountingInformation.isDefined)
      .fold(Redirect(routes.AboutYourTradingHistoryController.show())) { aboutTheTradingHistory =>
        Ok(
          view(
            bunkeredFuelSoldForm(years(aboutTheTradingHistory))
              .fill(aboutTheTradingHistory.bunkeredFuelSold.getOrElse(Seq.empty)),
            calculateBackLink(request),
            request.sessionData.toSummary
          )
        )
      }

  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    request.sessionData.aboutTheTradingHistory
      .filter(_.occupationAndAccountingInformation.isDefined)
      .fold(Future.successful(Redirect(routes.AboutYourTradingHistoryController.show()))) { aboutTheTradingHistory =>
        continueOrSaveAsDraft[Seq[BunkeredFuelSold]](
          bunkeredFuelSoldForm(years(aboutTheTradingHistory)),
          formWithErrors =>
            BadRequest(
              view(
                formWithErrors,
                calculateBackLink(request),
                request.sessionData.toSummary
              )
            ),
          success => {
            val bunkeredFuelSold =
              (success zip financialYearEndDates(aboutTheTradingHistory)).map { case (bunkeredFuelSold, finYearEnd) =>
                bunkeredFuelSold.copy(financialYearEnd = finYearEnd)
              }

            val updatedData = updateAboutTheTradingHistory(_.copy(bunkeredFuelSold = Some(bunkeredFuelSold)))
            session
              .saveOrUpdate(updatedData)
              .map { _ =>
                val redirectToCYA = navigator.cyaPage.filter(_ => navigator.from(request) == "CYA")
                val nextPage =
                  redirectToCYA.getOrElse(navigator.nextPage(BunkeredFuelSoldId, updatedData).apply(updatedData))
                Redirect(nextPage)
              }
          }
        )
      }
  }


  private def calculateBackLink(implicit request: SessionRequest[AnyContent]) =
    navigator.from match {
      case "CYA" =>
        controllers.aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url
      case "TL" => controllers.routes.TaskListController.show().url + "#bunkered-fuel-sales"
      case _ =>
        request.sessionData.aboutTheTradingHistory.flatMap(
          _.occupationAndAccountingInformation.flatMap(_.yearEndChanged)
        ) match {
          case Some(true) => controllers.aboutthetradinghistory.routes.FinancialYearEndDatesController.show().url
          case Some(false) => controllers.aboutthetradinghistory.routes.FinancialYearEndController.show().url
          case _ => controllers.routes.TaskListController.show().url
        }
    }

  private def financialYearEndDates(aboutTheTradingHistory: AboutTheTradingHistory): Seq[LocalDate] =
    aboutTheTradingHistory.turnoverSections6020.getOrElse(Seq.empty).map(_.financialYearEnd)

  private def years(aboutTheTradingHistory: AboutTheTradingHistory): Seq[String] =
    financialYearEndDates(aboutTheTradingHistory).map(_.getYear.toString)
}
