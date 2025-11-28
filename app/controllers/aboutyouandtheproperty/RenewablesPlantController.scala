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

import actions.{SessionRequest, WithSessionRefiner}
import connectors.Audit
import controllers.FORDataCaptureController
import form.aboutyouandtheproperty.RenewablesPlantForm.theForm
import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty.updateAboutYouAndTheProperty
import models.submissions.aboutyouandtheproperty.RenewablesPlantType
import navigation.AboutYouAndThePropertyNavigator
import navigation.identifiers.RenewablesPlantPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutyouandtheproperty.renewablesPlant as RenewablesPlantView

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class RenewablesPlantController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: AboutYouAndThePropertyNavigator,
  theView: RenewablesPlantView,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") repo: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with ReadOnlySupport
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    audit.sendChangeLink("RenewablesPlant")
    Ok(
      theView(
        request.sessionData.aboutYouAndTheProperty.flatMap(_.renewablesPlant) match {
          case Some(data) => theForm.fill(data)
          case _          => theForm
        },
        calculateBackLink(using request),
        request.sessionData.toSummary,
        isReadOnly
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[RenewablesPlantType](
      theForm,
      formWithErrors =>
        BadRequest(
          theView(formWithErrors, calculateBackLink(using request), request.sessionData.toSummary, isReadOnly)
        ),
      data => {
        val updatedData = updateAboutYouAndTheProperty(_.copy(renewablesPlant = Some(data)))
        repo
          .saveOrUpdate(updatedData)
          .map(_ => Redirect(navigator.nextPage(RenewablesPlantPageId, updatedData).apply(updatedData)))
      }
    )
  }

  private def calculateBackLink(implicit request: SessionRequest[AnyContent]) =
    navigator.from match {
      case "CYA" => controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show().url
      case "TL"  => controllers.routes.TaskListController.show().url + "#technology-type"
      case _     => controllers.aboutyouandtheproperty.routes.ContactDetailsQuestionController.show().url
    }
}
