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

package controllers.Form6010

import config.AppConfig
import controllers.LoginController.loginForm
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.Form6010.{areYouStillConnected, connectionToTheProperty, editAddress}
import views.html.login
import form.AreYouStillConnectedForm.areYouStillConnectedForm
import form.ConnectionToThePropertyForm.connectionToThePropertyForm
import form.EditAddressForm.editAddressForm
import form.FormDocumentRepository
import models.submissions.{AddressConnectionTypeNo, AddressConnectionTypeYes, AddressConnectionTypeYesChangeAddress}

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class AreYouStillConnectedController @Inject() (
  mcc: MessagesControllerComponents,
  formDocumentRepository: FormDocumentRepository,
  appConfig: AppConfig,
  login: login,
  areYouStillConnectedView: areYouStillConnected,
  connectionToThePropertyView: connectionToTheProperty,
  editAddressView: editAddress
) extends FrontendController(mcc) {

  def show: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(areYouStillConnectedView(areYouStillConnectedForm)))
  }

  def submit = Action.async { implicit request =>
    areYouStillConnectedForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(areYouStillConnectedView(formWithErrors))),
        data => {
          if (data.equals(AddressConnectionTypeYes)) {
            Future.successful(Ok(connectionToThePropertyView(connectionToThePropertyForm)))
          } else if (data.equals(AddressConnectionTypeNo)) {
            Future.successful(Ok(login(loginForm)))
          } else if (data.equals(AddressConnectionTypeYesChangeAddress)) {
            Future.successful(Ok(editAddressView(editAddressForm)))
          } else {
            Future.successful(Ok(login(loginForm)))
          }
        }
      )
  }
}
