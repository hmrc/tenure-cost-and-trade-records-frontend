/*
 * Copyright 2026 HM Revenue & Customs
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

package controllers.downloadFORTypeForm

import play.api.Logging
import connectors.BackendConnector
import form.ReferenceNumberForm.theForm
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.referenceNumber as ReferenceNumberView

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import scala.concurrent.Future.successful

@Singleton
class DownloadPDFReferenceNumberController @Inject() (
  mcc: MessagesControllerComponents,
  connector: BackendConnector,
  referenceNumberView: ReferenceNumberView
)(using ec: ExecutionContext)
    extends FrontendController(mcc)
    with Logging:

  def show: Action[AnyContent] = Action { implicit request =>
    Ok(referenceNumberView(theForm, call = routes.DownloadPDFReferenceNumberController.submit()))
  }

  def submit: Action[AnyContent] = Action.async { implicit request =>
    theForm
      .bindFromRequest()
      .fold(
        formWithErrors =>
          successful(
            BadRequest(referenceNumberView(formWithErrors, call = routes.DownloadPDFReferenceNumberController.submit()))
          ),
        referenceNumber =>
          connector
            .retrieveFORType(referenceNumber.value, hc)
            .map { forType =>
              Redirect(
                controllers.downloadFORTypeForm.routes.DownloadPDFController.show(forType)
              ).withSession(
                request.session + ("referenceNumber" -> referenceNumber.value)
              )
            }
            .recover { case _ =>
              logger.error(s"Failed to retrieve a FOR Type for ${referenceNumber.value}")
              Redirect(
                controllers.downloadFORTypeForm.routes.DownloadPDFController.show("invalidType")
              )
            }
      )
  }
