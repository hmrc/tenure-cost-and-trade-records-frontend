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

package controllers.connectiontoproperty

import actions.WithSessionRefiner
import controllers.FORDataCaptureController
import form.connectiontoproperty.AreYouStillConnectedForm.areYouStillConnectedForm
import navigation.ConnectionToPropertyNavigator
import navigation.identifiers.AreYouStillConnectedPageId
import play.api.i18n.I18nSupport
import play.api.mvc._
import repositories.SessionRepo
import views.html.connectiontoproperty.areYouStillConnected
import models.submissions.connectiontoproperty.StillConnectedDetails.updateStillConnectedDetails
import models.submissions.connectiontoproperty.AddressConnectionType

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AreYouStillConnectedController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: ConnectionToPropertyNavigator,
  areYouStillConnectedView: areYouStillConnected,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit val ec: ExecutionContext)
    extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        areYouStillConnectedView(
          request.sessionData.stillConnectedDetails.flatMap(_.addressConnectionType) match {
            case Some(addressConnectionType) => areYouStillConnectedForm.fill(addressConnectionType)
            case _                           => areYouStillConnectedForm
          },
          request.sessionData.toSummary
        )
      )
    )
  }

  def submit = (Action andThen withSessionRefiner).async { implicit request =>
    continueOrSaveAsDraft[AddressConnectionType](
      areYouStillConnectedForm,
      formWithErrors => BadRequest(areYouStillConnectedView(formWithErrors, request.sessionData.toSummary)),
      data => {
        val updatedData = updateStillConnectedDetails(_.copy(addressConnectionType = Some(data)))
        session
          .saveOrUpdate(updatedData)
          .map(_ =>
            navigator
              .cyaPageDependsOnSession(updatedData)
              .filter(_ =>
                navigator.from == "CYA" &&
                  request.sessionData.stillConnectedDetails.flatMap(_.addressConnectionType).contains(data)
              )
              .getOrElse(navigator.next(AreYouStillConnectedPageId, updatedData).apply(updatedData))
          )
          .map(Redirect)
      }
    )
  }

}
