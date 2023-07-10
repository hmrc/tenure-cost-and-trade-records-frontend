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
import form.requestReferenceNumber.RequestReferenceNumberContactDetailsForm.requestReferenceNumberContactDetailsForm
import navigation.ConnectionToPropertyNavigator
import models.submissions.requestReferenceNumber.RequestReferenceNumberDetails.updateRequestReferenceNumber
import navigation.identifiers.NoReferenceNumberContactDetailsPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.requestReferenceNumber.requestReferenceNumberContactDetails

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class RequestReferenceNumberContactDetailsController @Inject() (
  mcc: MessagesControllerComponents,
  navigator: ConnectionToPropertyNavigator,
  requestReferenceNumberContactDetailsView: requestReferenceNumberContactDetails,
  withSessionRefiner: WithSessionRefiner,
  @Named("session") val session: SessionRepo
) extends FrontendController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    Future.successful(
      Ok(
        requestReferenceNumberContactDetailsView(
          request.sessionData.requestReferenceNumberDetails.flatMap(_.requestReferenceContactDetails) match {
            case Some(requestReferenceContactDetails) =>
              requestReferenceNumberContactDetailsForm.fillAndValidate(requestReferenceContactDetails)
            case _                                    => requestReferenceNumberContactDetailsForm
          }
        )
      )
    )
  }

  def submit: Action[AnyContent] = (Action andThen withSessionRefiner).async { implicit request =>
    requestReferenceNumberContactDetailsForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(requestReferenceNumberContactDetailsView(formWithErrors))),
        data => {
          val updatedData = updateRequestReferenceNumber(_.copy(requestReferenceContactDetails = Some(data)))
          session.saveOrUpdate(updatedData)
          Future.successful(
            Redirect(navigator.nextPage(NoReferenceNumberContactDetailsPageId, updatedData).apply(updatedData))
          )
        }
      )
  }

}