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

package controllers.abouttheproperty

import actions.WithSessionRefiner
import form.abouttheproperty.WebsiteForPropertyForm.websiteForPropertyForm
import models.submissions.abouttheproperty.AboutTheProperty.updateAboutTheProperty
import navigation.AboutThePropertyNavigator
import navigation.identifiers.WebsiteForPropertyPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.abouttheproperty.websiteForProperty

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class WebsiteForPropertyController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutThePropertyNavigator,
  websiteForPropertyView: websiteForProperty,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        websiteForPropertyView(
          request.sessionData.aboutTheProperty.flatMap(_.websiteForPropertyDetails) match {
            case Some(websiteForPropertyDetails) => websiteForPropertyForm.fillAndValidate(websiteForPropertyDetails)
            case _                               => websiteForPropertyForm
          }
        )
      )
    )
  }

  //TODO - the view needs to be updated so that if the user selects 'yes' the text field is mandatory. It is currently possible for a user to click 'yes' and enter no data.
  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    websiteForPropertyForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(websiteForPropertyView(formWithErrors))),
        data => {
          val updatedData = updateAboutTheProperty(_.copy(websiteForPropertyDetails = Some(data)))
          session.saveOrUpdate(updatedData)
          Future.successful(Redirect(navigator.nextPage(WebsiteForPropertyPageId).apply(updatedData)))
        }
      )
  }

}
