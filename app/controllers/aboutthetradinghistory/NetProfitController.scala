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
import form.aboutthetradinghistory.NetProfitForm.netProfitForm
import models.submissions.aboutthetradinghistory.NetProfit
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.NetProfitId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.netProfit

import javax.inject.{Inject, Named, Singleton}

@Singleton
class NetProfitController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  netProfitView: netProfit,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    Ok(
      netProfitView(
        request.sessionData.aboutTheTradingHistory.flatMap(_.netProfit) match {
          case Some(netProfit) => netProfitForm.fillAndValidate(netProfit)
          case _               => netProfitForm
        },
        request.sessionData.toSummary
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[NetProfit](
      netProfitForm,
      formWithErrors => BadRequest(netProfitView(formWithErrors, request.sessionData.toSummary)),
      data => {
        val updatedData = request.sessionData
        Redirect(navigator.nextPage(NetProfitId, updatedData).apply(updatedData))
      }
    )
  }

}
