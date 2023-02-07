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
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.form.{cateringOperationOrLettingAccommodation, franchiseOrLettingsTiedToProperty}
import views.html.aboutYourLeaseOrTenure.aboutYourLandlord
import form.additionalinformation.FranchiseOrLettingsTiedToPropertyForm.franchiseOrLettingsTiedToPropertyForm
import form.Form6010.CateringOperationForm.cateringOperationForm
import form.aboutYourLeaseOrTenure.AboutTheLandlordForm.aboutTheLandlordForm
import models.submissions.Form6010.{FranchiseOrLettingsTiedToPropertiesNo, FranchiseOrLettingsTiedToPropertiesYes}
import play.api.i18n.I18nSupport
import repositories.SessionRepo
import views.html.login
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class FranchiseOrLettingsTiedToPropertyController @Inject() (
  mcc: MessagesControllerComponents,
  login: login,
  aboutYourLandlordView: aboutYourLandlord,
  cateringOperationOrLettingAccommodationView: cateringOperationOrLettingAccommodation,
  franchiseOrLettingsTiedToPropertyView: franchiseOrLettingsTiedToProperty,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        franchiseOrLettingsTiedToPropertyView(
          request.sessionData.aboutFranchisesOrLettings.flatMap(_.franchisesOrLettingsTiedToProperty) match {
            case Some(franchisesOrLettingsTiedToProperty) =>
              franchiseOrLettingsTiedToPropertyForm.fillAndValidate(franchisesOrLettingsTiedToProperty)
            case _                                        => franchiseOrLettingsTiedToPropertyForm
          }
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    franchiseOrLettingsTiedToPropertyForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(franchiseOrLettingsTiedToPropertyView(formWithErrors))),
        data =>
          data match {
            case FranchiseOrLettingsTiedToPropertiesYes =>
              val updatedData = updateAboutFranchisesOrLettings(_.copy(franchisesOrLettingsTiedToProperty = Some(data)))
              session.saveOrUpdate(updatedData)
              Future.successful(Ok(cateringOperationOrLettingAccommodationView(
                cateringOperationForm,
                "cateringOperationOrLettingAccommodation",
                controllers.Form6010.routes.FranchiseOrLettingsTiedToPropertyController.show().url)))
            case FranchiseOrLettingsTiedToPropertiesNo  =>
              val updatedData = updateAboutFranchisesOrLettings(_.copy(franchisesOrLettingsTiedToProperty = Some(data)))
              session.saveOrUpdate(updatedData)
              Future.successful(Ok(aboutYourLandlordView(aboutTheLandlordForm)))
            case _                                      => Future.successful(Ok(login(loginForm)))
          }
      )
  }

}
