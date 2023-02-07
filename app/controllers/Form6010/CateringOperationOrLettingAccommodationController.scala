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
import views.html.form.{cateringOperationOrLettingAccommodation, cateringOperationOrLettingAccommodationDetails}
import form.Form6010.CateringOperationForm.cateringOperationForm
import form.Form6010.CateringOperationOrLettingAccommodationForm.cateringOperationOrLettingAccommodationForm
import form.Form6010.LettingOtherPartOfPropertiesForm.lettingOtherPartOfPropertiesForm
import models.submissions.Form6010.{CateringOperationNo, CateringOperationYes}
import views.html.login

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class CateringOperationOrLettingAccommodationController @Inject() (
  mcc: MessagesControllerComponents,
  login: login,
  cateringOperationOrLettingAccommodationDetailsView: cateringOperationOrLettingAccommodationDetails,
  cateringOperationOrLettingAccommodationView: cateringOperationOrLettingAccommodation
) extends FrontendController(mcc) {

  def show: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(
      Ok(
        cateringOperationOrLettingAccommodationView(
          cateringOperationForm,
          "cateringOperationOrLettingAccommodation",
          controllers.Form6010.routes.FranchiseOrLettingsTiedToPropertyController.show().url
        )
      )
    )
  }

  def submit = Action.async { implicit request =>
    cateringOperationForm
      .bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(
            BadRequest(
              cateringOperationOrLettingAccommodationView(
                formWithErrors,
                "cateringOperationOrLettingAccommodation",
                controllers.Form6010.routes.FranchiseOrLettingsTiedToPropertyController.show().url
              )
            )
          ),
        data =>
          data.cateringOperationOrLettingAccommodation match {
            case CateringOperationYes =>
              Future.successful(
                Ok(
                  cateringOperationOrLettingAccommodationDetailsView(
                    cateringOperationOrLettingAccommodationForm,
                    "cateringOperationOrLettingAccommodationDetails",
                    controllers.Form6010.routes.CateringOperationOrLettingAccommodationController.show().url
                  )
                )
              )
            case CateringOperationNo  =>
              Future.successful(
                Ok(
                  cateringOperationOrLettingAccommodationView(
                    lettingOtherPartOfPropertiesForm,
                    "LettingOtherPartOfProperties",
                    controllers.Form6010.routes.CateringOperationOrLettingAccommodationController.show().url
                  )
                )
              )
            case _                    => Future.successful(Ok(login(loginForm)))
          }
      )
  }

}
