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
import controllers.FORDataCaptureController
import form.aboutyouandtheproperty.WebsiteForPropertyForm.websiteForPropertyForm
import models.{ForTypes, Session}
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty.updateAboutYouAndTheProperty
import models.submissions.aboutyouandtheproperty.WebsiteForPropertyDetails
import navigation.AboutYouAndThePropertyNavigator
import navigation.identifiers.WebsiteForPropertyPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutyouandtheproperty.websiteForProperty

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class WebsiteForPropertyController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYouAndThePropertyNavigator,
  websiteForPropertyView: websiteForProperty,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        websiteForPropertyView(
          request.sessionData.aboutYouAndTheProperty.flatMap(_.websiteForPropertyDetails) match {
            case Some(websiteForPropertyDetails) => websiteForPropertyForm.fill(websiteForPropertyDetails)
            case _                               => websiteForPropertyForm
          },
          request.sessionData.toSummary,
          backLink(request.sessionData)
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[WebsiteForPropertyDetails](
      websiteForPropertyForm,
      formWithErrors =>
        BadRequest(
          websiteForPropertyView(
            formWithErrors,
            request.sessionData.toSummary,
            backLink(request.sessionData)
          )
        ),
      data => {
        val updatedData = updateAboutYouAndTheProperty(_.copy(websiteForPropertyDetails = Some(data)))
        session.saveOrUpdate(updatedData)
        Redirect(navigator.nextPage(WebsiteForPropertyPageId, updatedData).apply(updatedData))
      }
    )
  }

  private def backLink(answers: Session): String =
    answers.forType match {
      case ForTypes.for6030 | ForTypes.for6020 =>
        controllers.aboutyouandtheproperty.routes.AboutThePropertyStringController.show().url
      case ForTypes.for6045 | ForTypes.for6046 =>
        controllers.aboutyouandtheproperty.routes.PropertyCurrentlyUsedController.show().url
      case _                                   => controllers.aboutyouandtheproperty.routes.AboutThePropertyController.show().url
    }
}
