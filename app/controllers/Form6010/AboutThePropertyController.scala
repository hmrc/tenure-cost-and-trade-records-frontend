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
import views.html.Form6010.{aboutTheProperty, aboutThePropertyOther, websiteForProperty}
import form.Form6010.AboutThePropertyForm.aboutThePropertyForm
import form.Form6010.AboutThePropertyOtherForm.aboutThePropertyOtherForm
import form.Form6010.WebsiteForPropertyForm.websiteForPropertyForm
import models.submissions.Form6010.CurrentPropertyOther

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class AboutThePropertyController @Inject() (
  mcc: MessagesControllerComponents,
  websiteForPropertyView: websiteForProperty,
  aboutThePropertyOtherView: aboutThePropertyOther,
  aboutThePropertyView: aboutTheProperty
) extends FrontendController(mcc) {

  def show: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(aboutThePropertyView(aboutThePropertyForm)))
  }

  def submit = Action.async { implicit request =>
    aboutThePropertyForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(aboutThePropertyView(formWithErrors))),
        data =>
          data.propertyCurrentlyUsed match {
            case CurrentPropertyOther => Future.successful(Ok(aboutThePropertyOtherView(aboutThePropertyOtherForm)))
            case _                    => Future.successful(Ok(websiteForPropertyView(websiteForPropertyForm)))
          }
      )
  }

}
