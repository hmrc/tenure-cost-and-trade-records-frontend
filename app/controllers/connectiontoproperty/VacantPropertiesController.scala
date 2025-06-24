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

package controllers.connectiontoproperty

import actions.{SessionRequest, WithSessionRefiner}
import connectors.Audit
import controllers.FORDataCaptureController
import form.connectiontoproperty.VacantPropertiesForm.theForm
import models.submissions.common.AnswersYesNo
import models.submissions.connectiontoproperty.StillConnectedDetails.updateStillConnectedDetails
import models.submissions.connectiontoproperty.AddressConnectionType.*
import navigation.ConnectionToPropertyNavigator
import navigation.identifiers.VacantPropertiesPageId
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.connectiontoproperty.vacantProperties as VacantPropertiesView

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class VacantPropertiesController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: ConnectionToPropertyNavigator,
  theView: VacantPropertiesView,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") repo: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport
    with ReadOnlySupport
    with Logging:

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    audit.sendChangeLink("VacantProperties")
    val freshForm  = theForm
    val filledForm =
      for
        stillConnectedDetails <- request.sessionData.stillConnectedDetails
        isPropertyVacant      <- stillConnectedDetails.isPropertyVacant
      yield theForm.fill(isPropertyVacant)

    Ok(
      theView(
        filledForm.getOrElse(freshForm),
        calculateBackLink,
        request.sessionData.toSummary,
        isReadOnly
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AnswersYesNo](
      theForm,
      formWithErrors =>
        BadRequest(
          theView(
            formWithErrors,
            calculateBackLink,
            request.sessionData.toSummary,
            isReadOnly
          )
        ),
      data => {
        val updatedData = updateStillConnectedDetails(_.copy(isPropertyVacant = Some(data)))
        repo
          .saveOrUpdate(updatedData)
          .map(_ =>
            navigator
              .cyaPageDependsOnSession(updatedData)
              .filter(_ =>
                navigator.from == "CYA" &&
                  request.sessionData.stillConnectedDetails
                    .flatMap(_.isPropertyVacant)
                    .contains(data)
              )
              .getOrElse(navigator.nextWithoutRedirectToCYA(VacantPropertiesPageId, updatedData).apply(updatedData))
          )
          .map(Redirect)
      }
    )
  }

  private def calculateBackLink(implicit request: SessionRequest[AnyContent]) =
    navigator.from match {
      case "CYA" => navigator.cyaPageDependsOnSession(request.sessionData).map(_.url).getOrElse("")
      case "TL"  => controllers.routes.TaskListController.show().url + "#vacant-properties"
      case _     =>
        request.sessionData.stillConnectedDetails.flatMap(_.addressConnectionType) match {
          case Some(AddressConnectionTypeYesChangeAddress) => routes.EditAddressController.show().url
          case _                                           => routes.AreYouStillConnectedController.show().url
        }
    }
