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

package controllers.requestReferenceNumber

import actions.WithSessionRefiner
import controllers.FORDataCaptureController
import form.requestReferenceNumber.RequestReferenceNumberForm.noReferenceNumberForm
import models.Session
import models.submissions.common.Address
import models.submissions.requestReferenceNumber.RequestReferenceNumberDetails.updateRequestReferenceNumber
import models.submissions.requestReferenceNumber.NoReferenceNumber
import navigation.ConnectionToPropertyNavigator
import navigation.identifiers.NoReferenceNumberPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.requestReferenceNumber.requestReferenceNumber

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class RequestReferenceNumberController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: ConnectionToPropertyNavigator,
  noReferenceNumberView: requestReferenceNumber,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def startWithSession: Action[AnyContent] = Action.async { implicit request =>
    session.start(Session("", "", Address("", None, "", None, ""), ""))
    Future.successful(Redirect(routes.RequestReferenceNumberController.show()))
  }

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        noReferenceNumberView(
          request.sessionData.requestReferenceNumberDetails.flatMap(_.noReferenceNumberAddress) match {
            case Some(noReferenceNumber) => noReferenceNumberForm.fillAndValidate(noReferenceNumber)
            case _                       => noReferenceNumberForm
          }
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    noReferenceNumberForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(noReferenceNumberView(formWithErrors))),
        data => {
          val updatedData = updateRequestReferenceNumber(_.copy(noReferenceNumberAddress = Some(data)))
          session.saveOrUpdate(updatedData)
          Future.successful(Redirect(navigator.nextPage(NoReferenceNumberPageId, updatedData).apply(updatedData)))
        }
      )
  }

}
