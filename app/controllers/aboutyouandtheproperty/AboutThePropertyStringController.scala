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
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutyouandtheproperty.AboutThePropertyStringForm.aboutThePropertyStringForm
import models.Session
import models.audit.ChangeLinkAudit
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty.updateAboutYouAndTheProperty
import models.submissions.aboutyouandtheproperty.PropertyDetailsString
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
            case Some(propertyDetailsString) => aboutThePropertyStringForm.fill(propertyDetailsString)
            case _                           => aboutThePropertyStringForm
          },
          request.sessionData.forType,
          request.sessionData.toSummary,
          backLink(request.sessionData)
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[PropertyDetailsString](
      aboutThePropertyStringForm,
      formWithErrors =>
        BadRequest(
          aboutThePropertyStringView(
            formWithErrors,
            request.sessionData.forType,
            request.sessionData.toSummary,
            backLink(request.sessionData)
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

  private def backLink(answers: Session)(implicit request: Request[AnyContent]): String =
    navigator.from match {
      case "TL" => controllers.routes.TaskListController.show().url + "#about-the-property"
      case _    =>
        answers.aboutYouAndTheProperty.flatMap(
          _.altDetailsQuestion.map(_.contactDetailsQuestion.name)
        ) match {
          case Some("yes") =>
            controllers.aboutyouandtheproperty.routes.AlternativeContactDetailsController.show().url
          case Some("no")  => controllers.aboutyouandtheproperty.routes.ContactDetailsQuestionController.show().url
          case _           =>
            logger.warn(s"Back link for alternative contact page reached with unknown enforcement taken value")
            controllers.routes.TaskListController.show().url
        }
    }

}
