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
import form.aboutthetradinghistory.FinancialYearEndDatesSummaryForm.financialYearEndDatesSummaryForm
import models.ForTypes
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne.updateAboutTheTradingHistoryPartOne
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.FinancialYearEndDatesPageId
import play.api.Logging
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.financialYearEndDatesSummary

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FinancialYearEndDatesSummaryController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  financialYearEndDateSummaryView: financialYearEndDatesSummary,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    Ok(
      financialYearEndDateSummaryView(
        request.sessionData.aboutTheTradingHistoryPartOne.flatMap(_.isFinancialYearEndDatesCorrect) match {
          case Some(x) => financialYearEndDatesSummaryForm.fill(x)
          case _       => financialYearEndDatesSummaryForm
        },
        getBackLink,
        request.sessionData.toSummary
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[Boolean](
      financialYearEndDatesSummaryForm,
      formWithErrors =>
        BadRequest(
          financialYearEndDateSummaryView(
            formWithErrors,
            getBackLink,
            request.sessionData.toSummary
          )
        ),
      data =>
        if (!data) {
          val formWithError = financialYearEndDatesSummaryForm
            .fill(data)
            .withError("isFinancialYearEndDatesCorrect", Messages("error.financialYearEndDates.incorrect"))
          Future.successful(
            BadRequest(financialYearEndDateSummaryView(formWithError, getBackLink, request.sessionData.toSummary))
          )
        } else {
          val updatedData = updateAboutTheTradingHistoryPartOne(_.copy(Some(data)))
          session
            .saveOrUpdate(updatedData)
            .map(_ =>
              navigator.cyaPage
                .filter(_ =>
                  navigator.from == "CYA" && isTurnoverSectionStarted(updatedData.aboutTheTradingHistory.get)
                )
                .getOrElse(navigator.nextPage(FinancialYearEndDatesPageId, updatedData).apply(updatedData))
            )
            .map(Redirect)
        }
    )
  }

  private def isTurnoverSectionStarted(
    aboutTheTradingHistory: AboutTheTradingHistory
  )(implicit request: SessionRequest[AnyContent]) =
    request.sessionData.forType match {
      case ForTypes.for6020                    =>
        aboutTheTradingHistory.turnoverSections6020.flatMap(_.headOption).exists(_.shop.isDefined)
      case ForTypes.for6030                    => aboutTheTradingHistory.turnoverSections6030.head.grossIncome.isDefined
      case ForTypes.for6045 | ForTypes.for6046 =>
        request.sessionData.aboutTheTradingHistoryPartOne
          .flatMap(_.turnoverSections6045)
          .flatMap(_.headOption)
          .exists(_.grossReceiptsCaravanFleetHire.isDefined)
      case ForTypes.for6076                    =>
        request.sessionData.aboutTheTradingHistoryPartOne
          .flatMap(_.turnoverSections6076)
          .flatMap(_.headOption)
          .exists(_.electricityGenerated.isDefined)
      case _                                   => aboutTheTradingHistory.turnoverSections.head.alcoholicDrinks.isDefined
    }

  private def getBackLink(implicit request: SessionRequest[AnyContent]) =
    navigator.from match {
      case "CYA" =>
        controllers.aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show().url
      case _     => controllers.aboutthetradinghistory.routes.FinancialYearEndController.show().url
    }
}
