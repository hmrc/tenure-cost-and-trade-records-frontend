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
import form.aboutyouandtheproperty.AboutThePropertyForm.aboutThePropertyForm
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty.updateAboutYouAndTheProperty
import navigation.AboutThePropertyNavigator
import navigation.identifiers.AboutThePropertyPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.aboutyouandtheproperty.aboutTheProperty

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class AboutThePropertyController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutThePropertyNavigator,
  aboutThePropertyView: aboutTheProperty,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        aboutThePropertyView(
          request.sessionData.aboutYouAndTheProperty.flatMap(_.propertyDetails) match {
            case Some(propertyDetails) => aboutThePropertyForm.fillAndValidate(propertyDetails)
            case _                     => aboutThePropertyForm
          },
          request.sessionData.forType
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    aboutThePropertyForm
      .bindFromRequest()
      .fold(
        formWithErrors =>
          Future.successful(
            BadRequest(aboutThePropertyView(formWithErrors, request.sessionData.forType))
          ),
        data => {
          val updatedData = updateAboutYouAndTheProperty(_.copy(propertyDetails = Some(data)))
          session.saveOrUpdate(updatedData)
          Future.successful(Redirect(navigator.nextPage(AboutThePropertyPageId).apply(updatedData)))
        }
      )
  }

}
