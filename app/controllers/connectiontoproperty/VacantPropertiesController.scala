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

import actions.{SessionRequest, WithSessionRefiner}
import connectors.Audit
import controllers.FORDataCaptureController
import form.connectiontoproperty.VacantPropertiesForm.vacantPropertiesForm
import models.audit.ChangeLinkAudit
import models.submissions.connectiontoproperty.StillConnectedDetails.updateStillConnectedDetails
import models.submissions.connectiontoproperty.{AddressConnectionTypeYesChangeAddress, VacantProperties}
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
  audit: Audit,
  navigator: ConnectionToPropertyNavigator,
  vacantPropertiesView: vacantProperties,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val containCYA = request.uri
    val forType    = request.sessionData.forType

    containCYA match {
      case containsCYA if containsCYA.contains("=CYA") =>
        audit.sendExplicitAudit("cya-change-link", ChangeLinkAudit(forType.toString, request.uri, "VacantProperties"))
      case _                                           =>
        Future.successful(
          Ok(
            vacantPropertiesView(
              request.sessionData.stillConnectedDetails.flatMap(_.vacantProperties) match {
                case Some(vacantProperties) => vacantPropertiesForm.fill(vacantProperties)
                case _                      => vacantPropertiesForm
              },
              calculateBackLink,
              request.sessionData.toSummary
            )
          )
        )
    }
    Future.successful(
      Ok(
        vacantPropertiesView(
          request.sessionData.stillConnectedDetails.flatMap(_.vacantProperties) match {
            case Some(vacantProperties) => vacantPropertiesForm.fill(vacantProperties)
            case _                      => vacantPropertiesForm
          },
          calculateBackLink,
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
            calculateBackLink,
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
              .getOrElse(navigator.nextWithoutRedirectToCYA(VacantPropertiesPageId, updatedData).apply(updatedData))
          )
          .map(Redirect)
      }
    )
  }

  private def calculateBackLink(implicit request: SessionRequest[AnyContent]) =
    navigator.from match {
      case "CYA" => navigator.cyaPageDependsOnSession(request.sessionData).map(_.url).getOrElse("")
      case "TL"  => controllers.routes.TaskListController.show().url + "#vacant-properties"
      case _     =>
        request.sessionData.stillConnectedDetails.flatMap(_.addressConnectionType) match {
          case Some(AddressConnectionTypeYesChangeAddress) =>
            controllers.connectiontoproperty.routes.EditAddressController.show().url
          case _                                           => controllers.connectiontoproperty.routes.AreYouStillConnectedController.show().url
        }
    }
}
