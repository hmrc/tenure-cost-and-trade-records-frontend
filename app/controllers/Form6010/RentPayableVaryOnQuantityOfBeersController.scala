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
import views.html.Form6010.{howIsCurrentRentFixed, rentPayableVaryOnQuantityOfBeers, rentPayableVaryOnQuantityOfBeersDetails}
import form.Form6010.RentPayableVaryOnQuantityOfBeersForm.rentPayableVaryOnQuantityOfBeersForm
import form.Form6010.RentPayableVaryOnQuantityOfBeersDetailsForm.rentPayableVaryOnQuantityOfBeersDetailsForm
import form.Form6010.HowIsCurrentRentFixedForm.howIsCurrentRentFixedForm
import models.submissions.Form6010.{RentPayableVaryOnQuantityOfBeersNo, RentPayableVaryOnQuantityOfBeersYes}
import views.html.login

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class RentPayableVaryOnQuantityOfBeersController @Inject() (
  mcc: MessagesControllerComponents,
  login: login,
  howIsCurrentRentFixedView: howIsCurrentRentFixed,
  rentPayableVaryOnQuantityOfBeersDetailsView: rentPayableVaryOnQuantityOfBeersDetails,
  rentPayableVaryOnQuantityOfBeersView: rentPayableVaryOnQuantityOfBeers
) extends FrontendController(mcc) {

  def show: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(rentPayableVaryOnQuantityOfBeersView(rentPayableVaryOnQuantityOfBeersForm)))
  }

  def submit = Action.async { implicit request =>
    rentPayableVaryOnQuantityOfBeersForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(rentPayableVaryOnQuantityOfBeersView(formWithErrors))),
        data =>
          if (data.rentPayableVaryOnQuantityOfBeersDetails.equals(RentPayableVaryOnQuantityOfBeersYes)) {
            Future.successful(Ok(rentPayableVaryOnQuantityOfBeersDetailsView(rentPayableVaryOnQuantityOfBeersDetailsForm)))
          } else if (data.rentPayableVaryOnQuantityOfBeersDetails.equals(RentPayableVaryOnQuantityOfBeersNo)) {
            Future.successful(Ok(howIsCurrentRentFixedView(howIsCurrentRentFixedForm)))
          } else {
            Future.successful(Ok(login(loginForm)))
          }
      )
  }
}
