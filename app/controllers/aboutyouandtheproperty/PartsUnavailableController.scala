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
import form.aboutyouandtheproperty.PartsUnavailableForm.partsUnavailableForm
import models.submissions.aboutyouandtheproperty.AboutYouAndThePropertyPartTwo.updateAboutYouAndThePropertyPartTwo
import models.submissions.common.AnswersYesNo
import navigation.AboutYouAndThePropertyNavigator
import navigation.identifiers.PartsUnavailableId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.aboutyouandtheproperty.partsUnavailable

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PartsUnavailableController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: AboutYouAndThePropertyNavigator,
  view: partsUnavailable,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with Logging {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        view(
          request.sessionData.aboutYouAndThePropertyPartTwo.flatMap(_.partsUnavailable) match {
            case Some(tiedForGoods) => partsUnavailableForm.fill(tiedForGoods)
            case _                  => partsUnavailableForm
          },
          calculateBackLink,
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      partsUnavailableForm,
      formWithErrors =>
        BadRequest(
          view(
            formWithErrors,
            calculateBackLink,
            request.sessionData.toSummary
          )
        ),
      data => {
        val updatedData = updateAboutYouAndThePropertyPartTwo(_.copy(partsUnavailable = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map(_ => Redirect(navigator.nextPage(PartsUnavailableId, updatedData).apply(updatedData)))
      }
    )
  }

  private def calculateBackLink(implicit request: SessionRequest[AnyContent]): String =
    navigator.from match {
      case "CYA" => controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show().url
      case "TL"  => s"${controllers.routes.TaskListController.show().url}#family-usage"
      case _     =>
        if (request.sessionData.isWelsh) {
          controllers.aboutyouandtheproperty.routes.CompletedCommercialLettingsWelshController.show().url
        } else {
          controllers.aboutyouandtheproperty.routes.CompletedCommercialLettingsController.show().url
        }
    }
}
