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

package controllers.Form6010

import controllers.LoginController.loginForm
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.form.{cateringOperationOrLettingAccommodation, franchiseOrLettingsTiedToProperty}
import form.additionalinformation.FranchiseOrLettingsTiedToPropertyForm.franchiseOrLettingsTiedToPropertyForm
import form.Form6010.CateringOperationForm.cateringOperationForm
import form.aboutYourLeaseOrTenure.AboutTheLandlordForm.aboutTheLandlordForm
import models.submissions.additionalinformation.{FranchiseOrLettingsTiedToPropertiesNo, FranchiseOrLettingsTiedToPropertiesYes}
import views.html.aboutYourLeaseOrTenure.aboutYourLandlord
import views.html.login

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class FranchiseOrLettingsTiedToPropertyController @Inject() (
  mcc: MessagesControllerComponents,
  login: login,
  aboutYourLandlordView: aboutYourLandlord,
  cateringOperationOrLettingAccommodationView: cateringOperationOrLettingAccommodation,
  franchiseOrLettingsTiedToPropertyView: franchiseOrLettingsTiedToProperty
) extends FrontendController(mcc) {

  def show: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(franchiseOrLettingsTiedToPropertyView(franchiseOrLettingsTiedToPropertyForm)))
  }

  def submit = Action.async { implicit request =>
    franchiseOrLettingsTiedToPropertyForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(franchiseOrLettingsTiedToPropertyView(formWithErrors))),
        data =>
          data.franchiseOrLettingsTiedToProperty match {
            case FranchiseOrLettingsTiedToPropertiesYes =>
              Future.successful(Ok(cateringOperationOrLettingAccommodationView(cateringOperationForm)))
            case FranchiseOrLettingsTiedToPropertiesNo  =>
              Future.successful(Ok(aboutYourLandlordView(aboutTheLandlordForm)))
            case _                                      => Future.successful(Ok(login(loginForm)))
          }
      )
  }

}
