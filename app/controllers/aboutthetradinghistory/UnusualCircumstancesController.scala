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
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutthetradinghistory.UnusualCircumstancesForm.unusualCircumstancesForm
import models.ForType.*
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.aboutthetradinghistory.UnusualCircumstances
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.UnusualCircumstancesId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.unusualCircumstances

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class UnusualCircumstancesController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutTheTradingHistoryNavigator,
  unusualCircumstancesView: unusualCircumstances,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    audit.sendChangeLink("UnusualCircumstances")

    Ok(
      unusualCircumstancesView(
        request.sessionData.aboutTheTradingHistory.flatMap(_.unusualCircumstances) match {
          case Some(unusualCircumstances) => unusualCircumstancesForm.fill(unusualCircumstances)
          case _                          => unusualCircumstancesForm
        },
        request.sessionData.forType,
        getBackLink,
        request.sessionData.toSummary
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[UnusualCircumstances](
      unusualCircumstancesForm,
      formWithErrors =>
        BadRequest(
          unusualCircumstancesView(
            formWithErrors,
            request.sessionData.forType,
            getBackLink,
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedData = updateAboutTheTradingHistory(_.copy(unusualCircumstances = Some(data)))
        session.saveOrUpdate(updatedData).map { _ =>
          Redirect(navigator.nextPage(UnusualCircumstancesId, updatedData).apply(updatedData))
        }
      }
    )
  }

  private def getBackLink(implicit answers: SessionRequest[AnyContent]): String =
    answers.sessionData.forType match {
      case FOR6015 => controllers.aboutthetradinghistory.routes.IncomeExpenditureSummaryController.show().url
      case FOR6030 => controllers.aboutthetradinghistory.routes.Turnover6030Controller.show().url
      case _       => controllers.routes.TaskListController.show().url
    }
}
