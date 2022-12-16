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
import config.AppConfig
import controllers.LoginController.loginForm
import form.AreYouStillConnectedForm.areYouStillConnectedForm
import form.ConnectionToThePropertyForm.connectionToThePropertyForm
import form.EditAddressForm.editAddressForm
import form.PastConnectionForm.pastConnectionForm
import models.submissions.Form6010.{AddressConnectionTypeNo, AddressConnectionTypeYes, AddressConnectionTypeYesChangeAddress}
import views.html.{areYouStillConnected, connectionToTheProperty, editAddress, login, pastConnection}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import repositories.SessionRepo
import models.Session
import play.api.i18n.I18nSupport
import models.submissions.StillConnectedDetails.updateStillConnectedDetails

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AreYouStillConnectedController @Inject() (
  mcc: MessagesControllerComponents,
  login: login,
  areYouStillConnectedView: areYouStillConnected,
  pastConnectionView: pastConnection,
  connectionToThePropertyView: connectionToTheProperty,
  editAddressView: editAddress,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        areYouStillConnectedView(
          request.sessionData.stillConnectedDetails match {
            case Some(stillConnectedDetails) => stillConnectedDetails.addressConnectionType match {
              case Some(addressConnectionType) => areYouStillConnectedForm.fillAndValidate(addressConnectionType)
              case _ => areYouStillConnectedForm
            }
            case _ => areYouStillConnectedForm
          }
          )
        )
      )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    areYouStillConnectedForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(areYouStillConnectedView(formWithErrors))),
        data =>
          if (data.equals(AddressConnectionTypeYes)) {
            session.saveOrUpdate(updateStillConnectedDetails(_.copy(addressConnectionType = Some(data))))
            Future.successful(Ok(connectionToThePropertyView(connectionToThePropertyForm)))
          } else if (data.equals(AddressConnectionTypeNo)) {
            session.saveOrUpdate(updateStillConnectedDetails(_.copy(addressConnectionType = Some(data))))
            Future.successful(Ok(pastConnectionView(pastConnectionForm)))
          } else if (data.equals(AddressConnectionTypeYesChangeAddress)) {
            session.saveOrUpdate(updateStillConnectedDetails(_.copy(addressConnectionType = Some(data))))
            Future.successful(Ok(editAddressView(editAddressForm)))
          } else {
            Future.successful(Ok(login(loginForm)))
          }
      )
  }

}
