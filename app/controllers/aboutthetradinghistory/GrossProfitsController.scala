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

import actions.WithSessionRefiner
import controllers.FORDataCaptureController
import form.aboutthetradinghistory.GrossProfitForm.grossProfitForm
import models.submissions.aboutthetradinghistory.GrossProfit
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.GrossProfitsId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.grossProfits

import javax.inject.{Inject, Named, Singleton}

@Singleton
class GrossProfitsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  grossProfitsView: grossProfits,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    Ok(
      grossProfitsView(
        request.sessionData.aboutTheTradingHistory.flatMap(_.grossProfit) match {
          case Some(grossProfit) => grossProfitForm.fillAndValidate(grossProfit)
          case _                 => grossProfitForm
        },
        request.sessionData.toSummary
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[GrossProfit](
      grossProfitForm,
      formWithErrors => BadRequest(grossProfitsView(formWithErrors, request.sessionData.toSummary)),
      data => {
        val updatedData = request.sessionData
        Redirect(navigator.nextPage(GrossProfitsId, updatedData).apply(updatedData))
      }
    )
  }

}
