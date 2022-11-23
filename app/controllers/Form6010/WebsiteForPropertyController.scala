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
import form.Form6010.WebsiteAddressForPropertyForm.websiteAddressForPropertyForm
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.Form6010.{websiteAddressForProperty, websiteForProperty}
import form.Form6010.WebsiteForPropertyForm.websiteForPropertyForm
import models.submissions.Form6010.{BuildingOperationHaveAWebsiteNo, BuildingOperationHaveAWebsiteYes}
import sun.security.jgss.GSSUtil.login
import views.html.{login, taskList}

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class WebsiteForPropertyController @Inject() (
  mcc: MessagesControllerComponents,
  websiteAddressForPropertyView: websiteAddressForProperty,
  websiteForPropertyView: websiteForProperty,
  taskListView: taskList,
  login: login
) extends FrontendController(mcc) {

  def show: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(websiteForPropertyView(websiteForPropertyForm)))
  }

  def submit = Action.async { implicit request =>
    websiteForPropertyForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(websiteForPropertyView(formWithErrors))),
        data =>
          data.buildingOperatingHaveAWebsite match {
            case BuildingOperationHaveAWebsiteYes =>
              Future.successful(Ok(websiteAddressForPropertyView(websiteAddressForPropertyForm)))
            case BuildingOperationHaveAWebsiteNo  => Future.successful(Ok(taskListView()))
            case _                                => Future.successful(Ok(login(loginForm)))
          }
      )
  }

}
