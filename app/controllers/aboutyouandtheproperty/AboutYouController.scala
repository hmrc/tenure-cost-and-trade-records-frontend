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

package controllers.aboutyouandtheproperty

import actions.WithSessionRefiner
import form.aboutyouandtheproperty.AboutYouForm.aboutYouForm
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty.updateAboutYouAndTheProperty
import navigation.AboutYouNavigator
import navigation.identifiers.AboutYouPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.aboutyouandtheproperty.aboutYou

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class AboutYouController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYouNavigator,
  aboutYouView: aboutYou,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        aboutYouView(
          request.sessionData.aboutYouAndTheProperty.flatMap(_.customerDetails) match {
            case Some(customerDetails) => aboutYouForm.fillAndValidate(customerDetails)
            case _                     => aboutYouForm
          }
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    aboutYouForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(aboutYouView(formWithErrors))),
        data => {
          val updatedData = updateAboutYouAndTheProperty(_.copy(customerDetails = Some(data)))
          session.saveOrUpdate(updatedData)

          if (request.body.asFormUrlEncoded.flatMap(_.get("save_button")).isDefined) {
            Future.successful(Redirect(controllers.routes.SaveAsDraftController.customPassword(request.path)))
          } else {
            Future.successful(Redirect(navigator.nextPage(AboutYouPageId).apply(updatedData)))
          }
        }
      )
  }
}
