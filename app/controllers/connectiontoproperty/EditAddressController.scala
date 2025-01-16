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
import controllers.FORDataCaptureController
import form.connectiontoproperty.EditAddressForm.editAddressForm
import models.submissions.connectiontoproperty.EditTheAddress
import navigation.ConnectionToPropertyNavigator
import navigation.identifiers.EditAddressPageId
import models.submissions.connectiontoproperty.StillConnectedDetails.updateStillConnectedDetails
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.connectiontoproperty.editAddress

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EditAddressController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: ConnectionToPropertyNavigator,
  editAddressView: editAddress,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val containCYA = request.uri
    val forType = request.sessionData.forType

    containCYA match {
      case containsCYA if containsCYA.contains("=CYA") =>
        audit.sendExplicitAudit("cya-change-link", ChangeLinkAudit(forType.toString, request.uri, "EditAddress"))
      case _ =>
        Future.successful(
          Ok(
            editAddressView(
              request.sessionData.stillConnectedDetails.flatMap(_.editAddress) match {
                case Some(editAddress) => editAddressForm.fill(editAddress)
                case _ => editAddressForm
              },
              request.sessionData.toSummary,
              calculateBackLink
            )
          )
        )
    }
    Future.successful(
      Ok(
        editAddressView(
          request.sessionData.stillConnectedDetails.flatMap(_.editAddress) match {
            case Some(editAddress) => editAddressForm.fill(editAddress)
            case _ => editAddressForm
          },
          request.sessionData.toSummary,
          calculateBackLink
        )
      )
    )
  }


  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[EditTheAddress](
      editAddressForm,
      formWithErrors => BadRequest(editAddressView(formWithErrors, request.sessionData.toSummary, calculateBackLink)),
      data => {
        val updatedData = updateStillConnectedDetails(_.copy(editAddress = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map(_ =>
            navigator
              .cyaPageDependsOnSession(updatedData)
              .filter(_ => navigator.from == "CYA")
              .getOrElse(navigator.nextWithoutRedirectToCYA(EditAddressPageId, updatedData).apply(updatedData))
          )
          .map(Redirect)
      }
    )
  }

  private def calculateBackLink(implicit request: SessionRequest[AnyContent]) =
    navigator.from match {
      case "CYA" => navigator.cyaPageDependsOnSession(request.sessionData).map(_.url).getOrElse("")
      case _     => controllers.connectiontoproperty.routes.AreYouStillConnectedController.show().url
    }

}
