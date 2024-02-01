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
import form.aboutyouandtheproperty.AboutTheProperty6030Form.aboutTheProperty6030Form
import form.aboutyouandtheproperty.AboutThePropertyForm.aboutThePropertyForm
import models.Session
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty.updateAboutYouAndTheProperty
import models.submissions.aboutyouandtheproperty.{PropertyDetails, PropertyDetails6030}
import navigation.AboutYouAndThePropertyNavigator
import navigation.identifiers.AboutThePropertyPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request}
import repositories.SessionRepo
import views.html.aboutyouandtheproperty.{aboutTheProperty, aboutTheProperty6030}

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class AboutThePropertyController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYouAndThePropertyNavigator,
  aboutThePropertyView: aboutTheProperty,
  aboutTheProperty6030View: aboutTheProperty6030,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        aboutThePropertyView(
          request.sessionData.aboutYouAndTheProperty.flatMap(_.propertyDetails) match {
            case Some(propertyDetails) => aboutThePropertyForm.fill(propertyDetails)
            case _                     => aboutThePropertyForm
          },
          request.sessionData.forType,
          request.sessionData.toSummary,
          backLink(request.sessionData)
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    val forType         = request.sessionData.forType

    if (forType.equals("FOR6030")) {
      continueOrSaveAsDraft[PropertyDetails6030](
        aboutTheProperty6030Form,
        formWithErrors =>
          BadRequest(
            aboutTheProperty6030View(
              formWithErrors,
              request.sessionData.forType,
              request.sessionData.toSummary,
              backLink(request.sessionData)
            )
          ),
        data => {
          val updatedData = updateAboutYouAndTheProperty(_.copy(propertyDetails = Some(data)))
          session.saveOrUpdate(updatedData)
          Redirect(navigator.nextPage(AboutThePropertyPageId, updatedData).apply(updatedData))
        }
      )
    } else {
      continueOrSaveAsDraft[PropertyDetails](
        aboutThePropertyForm,
        formWithErrors =>
          BadRequest(
            aboutThePropertyView(
              formWithErrors,
              request.sessionData.forType,
              request.sessionData.toSummary,
              backLink(request.sessionData)
            )
          ),
        data => {
          val updatedData = updateAboutYouAndTheProperty(_.copy(propertyDetails = Some(data)))
          session.saveOrUpdate(updatedData)
          Redirect(navigator.nextPage(AboutThePropertyPageId, updatedData).apply(updatedData))
        }
      )
    }
  }

  private def backLink(answers: Session)(implicit request: Request[AnyContent]): String =
    navigator.from match {
      case "TL" => controllers.routes.TaskListController.show().url + "#about-the-property"
      case _    =>
        answers.aboutYouAndTheProperty.flatMap(
          _.altDetailsQuestion.map(_.contactDetailsQuestion.name)
        ) match {
          case Some("yes") =>
            controllers.aboutyouandtheproperty.routes.AlternativeContactDetailsController.show.url
          case Some("no")  => controllers.aboutyouandtheproperty.routes.ContactDetailsQuestionController.show.url
          case _           =>
            logger.warn(s"Back link for alternative contact page reached with unknown enforcement taken value")
            controllers.routes.TaskListController.show().url
        }
    }

}
