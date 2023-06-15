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
import scala.concurrent.Future

@Singleton
class EditAddressController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: ConnectionToPropertyNavigator,
  editAddressView: editAddress,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        editAddressView(
          request.sessionData.stillConnectedDetails.flatMap(_.editAddress) match {
            case Some(editAddress) => editAddressForm.fillAndValidate(editAddress)
            case _                 => editAddressForm
          },
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[EditTheAddress](
      editAddressForm,
      formWithErrors => BadRequest(editAddressView(formWithErrors,
        request.sessionData.toSummary)),
      data => {
        val updatedData = updateStillConnectedDetails(_.copy(editAddress = Some(data)))
        session.saveOrUpdate(updatedData)
        Redirect(navigator.nextPage(EditAddressPageId, updatedData).apply(updatedData))
      }
    )
  }

}
