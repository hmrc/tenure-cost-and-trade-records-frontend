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
import form.connectiontoproperty.VacantPropertiesForm.vacantPropertiesForm
import models.Session
import models.submissions.connectiontoproperty.StillConnectedDetails.updateStillConnectedDetails
import models.submissions.connectiontoproperty.VacantProperties
import navigation.ConnectionToPropertyNavigator
import navigation.identifiers.VacantPropertiesPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.connectiontoproperty.vacantProperties

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VacantPropertiesController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: ConnectionToPropertyNavigator,
  vacantPropertiesView: vacantProperties,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        vacantPropertiesView(
          request.sessionData.stillConnectedDetails.flatMap(_.vacantProperties) match {
            case Some(vacantProperties) => vacantPropertiesForm.fill(vacantProperties)
            case _                      => vacantPropertiesForm
          },
          getBackLink(request.sessionData),
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[VacantProperties](
      vacantPropertiesForm,
      formWithErrors =>
        BadRequest(
          vacantPropertiesView(
            formWithErrors,
            getBackLink(request.sessionData),
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedData = updateStillConnectedDetails(_.copy(vacantProperties = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map(_ =>
            navigator
              .cyaPageDependsOnSession(updatedData)
              .filter(_ =>
                navigator.from == "CYA" &&
                  request.sessionData.stillConnectedDetails
                    .flatMap(_.vacantProperties)
                    .exists(_.vacantProperties == data.vacantProperties)
              )
              .getOrElse(navigator.next(VacantPropertiesPageId, updatedData).apply(updatedData))
          )
          .map(Redirect)
      }
    )
  }

  private def getBackLink(answers: Session): String =
    answers.stillConnectedDetails.flatMap(_.addressConnectionType.map(_.name)) match {
      case Some("yes-change-address") => controllers.connectiontoproperty.routes.EditAddressController.show().url
      case _                          => controllers.connectiontoproperty.routes.AreYouStillConnectedController.show().url
    }

}
