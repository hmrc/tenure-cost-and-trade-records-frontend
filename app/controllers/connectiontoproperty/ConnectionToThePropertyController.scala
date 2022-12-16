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

package controllers.connectiontoproperty

import actions.WithSessionRefiner
import form.connectiontoproperty.ConnectionToThePropertyForm.connectionToThePropertyForm
import models.Session
import navigation.ConnectionToPropertyNavigator
import navigation.identifiers.ConnectionToPropertyPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.connectiontoproperty._

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class ConnectionToThePropertyController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: ConnectionToPropertyNavigator,
  connectionToThePropertyView: connectionToTheProperty,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        connectionToThePropertyView(
          request.sessionData.connectionToProperty.fold(connectionToThePropertyForm)(connectionToProperty =>
            connectionToThePropertyForm.fillAndValidate(connectionToProperty)
          ),
          getBackLink(request.sessionData) match {
            case Right(link) => link
            case Left(msg)   =>
              logger.warn(s"Navigation for connection to property page reached with error: $msg")
              throw new RuntimeException(s"Navigation for connection to property page reached with error $msg")
          }
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    connectionToThePropertyForm
      .bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(
            BadRequest(
              connectionToThePropertyView(
                formWithErrors,
                controllers.connectiontoproperty.routes.AreYouStillConnectedController.show().url
              )
            )
          ),
        data => {
          val updatedData = request.sessionData.copy(connectionToProperty = Some(data))
          session.saveOrUpdate(request.sessionData.copy(connectionToProperty = Some(data)))
          Future.successful(Redirect(navigator.nextPage(ConnectionToPropertyPageId).apply(updatedData)))
        }
      )
  }

  private def getBackLink(answers: Session): Either[String, String] =
    (answers.addressConnectionType.map(_.name)) match {
      case Some("yes-change-address") => Right(controllers.connectiontoproperty.routes.EditAddressController.show().url)
      case Some("yes")                => Right(controllers.connectiontoproperty.routes.AreYouStillConnectedController.show().url)
      case _                          => Left(s"Unknown connection to property back link")
    }
}
