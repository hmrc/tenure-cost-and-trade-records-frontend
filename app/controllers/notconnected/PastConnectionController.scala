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

package controllers.notconnected

import actions.{SessionRequest, WithSessionRefiner}
import controllers.FORDataCaptureController
import form.notconnected.PastConnectionForm.pastConnectionForm
import models.submissions.notconnected.RemoveConnectionDetails.updateRemoveConnectionDetails
import models.submissions.common.AnswersYesNo
import navigation.RemoveConnectionNavigator
import navigation.identifiers.PastConnectionId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.notconnected.pastConnection

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PastConnectionController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: RemoveConnectionNavigator,
  pastConnectionView: pastConnection,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        pastConnectionView(
          request.sessionData.removeConnectionDetails
            .flatMap(_.pastConnectionType)
            .fold(pastConnectionForm)(pastConnectionForm.fill),
          request.sessionData.toSummary,
          calculateBackLink
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      pastConnectionForm,
      formWithErrors =>
        BadRequest(pastConnectionView(formWithErrors, request.sessionData.toSummary, calculateBackLink)),
      data => {
        val updatedData = updateRemoveConnectionDetails(_.copy(pastConnectionType = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map(_ => Redirect(navigator.nextPage(PastConnectionId, updatedData).apply(updatedData)))

      }
    )
  }

  private def calculateBackLink(implicit request: SessionRequest[AnyContent]) =
    navigator.from match {
      case "CYA" => controllers.notconnected.routes.CheckYourAnswersNotConnectedController.show().url
      case _     => controllers.connectiontoproperty.routes.AreYouStillConnectedController.show().url
    }

}
