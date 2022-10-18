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

import controllers.LoginController.loginForm
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.Form6010.rentOpenMarketValue
import form.RentOpenMarketValueForm.rentOpenMarketValuesForm
import views.html.login

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class RentOpenMarketValueController @Inject() (
  mcc: MessagesControllerComponents,
  login: login,
  rentOpenMarketValueView: rentOpenMarketValue
) extends FrontendController(mcc) {

  def show: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(rentOpenMarketValueView(rentOpenMarketValuesForm)))
  }

  def submit = Action.async { implicit request =>
    rentOpenMarketValuesForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(rentOpenMarketValueView(formWithErrors))),
        data => Future.successful(Ok(login(loginForm)))
      )
  }
}
