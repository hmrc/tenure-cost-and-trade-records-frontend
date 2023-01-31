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

import actions.WithSessionRefiner
import controllers.LoginController.loginForm
import form.Form6010.AddAnotherCateringOperationOrLettingAccommodationForm.addAnotherCateringOperationOrLettingAccommodationForm
import form.Form6010.LettingOtherPartOfPropertiesForm.lettingOtherPartOfPropertiesForm
import form.Form6010.CateringOperationOrLettingAccommodationForm.cateringOperationOrLettingAccommodationForm
import models.submissions.Form6010.{AddAnotherCateringOperationOrLettingAccommodationNo, AddAnotherCateringOperationOrLettingAccommodationYes}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.form.{addAnotherCateringOperationOrLettingAccommodation, cateringOperationOrLettingAccommodationDetails, lettingOtherPartOfProperty}
import views.html.login

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class AddAnotherCateringOperationOrLettingAccommodationController @Inject() (
  mcc: MessagesControllerComponents,
  addAnotherCateringOperationOrLettingAccommodationView: addAnotherCateringOperationOrLettingAccommodation,
  login: login,
  cateringOperationOrLettingAccommodationDetailsView: cateringOperationOrLettingAccommodationDetails,
  lettingOtherPartOfPropertyView: lettingOtherPartOfProperty,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc) with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(addAnotherCateringOperationOrLettingAccommodationView(addAnotherCateringOperationOrLettingAccommodationForm))
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    addAnotherCateringOperationOrLettingAccommodationForm
      .bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(BadRequest(addAnotherCateringOperationOrLettingAccommodationView(formWithErrors))),
        data =>
          data.addAnotherCateringOperationOrLettingAccommodationDetails match {
            case AddAnotherCateringOperationOrLettingAccommodationYes =>
              Future.successful(
                Ok(cateringOperationOrLettingAccommodationDetailsView(cateringOperationOrLettingAccommodationForm, None))
              )
            case AddAnotherCateringOperationOrLettingAccommodationNo  =>
              Future.successful(Ok(lettingOtherPartOfPropertyView(lettingOtherPartOfPropertiesForm)))
            case _                                                    => Future.successful(Ok(login(loginForm)))
          }
      )
  }

}
