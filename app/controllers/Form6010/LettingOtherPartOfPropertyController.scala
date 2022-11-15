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
import views.html.Form6010.{aboutYourLandlord, lettingOtherPartOfProperty, lettingOtherPartOfPropertyDetails}
import form.Form6010.LettingOtherPartOfPropertyForm.lettingOtherPartOfPropertyForm
import form.Form6010.AboutTheLandlordForm.aboutTheLandlordForm
import form.Form6010.LettingOtherPartOfPropertiesForm.lettingOtherPartOfPropertiesForm
import models.submissions.Form6010.{LettingOtherPartOfPropertiesNo, LettingOtherPartOfPropertiesYes}
import views.html.login

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class LettingOtherPartOfPropertyController @Inject() (
  mcc: MessagesControllerComponents,
  login: login,
  lettingOtherPartOfPropertyDetailsView: lettingOtherPartOfPropertyDetails,
  lettingOtherPartOfPropertyView: lettingOtherPartOfProperty,
  aboutTheLandlordView: aboutYourLandlord
) extends FrontendController(mcc) {

  def show: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(lettingOtherPartOfPropertyView(lettingOtherPartOfPropertiesForm)))
  }

  def submit = Action.async { implicit request =>
    lettingOtherPartOfPropertiesForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(lettingOtherPartOfPropertyView(formWithErrors))),
        data =>
          if (data.lettingOtherPartOfProperties.equals(LettingOtherPartOfPropertiesYes)) {
          Future.successful(Ok(lettingOtherPartOfPropertyDetailsView(lettingOtherPartOfPropertyForm)))
        } else if (data.lettingOtherPartOfProperties.equals(LettingOtherPartOfPropertiesNo)) {
          Future.successful(Ok(aboutTheLandlordView(aboutTheLandlordForm)))
        } else {
          Future.successful(Ok(login(loginForm)))
        }
      )
  }
}
