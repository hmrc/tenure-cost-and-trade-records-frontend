/*
 * Copyright 2025 HM Revenue & Customs
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
import form.aboutthetradinghistory.CheckYourAnswersNoFinancialYearsForm.theForm
import models.submissions.aboutthetradinghistory.{AboutTheTradingHistory, AboutTheTradingHistoryPartOne}
import models.submissions.common.AnswersYesNo
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.CheckYourAnswersAboutTheTradingHistoryId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import util.AccountingInformationUtil.backLinkToFinancialYearEndDates
import views.html.aboutthetradinghistory.checkYourAnswerNoFinancialYears as CheckYourAnswerNoFinancialYearsView

import javax.inject.{Inject, Named}
import scala.concurrent.ExecutionContext
import scala.concurrent.Future.successful

class CheckYourAnswersNoFinancialYearsController @Inject (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  theView: CheckYourAnswerNoFinancialYearsView,
  sessionRefiner: WithSessionRefiner,
  @Named("session") repository: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport:

  def show(): Action[AnyContent] = (Action andThen sessionRefiner) { implicit request =>
    Ok(theView(theForm, backLinkUrl))
  }

  def submit(): Action[AnyContent] = (Action andThen sessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      theForm,
      formWithErrors => successful(BadRequest(theView(formWithErrors, backLinkUrl))),
      formData =>
        val currentSession = request.sessionData

        // Eventually empty all turnover sections across the entire trading history declaration
        val eventuallyEmptiedSession =
          if (currentSession.financialYearEndDates.isEmpty) {
            // It happens when the users have first occupied the property within their last financial year
            currentSession.copy(
              aboutTheTradingHistory = Some(
                AboutTheTradingHistory(
                  occupationAndAccountingInformation =
                    currentSession.aboutTheTradingHistory.flatMap(_.occupationAndAccountingInformation)
                )
              ),
              aboutTheTradingHistoryPartOne = Some(
                AboutTheTradingHistoryPartOne(
                  isFinancialYearsCorrect = Some(true),
                  whatYouWillNeed = Some(""),
                  turnoverSections6045 = Some(Seq.empty)
                )
              )
            )
          } else {
            currentSession
          }

        val newSession =
          eventuallyEmptiedSession
            .copy(
              aboutTheTradingHistory =
                Some(eventuallyEmptiedSession.aboutTheTradingHistory.fold(AboutTheTradingHistory()) {
                  _.copy(checkYourAnswersAboutTheTradingHistory = Some(formData))
                })
            )
            .copy(lastCYAPageUrl =
              Some(
                controllers.aboutthetradinghistory.routes.CheckYourAnswersNoFinancialYearsController
                  .show()
                  .url
              )
            )

        repository.saveOrUpdate(newSession).map { _ =>
          Redirect(
            navigator
              .nextPage(CheckYourAnswersAboutTheTradingHistoryId, request.sessionData)
              .apply(request.sessionData)
          )
        }
    )
  }

  private def backLinkUrl(using request: SessionRequest[AnyContent]): Option[String] =
    Some(backLinkToFinancialYearEndDates(navigator))
