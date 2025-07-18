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
import form.aboutyouandtheproperty.PropertyCurrentlyUsedForm.propertyCurrentlyUsedForm
import models.Session
import models.submissions.aboutyouandtheproperty.AboutYouAndThePropertyPartTwo.updateAboutYouAndThePropertyPartTwo
import models.submissions.aboutyouandtheproperty.PropertyCurrentlyUsed
import models.submissions.common.AnswersYesNo.*
import navigation.AboutYouAndThePropertyNavigator
import navigation.identifiers.PropertyCurrentlyUsedPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request}
import repositories.SessionRepo
import views.html.aboutyouandtheproperty.propertyCurrentlyUsed

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PropertyCurrentlyUsedController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYouAndThePropertyNavigator,
  view: propertyCurrentlyUsed,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    audit.sendChangeLink("PropertyCurrentlyUsed")
    Future.successful(
      Ok(
        view(
          request.sessionData.aboutYouAndThePropertyPartTwo.flatMap(_.propertyCurrentlyUsed) match {
            case Some(propertyDetails) => propertyCurrentlyUsedForm.fill(propertyDetails)
            case _                     => propertyCurrentlyUsedForm
          },
          request.sessionData.toSummary,
          backLink(request.sessionData)
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[PropertyCurrentlyUsed](
      propertyCurrentlyUsedForm,
      formWithErrors =>
        BadRequest(
          view(
            formWithErrors,
            request.sessionData.toSummary,
            backLink(request.sessionData)
          )
        ),
      data => {
        val updatedData = updateAboutYouAndThePropertyPartTwo(_.copy(propertyCurrentlyUsed = Some(data)))
        session.saveOrUpdate(updatedData).map { _ =>
          Redirect(navigator.nextPage(PropertyCurrentlyUsedPageId, updatedData).apply(updatedData))
        }
      }
    )
  }

  private def backLink(answers: Session)(implicit request: Request[AnyContent]): String =
    navigator.from match {
      case "TL"  => controllers.routes.TaskListController.show().url
      case "CYA" => routes.CheckYourAnswersAboutThePropertyController.show().url
      case _     =>
        answers.aboutYouAndTheProperty.flatMap(_.altDetailsQuestion) match {
          case Some(AnswerYes) =>
            controllers.aboutyouandtheproperty.routes.AlternativeContactDetailsController.show().url
          case _               => controllers.aboutyouandtheproperty.routes.ContactDetailsQuestionController.show().url
        }
    }

}
