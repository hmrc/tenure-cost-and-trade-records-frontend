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

import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.Form6010.{aboutYou, taskList}
import form.AboutYouForm.aboutYouForm

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class AboutYouController @Inject() (mcc: MessagesControllerComponents, taskListView: taskList, aboutYouView: aboutYou)
    extends FrontendController(mcc) {

  def show: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(aboutYouView(aboutYouForm)))
  }

  def submit = Action.async { implicit request =>
    aboutYouForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(aboutYouView(formWithErrors))),
        data => Future.successful(Ok(taskListView()))
      )
  }
}
