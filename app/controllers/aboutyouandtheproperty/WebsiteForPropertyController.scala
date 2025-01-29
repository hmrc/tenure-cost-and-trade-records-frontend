/*
 * Copyright 2024 HM Revenue & Customs
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
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutyouandtheproperty.WebsiteForPropertyForm.websiteForPropertyForm
import models.ForType.*
import models.Session
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
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WebsiteForPropertyController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYouAndThePropertyNavigator,
  websiteForPropertyView: websiteForProperty,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("WebsiteForProperty")
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
        session
          .saveOrUpdate(updatedData)
          .map(_ => Redirect(navigator.nextPage(WebsiteForPropertyPageId, updatedData).apply(updatedData)))

      }
    )
  }

  private def backLink(answers: Session): String =
    answers.forType match {
      case FOR6030 | FOR6020 =>
        controllers.aboutyouandtheproperty.routes.AboutThePropertyStringController.show().url
      case FOR6045 | FOR6046 =>
        controllers.aboutyouandtheproperty.routes.PropertyCurrentlyUsedController.show().url
      case _                 => controllers.aboutyouandtheproperty.routes.AboutThePropertyController.show().url
    }
}
