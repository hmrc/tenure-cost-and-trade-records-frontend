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

package controllers.aboutfranchisesorlettings

import actions.WithSessionRefiner
import controllers.LoginController.loginForm
import form.Form6010.LettingOtherPartOfPropertiesForm.lettingOtherPartOfPropertiesForm
import form.Form6010.LettingOtherPartOfPropertyForm.lettingOtherPartOfPropertyForm
import form.aboutYourLeaseOrTenure.AboutTheLandlordForm.aboutTheLandlordForm
import models.submissions.Form6010.{LettingOtherPartOfPropertiesNo, LettingOtherPartOfPropertiesYes}
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.aboutYourLeaseOrTenure.aboutYourLandlord
import views.html.form.{cateringOperationOrLettingAccommodation, cateringOperationOrLettingAccommodationDetails}
import views.html.login

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class LettingOtherPartOfPropertyController @Inject() (
  mcc: MessagesControllerComponents,
  login: login,
  cateringOperationOrLettingAccommodationDetailsView: cateringOperationOrLettingAccommodationDetails,
  cateringOperationOrLettingAccommodationView: cateringOperationOrLettingAccommodation,
  aboutTheLandlordView: aboutYourLandlord,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        cateringOperationOrLettingAccommodationView(
          request.sessionData.aboutFranchisesOrLettings.flatMap(_.lettingOtherPartOfProperty) match {
            case Some(lettingOtherPartOfProperty) =>
              lettingOtherPartOfPropertiesForm.fillAndValidate(lettingOtherPartOfProperty)
            case _                                => lettingOtherPartOfPropertiesForm
          },
          "lettingOtherPartOfProperties",
          controllers.aboutfranchisesorlettings.routes.CateringOperationController.show().url
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    lettingOtherPartOfPropertiesForm
      .bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(
            BadRequest(
              cateringOperationOrLettingAccommodationView(
                formWithErrors,
                "lettingOtherPartOfProperties",
                controllers.aboutfranchisesorlettings.routes.CateringOperationController.show().url
              )
            )
          ),
        data =>
          data match {
            case LettingOtherPartOfPropertiesYes =>
              val updatedData = updateAboutFranchisesOrLettings(_.copy(lettingOtherPartOfProperty = Some(data)))
              session.saveOrUpdate(updatedData)
              Future.successful(
                Ok(
                  cateringOperationOrLettingAccommodationDetailsView(
                    lettingOtherPartOfPropertyForm,
                    None,
                    "lettingOtherPartOfPropertyDetails",
                    controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyController.show().url
                  )
                )
              )
            case LettingOtherPartOfPropertiesNo  =>
              val updatedData = updateAboutFranchisesOrLettings(_.copy(lettingOtherPartOfProperty = Some(data)))
              session.saveOrUpdate(updatedData)
              Future.successful(Ok(aboutTheLandlordView(aboutTheLandlordForm)))
            case _                               => Future.successful(Ok(login(loginForm)))
          }
      )
  }
}
