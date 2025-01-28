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
import controllers.{FORDataCaptureController, aboutthetradinghistory}
import form.aboutthetradinghistory.TurnoverForm6020.turnoverForm6020
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistory, TurnoverSection6020}
import models.submissions.common.AnswerYes
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.TurnoverPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.turnover6020

import java.time.LocalDate
import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Yuriy Tumakha
  */
@Singleton
class NonFuelTurnoverController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutTheTradingHistoryNavigator,
  turnoverView: turnover6020,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("NonFuelTurnover")

    runWithSessionCheck { tradingHistory =>
      val yearEndDates = financialYearEndDates(tradingHistory)
      val years        = yearEndDates.map(_.getYear.toString)

      Ok(
        turnoverView(
          turnoverForm6020(years).fill(tradingHistory.turnoverSections6020.getOrElse(Seq.empty)),
          getBackLink(tradingHistory)
        )
      )
    }
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    runWithSessionCheck { tradingHistory =>
      val yearEndDates = financialYearEndDates(tradingHistory)
      val years        = yearEndDates.map(_.getYear.toString)

      continueOrSaveAsDraft[Seq[TurnoverSection6020]](
        turnoverForm6020(years),
        formWithErrors => BadRequest(turnoverView(formWithErrors, getBackLink(tradingHistory))),
        success => {
          val turnoverSections6020 =
            (success zip yearEndDates).map { case (turnoverSection, finYearEnd) =>
              turnoverSection.copy(financialYearEnd = finYearEnd)
            }

          val updatedData = updateAboutTheTradingHistory(
            _.copy(
              turnoverSections6020 = Some(turnoverSections6020)
            )
          )

          session
            .saveOrUpdate(updatedData)
            .map { _ =>
              navigator.cyaPage
                .filter(_ => navigator.from == "CYA" && tradingHistory.electricVehicleChargingPoints.isDefined)
                .getOrElse(navigator.nextPage(TurnoverPageId, updatedData).apply(updatedData))
            }
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
      .filter(_.turnoverSections6020.exists(_.nonEmpty))
      .fold(Future.successful(Redirect(routes.AboutYourTradingHistoryController.show())))(action)

  private def financialYearEndDates(aboutTheTradingHistory: AboutTheTradingHistory): Seq[LocalDate] =
    aboutTheTradingHistory.turnoverSections6020.getOrElse(Seq.empty).map(_.financialYearEnd)

  private def getBackLink(
    tradingHistory: AboutTheTradingHistory
  )(implicit request: SessionRequest[AnyContent]): String =
    navigator.from match {
      case "CYA" =>
        controllers.aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url
      case "TL"  => controllers.routes.TaskListController.show().url + "#non-fuel-turnover"
      case _     =>
        tradingHistory.doYouAcceptLowMarginFuelCard match {
          case Some(AnswerYes) =>
            aboutthetradinghistory.routes.AddAnotherLowMarginFuelCardsDetailsController.show(0).url
          case _               => aboutthetradinghistory.routes.AcceptLowMarginFuelCardController.show().url
        }
    }

}
