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
import form.aboutthetradinghistory.GrossProfitForm.grossProfitForm
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.GrossProfitsId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.aboutthetradinghistory.grossProfits

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class GrossProfitsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  grossProfitsView: grossProfits,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    Ok(
      grossProfitsView(
        request.sessionData.aboutTheTradingHistory.flatMap(_.grossProfit) match {
          case Some(grossProfit) => grossProfitForm.fillAndValidate(grossProfit)
          case _                 => grossProfitForm
        }
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    grossProfitForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(grossProfitsView(formWithErrors))),
        data => {
          val updatedData = request.sessionData
          Future.successful(Redirect(navigator.nextPage(GrossProfitsId).apply(updatedData)))
        }
      )
  }
}
