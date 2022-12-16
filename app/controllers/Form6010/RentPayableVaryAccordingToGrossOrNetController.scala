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
import views.html.form.{rentPayableVaryAccordingToGrossOrNet, rentPayableVaryAccordingToGrossOrNetDetails, rentPayableVaryOnQuantityOfBeers}
import form.Form6010.RentPayableVaryAccordingToGrossOrNetForm.rentPayableVaryAccordingToGrossOrNetForm
import form.Form6010.RentPayableVaryAccordingToGrossOrNetDetailsForm.rentPayableVaryAccordingToGrossOrNetInformationForm
import form.Form6010.RentPayableVaryOnQuantityOfBeersForm.rentPayableVaryOnQuantityOfBeersForm
import models.submissions.Form6010.{RentPayableVaryAccordingToGrossOrNetsNo, RentPayableVaryAccordingToGrossOrNetsYes}
import views.html.login

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class RentPayableVaryAccordingToGrossOrNetController @Inject() (
  mcc: MessagesControllerComponents,
  login: login,
  rentPayableVaryAccordingToGrossOrNetView: rentPayableVaryAccordingToGrossOrNet,
  rentPayableVaryAccordingToGrossOrNetDetailsView: rentPayableVaryAccordingToGrossOrNetDetails,
  rentPayableVaryOnQuantityOfBeersView: rentPayableVaryOnQuantityOfBeers
) extends FrontendController(mcc) {

  def show: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(rentPayableVaryAccordingToGrossOrNetView(rentPayableVaryAccordingToGrossOrNetForm)))
  }

  def submit = Action.async { implicit request =>
    rentPayableVaryAccordingToGrossOrNetForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(rentPayableVaryAccordingToGrossOrNetView(formWithErrors))),
        data =>
          data.rentPayableVaryAccordingToGrossOrNets match {
            case RentPayableVaryAccordingToGrossOrNetsYes =>
              Future.successful(
                Ok(rentPayableVaryAccordingToGrossOrNetDetailsView(rentPayableVaryAccordingToGrossOrNetInformationForm))
              )
            case RentPayableVaryAccordingToGrossOrNetsNo  =>
              Future.successful(Ok(rentPayableVaryOnQuantityOfBeersView(rentPayableVaryOnQuantityOfBeersForm)))
            case _                                        => Future.successful(Ok(login(loginForm)))
          }
      )
  }
}
