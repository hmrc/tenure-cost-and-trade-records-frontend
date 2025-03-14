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

package controllers.requestReferenceNumber

import actions.WithSessionRefiner
import form.requestReferenceNumber.RequestReferenceNumberForm.requestReferenceNumberForm
import models.ForType.*
import models.Session
import models.submissions.common.Address
import models.submissions.requestReferenceNumber.RequestReferenceNumberDetails.updateRequestReferenceNumber
import navigation.RequestReferenceNumberNavigator
import navigation.identifiers.NoReferenceNumberPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.requestReferenceNumber.requestReferenceNumber

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RequestReferenceNumberController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: RequestReferenceNumberNavigator,
  requestReferenceNumberView: requestReferenceNumber,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc)
    with I18nSupport {

  def startWithSession: Action[AnyContent] = Action.async { implicit request =>
    session.start(Session("", FOR6010, Address("", None, "", None, ""), "", isWelsh = false))
    Future.successful(Redirect(routes.RequestReferenceNumberController.show()))
  }

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        requestReferenceNumberView(
          request.sessionData.requestReferenceNumberDetails.flatMap(_.requestReferenceNumberAddress) match {
            case Some(requestReferenceNumberAddress) =>
              requestReferenceNumberForm.fill(requestReferenceNumberAddress)
            case _                                   => requestReferenceNumberForm
          }
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    requestReferenceNumberForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(requestReferenceNumberView(formWithErrors))),
        data => {
          val updatedData = updateRequestReferenceNumber(_.copy(requestReferenceNumberAddress = Some(data)))
          session
            .saveOrUpdate(updatedData)
            .map(_ => Redirect(navigator.nextPage(NoReferenceNumberPageId, updatedData).apply(updatedData)))
        }
      )
  }

}
