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
import form.aboutthetradinghistory.AboutYourTradingHistoryForm.aboutYourTradingHistoryForm
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory.updateAboutTheTradingHistory
import models.submissions.aboutthetradinghistory.AboutYourTradingHistory
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.AboutYourTradingHistoryPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.aboutYourTradingHistory

import javax.inject.{Inject, Named, Singleton}

@Singleton
class AboutYourTradingHistoryController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutTheTradingHistoryNavigator,
  aboutYourTradingHistoryView: aboutYourTradingHistory,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    Ok(
      aboutYourTradingHistoryView(
        request.sessionData.aboutTheTradingHistory.flatMap(_.aboutYourTradingHistory) match {
          case Some(tradingHistory) => aboutYourTradingHistoryForm.fillAndValidate(tradingHistory)
          case _                    => aboutYourTradingHistoryForm
        }
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AboutYourTradingHistory](
      aboutYourTradingHistoryForm,
      formWithErrors => BadRequest(aboutYourTradingHistoryView(formWithErrors)),
      data => {
        val updatedData = updateAboutTheTradingHistory(_.copy(aboutYourTradingHistory = Some(data)))
        session.saveOrUpdate(updatedData)
        Redirect(navigator.nextPage(AboutYourTradingHistoryPageId).apply(updatedData))
      }
    )
  }
}
