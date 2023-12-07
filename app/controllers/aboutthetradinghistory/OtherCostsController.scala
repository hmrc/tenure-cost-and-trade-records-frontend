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
import form.aboutthetradinghistory.OtherCostsForm.otherCostsForm
import models.submissions.aboutthetradinghistory.OtherCosts
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.OtherCostsId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.otherCosts

import javax.inject.{Inject, Named, Singleton}

@Singleton
class OtherCostsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  otherCostsView: otherCosts,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    Ok(
      otherCostsView(
        request.sessionData.aboutTheTradingHistory.flatMap(_.otherCosts) match {
          case Some(otherCosts) => otherCostsForm.fill(otherCosts)
          case _                => otherCostsForm
        },
        request.sessionData.toSummary
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[OtherCosts](
      otherCostsForm,
      formWithErrors => BadRequest(otherCostsView(formWithErrors, request.sessionData.toSummary)),
      data => {
        val updatedData = request.sessionData
        Redirect(navigator.nextPage(OtherCostsId, updatedData).apply(updatedData))
      }
    )
  }

}
