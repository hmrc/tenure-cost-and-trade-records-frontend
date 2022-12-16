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

import actions.{SessionRequest, WithSessionRefiner}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.Form6010.aboutYou
import views.html.taskList
import form.Form6010.AboutYouForm.aboutYouForm
import models.Session
import models.submissions.SectionOne.updateSectionOne
import play.api.i18n.I18nSupport
import repositories.SessionRepo

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class AboutYouController @Inject() (
  mcc: MessagesControllerComponents,
  taskListView: taskList,
  aboutYouView: aboutYou,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        aboutYouView(
          request.sessionData.sectionOne match {
            case Some(sectionOne) =>
              sectionOne.customerDetails match {
                case Some(customerDetails) => aboutYouForm.fillAndValidate(customerDetails)
                case _                     => aboutYouForm
              }
            case _                => aboutYouForm
          }
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    aboutYouForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(aboutYouView(formWithErrors))),
        data => {
          session.saveOrUpdate(updateSectionOne(_.copy(customerDetails = Some(data))))
          Future.successful(Ok(taskListView()))
        }
      )
  }
}
