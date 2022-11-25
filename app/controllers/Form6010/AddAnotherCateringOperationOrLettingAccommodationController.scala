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
import form.Form6010.AddAnotherCateringOperationOrLettingAccommodationForm.addAnotherCateringOperationOrLettingAccommodationForm
import form.Form6010.LettingOtherPartOfPropertiesForm.lettingOtherPartOfPropertiesForm
import form.Form6010.CateringOperationOrLettingAccommodationForm.cateringOperationOrLettingAccommodationForm
import models.submissions.Form6010.{AddAnotherCateringOperationOrLettingAccommodationNo, AddAnotherCateringOperationOrLettingAccommodationYes}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.Form6010.{addAnotherCateringOperationOrLettingAccommodation, cateringOperationOrLettingAccommodationDetails, lettingOtherPartOfProperty}
import views.html.login

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class AddAnotherCateringOperationOrLettingAccommodationController @Inject() (
  mcc: MessagesControllerComponents,
  addAnotherCateringOperationOrLettingAccommodationView: addAnotherCateringOperationOrLettingAccommodation,
  login: login,
  cateringOperationOrLettingAccommodationDetailsView: cateringOperationOrLettingAccommodationDetails,
  lettingOtherPartOfPropertyView: lettingOtherPartOfProperty
) extends FrontendController(mcc) {

  def show: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(
      Ok(addAnotherCateringOperationOrLettingAccommodationView(addAnotherCateringOperationOrLettingAccommodationForm))
    )
  }

  def submit = Action.async { implicit request =>
    addAnotherCateringOperationOrLettingAccommodationForm
      .bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(BadRequest(addAnotherCateringOperationOrLettingAccommodationView(formWithErrors))),
        data =>
          data.addAnotherCateringOperationOrLettingAccommodationDetails match {
            case AddAnotherCateringOperationOrLettingAccommodationYes =>
              Future.successful(
                Ok(cateringOperationOrLettingAccommodationDetailsView(cateringOperationOrLettingAccommodationForm))
              )
            case AddAnotherCateringOperationOrLettingAccommodationNo  =>
              Future.successful(Ok(lettingOtherPartOfPropertyView(lettingOtherPartOfPropertiesForm)))
            case _                                                    => Future.successful(Ok(login(loginForm)))
          }
      )
  }

}
