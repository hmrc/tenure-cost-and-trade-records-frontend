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
import views.html.form.{cateringOperationOrLettingAccommodation, cateringOperationOrLettingAccommodationDetails, lettingOtherPartOfProperty}
import form.Form6010.CateringOperationForm.cateringOperationForm
import form.Form6010.CateringOperationOrLettingAccommodationForm.cateringOperationOrLettingAccommodationForm
import form.Form6010.LettingOtherPartOfPropertiesForm.lettingOtherPartOfPropertiesForm
import play.api.i18n.I18nSupport
import repositories.SessionRepo
import views.html.login
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings.updateAboutFranchisesOrLettings
import models.submissions.aboutfranchisesorlettings.{CateringOperationNo, CateringOperationYes}

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class CateringOperationOrLettingAccommodationController @Inject() (
  mcc: MessagesControllerComponents,
  login: login,
  cateringOperationOrLettingAccommodationDetailsView: cateringOperationOrLettingAccommodationDetails,
  lettingOtherPartOfPropertyView: lettingOtherPartOfProperty,
  cateringOperationOrLettingAccommodationView: cateringOperationOrLettingAccommodation,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc) with I18nSupport{

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        cateringOperationOrLettingAccommodationView(
          request.sessionData.aboutFranchisesOrLettings.flatMap(_.cateringOperationOrLettingAccommodation) match {
            case Some(cateringOperationOrLettingAccommodation) => cateringOperationForm.fillAndValidate(cateringOperationOrLettingAccommodation)
            case _                                             => cateringOperationForm
          }
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    cateringOperationForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(cateringOperationOrLettingAccommodationView(formWithErrors))),
        data =>
          data match {
            case CateringOperationYes =>
              val updatedData = updateAboutFranchisesOrLettings(_.copy(cateringOperationOrLettingAccommodation = Some(data)))
              session.saveOrUpdate(updatedData)
              Future.successful(
                Ok(cateringOperationOrLettingAccommodationDetailsView(cateringOperationOrLettingAccommodationForm, None))
              )
            case CateringOperationNo  =>
              val updatedData = updateAboutFranchisesOrLettings(_.copy(cateringOperationOrLettingAccommodation = Some(data)))
              session.saveOrUpdate(updatedData)
              Future.successful(Ok(lettingOtherPartOfPropertyView(lettingOtherPartOfPropertiesForm)))
            case _                    => Future.successful(Ok(login(loginForm)))
          }
      )
  }

}
