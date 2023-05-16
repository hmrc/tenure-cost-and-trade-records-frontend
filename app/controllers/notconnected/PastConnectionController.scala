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

import actions.WithSessionRefiner
import controllers.FORDataCaptureController
import form.notconnected.PastConnectionForm.pastConnectionForm
import models.submissions.notconnected.PastConnectionType
import models.submissions.notconnected.RemoveConnectionDetails.updateRemoveConnectionDetails
import navigation.RemoveConnectionNavigator
import navigation.identifiers.PastConnectionId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.notconnected.pastConnection

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class PastConnectionController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: RemoveConnectionNavigator,
  pastConnectionView: pastConnection,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        pastConnectionView(
          request.sessionData.removeConnectionDetails match {
            case Some(removeConnectionDetails) =>
              removeConnectionDetails.pastConnectionType match {
                case Some(pastConnection) => pastConnectionForm.fillAndValidate(pastConnection)
                case _                    => pastConnectionForm
              }
            case _                             => pastConnectionForm
          },
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[PastConnectionType](
      pastConnectionForm,
      formWithErrors => BadRequest(pastConnectionView(formWithErrors, request.sessionData.toSummary)),
      data => {
        val updatedData = updateRemoveConnectionDetails(_.copy(pastConnectionType = Some(data)))
        session.saveOrUpdate(updatedData)
        Redirect(navigator.nextPage(PastConnectionId, updatedData).apply(updatedData))
      }
    )
  }

}
