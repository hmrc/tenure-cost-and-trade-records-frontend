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
import form.EditAddressForm.editAddressForm
import models.Session
import models.submissions.StillConnectedDetails.updateStillConnectedDetails
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.{connectionToTheProperty, editAddress}

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class EditAddressController @Inject() (
  mcc: MessagesControllerComponents,
  connectionToThePropertyView: connectionToTheProperty,
  editAddressView: editAddress,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        editAddressView(
          request.sessionData.stillConnectedDetails match {
            case Some(stillConnectedDetails) => stillConnectedDetails.editAddress match {
              case Some(address) => editAddressForm.fillAndValidate(address)
              case _ => editAddressForm
            }
            case _ => editAddressForm
          }
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    editAddressForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(editAddressView(formWithErrors))),
        data => {
          session.saveOrUpdate(updateStillConnectedDetails(_.copy(editAddress = Some(data))))
          Future.successful(Ok(connectionToThePropertyView(connectionToThePropertyForm)))
        }
      )
  }

}
