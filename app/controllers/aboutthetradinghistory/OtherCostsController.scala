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
import form.aboutthetradinghistory.OtherCostsForm.otherCostsForm
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.OtherCostsId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.aboutthetradinghistory.otherCosts

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class OtherCostsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  otherCostsView: otherCosts,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    Ok(
      otherCostsView(
        request.sessionData.aboutTheTradingHistory.flatMap(_.otherCosts) match {
          case Some(otherCosts) => otherCostsForm.fillAndValidate(otherCosts)
          case _                => otherCostsForm
        }
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    otherCostsForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(otherCostsView(formWithErrors))),
        data => {
          val updatedData = request.sessionData
          Future.successful(Redirect(navigator.nextPage(OtherCostsId).apply(updatedData)))
        }
      )
  }

}
