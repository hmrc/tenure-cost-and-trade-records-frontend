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
import form.Form6010.LettingOtherPartOfPropertyForm.lettingOtherPartOfPropertyForm
import form.Form6010.AddAnotherLettingOtherPartOfPropertyForm.addAnotherLettingOtherPartOfPropertyForm
import form.Form6010.AboutTheLandlordForm.aboutTheLandlordForm
import form.Form6010.LettingOtherPartOfPropertiesForm.lettingOtherPartOfPropertiesForm
import models.submissions.Form6010._
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.Form6010._
import views.html.{login, taskList}

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class AddAnotherLettingOtherPartOfPropertyController @Inject()(
                                                                mcc: MessagesControllerComponents,
                                                                websiteAddressForPropertyView: websiteAddressForProperty,
                                                                addAnotherLettingOtherPartOfPropertyView: addAnotherLettingOtherPartOfProperty,
                                                                lettingOtherPartOfPropertyDetailsView: lettingOtherPartOfPropertyDetails,
                                                                login: login,
                                                                aboutTheLandlordView: aboutYourLandlord,
                                                                lettingOtherPartOfPropertyView: lettingOtherPartOfProperty
                                                              ) extends FrontendController(mcc) {

  def show: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(
      Ok(addAnotherLettingOtherPartOfPropertyView(addAnotherLettingOtherPartOfPropertyForm))
    )
  }

  def submit = Action.async { implicit request =>
    addAnotherLettingOtherPartOfPropertyForm
      .bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(BadRequest(addAnotherLettingOtherPartOfPropertyView(formWithErrors))),
        data =>
          data.addAnotherLettingOtherPartOfPropertyDetails match {
            case AddAnotherLettingOtherPartOfPropertiesYes =>
              Future.successful(
                Ok(lettingOtherPartOfPropertyDetailsView(lettingOtherPartOfPropertyForm))
              )
            case AddAnotherLettingOtherPartOfPropertiesNo  =>
              Future.successful(Ok(aboutTheLandlordView(aboutTheLandlordForm)))
            case _                                                    => Future.successful(Ok(login(loginForm)))
          }
      )
  }

}