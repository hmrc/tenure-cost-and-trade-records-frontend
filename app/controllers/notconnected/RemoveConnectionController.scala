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

package controllers.notconnected

import actions.{SessionRequest, WithSessionRefiner}
import controllers.FORDataCaptureController
import form.notconnected.RemoveConnectionForm.removeConnectionForm
import models.submissions.notconnected.RemoveConnectionDetails.updateRemoveConnectionDetails
import models.submissions.notconnected.RemoveConnectionsDetails
import play.api.i18n.I18nSupport
import navigation.RemoveConnectionNavigator
import navigation.identifiers.RemoveConnectionId
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.notconnected.removeConnection

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class RemoveConnectionController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: RemoveConnectionNavigator,
  removeConnectionView: removeConnection,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        removeConnectionView(
          request.sessionData.removeConnectionDetails.flatMap(_.removeConnectionDetails) match {
            case Some(removeConnectionDetails) => removeConnectionForm.fill(removeConnectionDetails)
            case _                             => removeConnectionForm
          },
          request.sessionData.toSummary,
          calculateBackLink
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[RemoveConnectionsDetails](
      removeConnectionForm,
      formWithErrors =>
        BadRequest(removeConnectionView(formWithErrors, request.sessionData.toSummary, calculateBackLink)),
      data => {
        val updatedData = updateRemoveConnectionDetails(_.copy(removeConnectionDetails = Some(data)))
        session.saveOrUpdate(updatedData)
        Redirect(navigator.nextPage(RemoveConnectionId, updatedData).apply(updatedData))
      }
    )
  }

  private def calculateBackLink(implicit request: SessionRequest[AnyContent]) =
    navigator.from match {
      case "CYA" => controllers.notconnected.routes.CheckYourAnswersNotConnectedController.show().url
      case _     => controllers.notconnected.routes.PastConnectionController.show().url
    }

}
