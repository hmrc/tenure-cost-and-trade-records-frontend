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
import form.aboutthetradinghistory.TurnoverForm6030.turnoverForm6030
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistory, TurnoverSection6030}
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.TurnoverPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.turnover6020

import java.time.LocalDate
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class NonFuelTurnoverController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  turnoverView: turnover6020,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    runWithSessionCheck { aboutTheTradingHistory =>
      val numberOfColumns = aboutTheTradingHistory.turnoverSections6020.getOrElse(Seq.empty).size

      Ok(
        turnoverView(
          turnoverForm6030(numberOfColumns, financialYearEndDates(aboutTheTradingHistory))
            .fill(
              aboutTheTradingHistory.turnoverSections6020
                .getOrElse(Seq.empty)
                .map(ts => TurnoverSection6030(ts.financialYearEnd, 52, None, None))
            ),
          getBackLink
        )
      )
    }
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    runWithSessionCheck { aboutTheTradingHistory =>
      val numberOfColumns = aboutTheTradingHistory.turnoverSections6020.getOrElse(Seq.empty).size
      val yearEndDates    = financialYearEndDates(aboutTheTradingHistory)

      continueOrSaveAsDraft[Seq[TurnoverSection6030]](
        turnoverForm6030(numberOfColumns, yearEndDates),
        formWithErrors => BadRequest(turnoverView(formWithErrors, getBackLink)),
        success => {
          val turnoverSections6020 =
            (success zip yearEndDates).map { case (turnoverSection, finYearEnd) =>
              turnoverSection.copy(financialYearEnd = finYearEnd)
            }

//            val updatedData = updateAboutTheTradingHistory(
//              _.copy(
//                turnoverSections6020 = Some(turnoverSections6020)
//              )
//            )
          val updatedData = request.sessionData

          session
            .saveOrUpdate(updatedData)
            .map { _ =>
              navigator.cyaPage
                .filter(_ => navigator.from == "CYA" && aboutTheTradingHistory.electricVehicleChargingPoints.isDefined)
                .getOrElse(navigator.nextPage(TurnoverPageId, updatedData).apply(updatedData))
            }
            .map(Redirect)
        }
      )
      Redirect(navigator.nextPage(TurnoverPageId, request.sessionData).apply(request.sessionData))
    }
  }

  private def runWithSessionCheck(
    action: AboutTheTradingHistory => Future[Result]
  )(implicit request: SessionRequest[AnyContent]): Future[Result] =
    request.sessionData.aboutTheTradingHistory
      .filter(_.occupationAndAccountingInformation.isDefined)
      .filter(_.turnoverSections6020.exists(_.nonEmpty))
      .fold(Future.successful(Redirect(routes.AboutYourTradingHistoryController.show())))(action)

  private def financialYearEndDates(aboutTheTradingHistory: AboutTheTradingHistory): Seq[LocalDate] =
    aboutTheTradingHistory.turnoverSections6020.getOrElse(Seq.empty).map(_.financialYearEnd)

  private def getBackLink(implicit request: SessionRequest[AnyContent]): String =
    navigator.from match {
      case "CYA" =>
        controllers.aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url
      case "TL"  => controllers.routes.TaskListController.show().url + "#non-fuel-turnover"
      case _     => controllers.aboutthetradinghistory.routes.AddAnotherLowMarginFuelCardsDetailsController.show(0).url
    }

}
