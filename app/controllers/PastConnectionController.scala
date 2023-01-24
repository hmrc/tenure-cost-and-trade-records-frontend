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

package controllers

import actions.WithSessionRefiner
import form.notconnected.RemoveConnectionForm.removeConnectionForm
import form.PastConnectionForm.pastConnectionForm
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.pastConnection
import models.submissions.connectiontoproperty.StillConnectedDetails.updateStillConnectedDetails
import views.html.notconnected.removeConnection

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class PastConnectionController @Inject() (
  mcc: MessagesControllerComponents,
  pastConnectionView: pastConnection,
  removeConnectionView: removeConnection,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        pastConnectionView(
          request.sessionData.stillConnectedDetails match {
            case Some(stillConnectedDetails) =>
              stillConnectedDetails.pastConnectionType match {
                case Some(pastConnection) => pastConnectionForm.fillAndValidate(pastConnection)
                case _                    => pastConnectionForm
              }
            case _                           => pastConnectionForm
          }
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    pastConnectionForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(pastConnectionView(formWithErrors))),
        data => {
          session.saveOrUpdate(updateStillConnectedDetails(_.copy(pastConnectionType = Some(data))))
          Future.successful(Ok(removeConnectionView(removeConnectionForm)))
        }
      )
  }

}
