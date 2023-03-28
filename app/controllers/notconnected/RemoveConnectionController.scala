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
import form.notconnected.RemoveConnectionForm.removeConnectionForm
import models.submissions.notconnected.RemoveConnectionDetails.updateRemoveConnectionDetails
import play.api.i18n.I18nSupport
import navigation.RemoveConnectionNavigator
import navigation.identifiers.RemoveConnectionId
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
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
) extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        removeConnectionView(
          request.sessionData.removeConnectionDetails.flatMap(_.removeConnectionDetails) match {
            case Some(removeConnectionDetails) => removeConnectionForm.fillAndValidate(removeConnectionDetails)
            case _                             => removeConnectionForm
          }
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    removeConnectionForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(removeConnectionView(formWithErrors))),
        data => {
          val updatedData = updateRemoveConnectionDetails(_.copy(removeConnectionDetails = Some(data)))
          session.saveOrUpdate(updatedData)
          Future.successful(Redirect(navigator.nextPage(RemoveConnectionId).apply(updatedData)))
        }
      )
  }

}
