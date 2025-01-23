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

import actions.{SessionRequest, WithSessionRefiner}
import controllers.FORDataCaptureController
import form.aboutyouandtheproperty.RenewablesPlantForm.renewablesPlantForm
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty.updateAboutYouAndTheProperty
import models.submissions.aboutyouandtheproperty.RenewablesPlant
import models.submissions.common.{AnswerNo, AnswerYes}
import navigation.AboutYouAndThePropertyNavigator
import navigation.identifiers.RenewablesPlantPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutyouandtheproperty.renewablesPlant

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RenewablesPlantController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYouAndThePropertyNavigator,
  view: renewablesPlant,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        view(
          request.sessionData.aboutYouAndTheProperty.flatMap(_.renewablesPlant) match {
            case Some(data) => renewablesPlantForm.fill(data)
            case _          => renewablesPlantForm
          },
          calculateBackLink(request),
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[RenewablesPlant](
      renewablesPlantForm,
      formWithErrors =>
        BadRequest(
          view(formWithErrors, calculateBackLink(request), request.sessionData.toSummary)
        ),
      data => {
        val updatedData = updateAboutYouAndTheProperty(_.copy(renewablesPlant = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map(_ => Redirect(navigator.nextPage(RenewablesPlantPageId, updatedData).apply(updatedData)))
      }
    )
  }

  private def calculateBackLink(implicit request: SessionRequest[AnyContent]) =
    navigator.from match {
      case "CYA" => controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show().url
      case "TL"  => controllers.routes.TaskListController.show().url + "#technology-type"
      case _     =>
        request.sessionData.aboutYouAndTheProperty.flatMap(_.altDetailsQuestion).map(_.contactDetailsQuestion) match {
          case Some(AnswerYes) =>
            controllers.aboutyouandtheproperty.routes.AlternativeContactDetailsController.show().url
          case Some(AnswerNo)  =>
            controllers.aboutyouandtheproperty.routes.ContactDetailsQuestionController.show().url
          case _               => controllers.routes.TaskListController.show().url
        }

    }
}
