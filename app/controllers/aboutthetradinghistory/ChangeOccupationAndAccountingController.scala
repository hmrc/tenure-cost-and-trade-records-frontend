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

import actions.WithSessionRefiner
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutthetradinghistory.ChangeOccupationAndAccountingForm.changeOccupationAndAccountingForm
import models.submissions.common.AnswersYesNo
import models.submissions.common.AnswersYesNo.*
import navigation.AboutTheTradingHistoryNavigator
import navigation.identifiers.ChangeOccupationAndAccountingId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutthetradinghistory.changeOccupationAndAccountingInfo

import javax.inject.{Inject, Named, Singleton}

/**
  * @author Yuriy Tumakha
  */
@Singleton
class ChangeOccupationAndAccountingController @Inject() (
  changeOccupationAndAccountingInfoView: changeOccupationAndAccountingInfo,
  audit: Audit,
  navigator: AboutTheTradingHistoryNavigator,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo,
  mcc: MessagesControllerComponents
) extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("ChangeOccupationAndAccounting")

    Ok(changeOccupationAndAccountingInfoView(changeOccupationAndAccountingForm))
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val sessionData = request.sessionData

    continueOrSaveAsDraft[AnswersYesNo](
      changeOccupationAndAccountingForm,
      formWithErrors => BadRequest(changeOccupationAndAccountingInfoView(formWithErrors)),
      data =>
        Redirect(
          if data == AnswerYes then
            navigator.nextWithoutRedirectToCYA(ChangeOccupationAndAccountingId, sessionData).apply(sessionData)
          else navigator.cyaPage.get
        )
    )
  }

}
