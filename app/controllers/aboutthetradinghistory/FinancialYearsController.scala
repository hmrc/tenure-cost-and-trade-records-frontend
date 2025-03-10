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
import form.aboutthetradinghistory.FinancialYearsForm.financialYearsForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistoryPartOne.updateAboutTheTradingHistoryPartOne
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.FinancialYearsPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.financialYears
import util.AccountingInformationUtil.backLinkToFinancialYearEndDates

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class FinancialYearsController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutTheTradingHistoryNavigator,
  financialYearsView: financialYears,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    audit.sendChangeLink("FinancialYears")

    Ok(
      financialYearsView(
        request.sessionData.aboutTheTradingHistoryPartOne
          .flatMap(_.isFinancialYearsCorrect)
          .fold(financialYearsForm)(financialYearsForm.fill),
        getBackLink
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[Boolean](
      financialYearsForm,
      formWithErrors =>
        BadRequest(
          financialYearsView(
            formWithErrors,
            getBackLink
          )
        ),
      data =>
        val updatedData = updateAboutTheTradingHistoryPartOne(_.copy(isFinancialYearsCorrect = Some(data)))

        session
          .saveOrUpdate(updatedData)
          .map(_ => Redirect(navigator.nextPage(FinancialYearsPageId, updatedData).apply(updatedData)))
    )
  }

  private def getBackLink(implicit request: SessionRequest[AnyContent]): String =
    backLinkToFinancialYearEndDates(navigator)

}
