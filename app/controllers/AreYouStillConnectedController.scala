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
import models.submissions.Form6010.{AddressConnectionTypeNo, AddressConnectionTypeYes, AddressConnectionTypeYesChangeAddress}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.{areYouStillConnected, connectionToTheProperty, editAddress, login}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.Form6010.aboutYou
import views.html.login
import form.Form6010.AboutYouForm
import form.EnumMapping
import repositories.SessionRepo
import models.areYouStillConnectedToAddress
import models.submissions.Form6010.AddressConnectionType
import play.api.data.Form
import play.api.data.Forms.mapping
import models.Session

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class AreYouStillConnectedController @Inject() (
  mcc: MessagesControllerComponents,
  appConfig: AppConfig,
  login: login,
  areYouStillConnectedView: areYouStillConnected,
  connectionToThePropertyView: connectionToTheProperty,
  editAddressView: editAddress,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc) {

  def show: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(areYouStillConnectedView(areYouStillConnectedForm)))
  }

  def submit = Action.async { implicit request =>
    areYouStillConnectedForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(areYouStillConnectedView(formWithErrors))),
        data =>
          if (data.equals(AddressConnectionTypeYes)) {
            session.start(Session(data))
            Future.successful(Ok(connectionToThePropertyView(connectionToThePropertyForm)))
          } else if (data.equals(AddressConnectionTypeNo)) {
            session.start(data)
            Future.successful(Ok(login(loginForm)))
          } else if (data.equals(AddressConnectionTypeYesChangeAddress)) {
            session.start(data)
            Future.successful(Ok(editAddressView(editAddressForm)))
          } else {
            Future.successful(Ok(login(loginForm)))
          }
      )
  }

}
