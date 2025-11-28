/*
 * Copyright 2025 HM Revenue & Customs
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
import form.aboutyouandtheproperty.AboutThePropertyStringForm.aboutThePropertyStringForm
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty.updateAboutYouAndTheProperty
import navigation.AboutYouAndThePropertyNavigator
import navigation.identifiers.AboutThePropertyPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request}
import repositories.SessionRepo
import views.html.aboutyouandtheproperty.aboutThePropertyString

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AboutThePropertyStringController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYouAndThePropertyNavigator,
  aboutThePropertyStringView: aboutThePropertyString,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("AboutThePropertyString")

    Future.successful(
      Ok(
        aboutThePropertyStringView(
          request.sessionData.aboutYouAndTheProperty.flatMap(_.propertyDetailsString) match {
            case Some(propertyDetails) => aboutThePropertyStringForm.fill(propertyDetails)
            case _                     => aboutThePropertyStringForm
          },
          request.sessionData.forType,
          request.sessionData.toSummary,
          backLink
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[String](
      aboutThePropertyStringForm,
      formWithErrors =>
        BadRequest(
          aboutThePropertyStringView(
            formWithErrors,
            request.sessionData.forType,
            request.sessionData.toSummary,
            backLink
          )
        ),
      data => {
        val updatedData = updateAboutYouAndTheProperty(_.copy(propertyDetailsString = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map(_ => Redirect(navigator.nextPage(AboutThePropertyPageId, updatedData).apply(updatedData)))
      }
    )
  }

  private def backLink(implicit request: Request[AnyContent]): String =
    navigator.from match {
      case "TL" => controllers.routes.TaskListController.show().url + "#about-the-property"
      case _    => controllers.aboutyouandtheproperty.routes.ContactDetailsQuestionController.show().url
    }

}
