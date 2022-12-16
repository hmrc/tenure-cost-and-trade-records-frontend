/*
 * Copyright 2022 HM Revenue & Customs
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
import form.ConnectionToThePropertyForm.connectionToThePropertyForm
import models.Session
import models.submissions.ConnectionToThePropertyOwnerTrustee
import models.submissions.StillConnectedDetails.updateStillConnectedDetails
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.{connectionToTheProperty, taskList, taskListOwner}

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class ConnectionToThePropertyController @Inject() (
  mcc: MessagesControllerComponents,
  taskListView: taskList,
  taskListOwnerView: taskListOwner,
  connectionToThePropertyView: connectionToTheProperty,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        connectionToThePropertyView(
          request.sessionData.stillConnectedDetails match {
            case Some(stillConnectedDetails) =>
              stillConnectedDetails.connectionToProperty match {
                case Some(connectionToProperty) => connectionToThePropertyForm.fillAndValidate(connectionToProperty)
                case _                          => connectionToThePropertyForm
              }
            case _                           => connectionToThePropertyForm
          }
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    connectionToThePropertyForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(connectionToThePropertyView(formWithErrors))),
        data =>
          data match {
            case ConnectionToThePropertyOwnerTrustee =>
              session.saveOrUpdate(updateStillConnectedDetails(_.copy(connectionToProperty = Some(data))))
              Future.successful(Ok(taskListOwnerView()))
            case _                                   =>
              session.saveOrUpdate(updateStillConnectedDetails(_.copy(connectionToProperty = Some(data))))
              Future.successful(Ok(taskListView()))
          }
      )
  }
}
