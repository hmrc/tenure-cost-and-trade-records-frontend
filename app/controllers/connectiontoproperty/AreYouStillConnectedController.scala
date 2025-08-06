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
import form.connectiontoproperty.AreYouStillConnectedForm.theForm
import navigation.ConnectionToPropertyNavigator
import navigation.identifiers.AreYouStillConnectedPageId
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepo
import views.html.connectiontoproperty.areYouStillConnected as AreYouStillConnectedView
import models.submissions.connectiontoproperty.StillConnectedDetails.updateStillConnectedDetails
import models.submissions.connectiontoproperty.AddressConnectionType
import models.submissions.connectiontoproperty.AddressConnectionType.*

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class AreYouStillConnectedController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  navigator: ConnectionToPropertyNavigator,
  theView: AreYouStillConnectedView,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") repo: SessionRepo
)(using ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with ReadOnlySupport
    with I18nSupport:

  def show: Action[AnyContent] = (Action andThen withSessionRefiner) { implicit request =>
    audit.sendChangeLink("AreYouStillConnected")
    val freshForm  = theForm
    val filledForm = addressConnectionType.map(theForm.fill)
    Ok(
      theView(
        filledForm.getOrElse(freshForm),
        request.sessionData.toSummary,
        calculateBackLink,
        isReadOnly
      )
    )
  }

  def submit: Action[AnyContent]                                              = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AddressConnectionType](
      theForm,
      formWithErrors =>
        BadRequest(
          theView(formWithErrors, request.sessionData.toSummary, calculateBackLink, isReadOnly)
        ),
      data => {
        val updatedData = updateStillConnectedDetails(_.copy(addressConnectionType = Some(data)))
        repo
          .saveOrUpdate(updatedData)
          .map(_ =>
            navigator
              .cyaPageDependsOnSession(updatedData)
              .filter(_ => navigator.from == "CYA" && addressConnectionType.contains(data))
              .getOrElse(
                if (data == AddressConnectionTypeNo || addressConnectionType.contains(AddressConnectionTypeNo)) {
                  navigator.nextWithoutRedirectToCYA(AreYouStillConnectedPageId, updatedData).apply(updatedData)
                } else if (navigator.from == "CYA" && data == AddressConnectionTypeYes) {
                  navigator.cyaPageDependsOnSession(updatedData).orElse(navigator.cyaPage).get
                } else {
                  navigator.nextPage(AreYouStillConnectedPageId, updatedData).apply(updatedData)
                }
              )
          )
          .map(Redirect)
      }
    )
  }
  private def calculateBackLink(implicit request: SessionRequest[AnyContent]) =
    navigator.from match {
      case "CYA" => navigator.cyaPageDependsOnSession(request.sessionData).map(_.url).getOrElse("")
      case "TL"  => controllers.routes.TaskListController.show().url
      case _     => controllers.routes.LoginController.show.url
    }

  private def addressConnectionType(using r: SessionRequest[AnyContent]) =
    r.sessionData.stillConnectedDetails.flatMap(_.addressConnectionType)
