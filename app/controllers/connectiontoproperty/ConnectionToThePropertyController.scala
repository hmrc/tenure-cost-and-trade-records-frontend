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
import connectors.Audit
import controllers.FORDataCaptureController
import form.connectiontoproperty.ConnectionToThePropertyForm.connectionToThePropertyForm
import models.Session
import models.audit.ChangeLinkAudit
import models.submissions.connectiontoproperty.ConnectionToProperty
import navigation.ConnectionToPropertyNavigator
import navigation.identifiers.ConnectionToPropertyPageId
import play.api.Logging
import models.submissions.connectiontoproperty.StillConnectedDetails.updateStillConnectedDetails
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.connectiontoproperty.connectionToTheProperty

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ConnectionToThePropertyController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: ConnectionToPropertyNavigator,
  connectionToThePropertyView: connectionToTheProperty,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        connectionToThePropertyView(
          request.sessionData.stillConnectedDetails.flatMap(_.connectionToProperty) match {
            case Some(connectionToProperty) => connectionToThePropertyForm.fill(connectionToProperty)
            case _                          => connectionToThePropertyForm
          },
          getBackLink(request.sessionData) match {
            case Right(link) => link
            case Left(msg)   =>
              logger.warn(s"Navigation for connection to property page reached with error: $msg")
              throw new RuntimeException(s"Navigation for connection to property page reached with error $msg")
          },
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[ConnectionToProperty](
      connectionToThePropertyForm,
      formWithErrors =>
        BadRequest(
          connectionToThePropertyView(
            formWithErrors,
            controllers.connectiontoproperty.routes.AreYouStillConnectedController.show().url,
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedData = updateStillConnectedDetails(_.copy(connectionToProperty = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map(_ => Redirect(navigator.nextPage(ConnectionToPropertyPageId, updatedData).apply(updatedData)))

      }
    )
  }

  private def getBackLink(answers: Session): Either[String, String] =
    answers.stillConnectedDetails.flatMap(_.addressConnectionType.map(_.name)) match {
      case Some("yes-change-address") => Right(controllers.connectiontoproperty.routes.EditAddressController.show().url)
      case Some("yes")                => Right(controllers.connectiontoproperty.routes.AreYouStillConnectedController.show().url)
      case _                          => Left(s"Unknown connection to property back link")
    }
}
