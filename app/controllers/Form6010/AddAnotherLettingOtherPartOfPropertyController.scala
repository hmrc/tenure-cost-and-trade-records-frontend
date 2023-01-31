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
import form.Form6010.LettingOtherPartOfPropertyForm.lettingOtherPartOfPropertyForm
import form.Form6010.AddAnotherLettingOtherPartOfPropertyForm.addAnotherLettingOtherPartOfPropertyForm
import form.aboutYourLeaseOrTenure.AboutTheLandlordForm.aboutTheLandlordForm
import models.submissions.Form6010._
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.aboutYourLeaseOrTenure.aboutYourLandlord
import views.html.form._
import views.html.login

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class AddAnotherLettingOtherPartOfPropertyController @Inject() (
  mcc: MessagesControllerComponents,
  addAnotherLettingOtherPartOfPropertyView: addAnotherLettingOtherPartOfProperty,
  lettingOtherPartOfPropertyDetailsView: lettingOtherPartOfPropertyDetails,
  login: login,
  aboutTheLandlordView: aboutYourLandlord,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc) with I18nSupport{

  def show(index: Int): Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(addAnotherLettingOtherPartOfPropertyView(addAnotherLettingOtherPartOfPropertyForm, index))
    )
  }

  def submit(index: Int) = (Action andThen withSessionRefiner).async { implicit request =>
    addAnotherLettingOtherPartOfPropertyForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(addAnotherLettingOtherPartOfPropertyView(formWithErrors, index))),
        data =>
          data.addAnotherLettingOtherPartOfPropertyDetails match {
            case AddAnotherLettingOtherPartOfPropertiesYes =>
              Future.successful(
                Ok(lettingOtherPartOfPropertyDetailsView(lettingOtherPartOfPropertyForm, None))
              )
            case AddAnotherLettingOtherPartOfPropertiesNo  =>
              Future.successful(Ok(aboutTheLandlordView(aboutTheLandlordForm)))
            case _                                         => Future.successful(Ok(login(loginForm)))
          }
      )
  }

}
