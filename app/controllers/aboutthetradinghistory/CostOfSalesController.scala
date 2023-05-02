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
import form.aboutthetradinghistory.CostOfSalesForm.costOfSalesForm
import models.submissions.aboutthetradinghistory.CostOfSales
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.CostOfSalesId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.costOfSales

import javax.inject.{Inject, Named, Singleton}

@Singleton
class CostOfSalesController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  costOfSalesView: costOfSales,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    Ok(
      costOfSalesView(
        request.sessionData.aboutTheTradingHistory.flatMap(_.costOfSales) match {
          case Some(costOfSales) => costOfSalesForm.fillAndValidate(costOfSales)
          case _                 => costOfSalesForm
        },
        request.sessionData.toSummary
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[CostOfSales](
      costOfSalesForm,
      formWithErrors => BadRequest(costOfSalesView(formWithErrors, request.sessionData.toSummary)),
      data => {
        val updatedData = request.sessionData
        Redirect(navigator.nextPage(CostOfSalesId).apply(updatedData))
      }
    )
  }

}
