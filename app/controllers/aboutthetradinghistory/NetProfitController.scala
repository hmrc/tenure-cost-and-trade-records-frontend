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
import form.aboutthetradinghistory.NetProfitForm.netProfitForm
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.NetProfitId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.aboutthetradinghistory.netProfit

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class NetProfitController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  netProfitView: netProfit,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    Ok(
      netProfitView(
        request.sessionData.aboutTheTradingHistory.flatMap(_.netProfit) match {
          case Some(netProfit) => netProfitForm.fillAndValidate(netProfit)
          case _               => netProfitForm
        }
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    netProfitForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(netProfitView(formWithErrors))),
        data => {
          val updatedData = request.sessionData
          Future.successful(Redirect(navigator.nextPage(NetProfitId).apply(updatedData)))
        }
      )
    val updatedData = request.sessionData
    Future.successful(Redirect(navigator.nextPage(NetProfitId).apply(updatedData)))
  }

}
