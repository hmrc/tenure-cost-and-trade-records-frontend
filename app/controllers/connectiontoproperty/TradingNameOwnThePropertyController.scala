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

package controllers.connectiontoproperty

import actions.WithSessionRefiner
import controllers.FORDataCaptureController
import models.Session
import models.submissions.common.AnswersYesNo
import navigation.ConnectionToPropertyNavigator
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.connectiontoproperty.tradingNameOwnTheProperty
import form.connectiontoproperty.TradingNameOwnThePropertyForm.tradingNameOwnThePropertyForm
import models.submissions.connectiontoproperty.StillConnectedDetails.updateStillConnectedDetails
import navigation.identifiers.TradingNameOwnThePropertyPageId

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class TradingNameOwnThePropertyController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: ConnectionToPropertyNavigator,
  tradingNameOwnThePropertyView: tradingNameOwnTheProperty,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        tradingNameOwnThePropertyView(
          request.sessionData.stillConnectedDetails.flatMap(_.tradingNameOwnTheProperty) match {
            case Some(enforcementAction) => tradingNameOwnThePropertyForm.fillAndValidate(enforcementAction)
            case _                       => tradingNameOwnThePropertyForm
          },
          getBackLink(request.sessionData),
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      tradingNameOwnThePropertyForm,
      formWithErrors =>
        BadRequest(
          tradingNameOwnThePropertyView(
            formWithErrors,
            getBackLink(request.sessionData),
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedData = updateStillConnectedDetails(_.copy(tradingNameOwnTheProperty = Some(data)))
        session.saveOrUpdate(updatedData)
        Redirect(navigator.nextPage(TradingNameOwnThePropertyPageId, updatedData).apply(updatedData))
      }
    )
  }

  private def getBackLink(answers: Session): String =
    controllers.connectiontoproperty.routes.TradingNameOperatingFromPropertyController.show().url
}
