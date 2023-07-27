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

package controllers.downloadFORTypeForm

import play.api.Logging
import connectors.BackendConnector
import form.downloadFORTypeForm.DownloadPDFReferenceNumberForm.downloadPDFReferenceNumberForm
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.downloadFORTypeForm.downloadPDFReferenceNumber

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DownloadPDFReferenceNumberController @Inject() (
  mcc: MessagesControllerComponents,
  downloadPDFReferenceNumberView: downloadPDFReferenceNumber,
  connector: BackendConnector
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc)
    with Logging {

  def show: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(downloadPDFReferenceNumberView(downloadPDFReferenceNumberForm)))
  }

  def submit: Action[AnyContent] = Action.async { implicit request =>
    downloadPDFReferenceNumberForm
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(downloadPDFReferenceNumberView(formWithErrors))),
        userData =>
          connector
            .retrieveFORType(userData.downloadPDFReferenceNumber)
            .flatMap { value =>
              Future.successful(Redirect(controllers.downloadFORTypeForm.routes.DownloadPDFController.show(value)))
            }
            .recover { case _ =>
              logger.error(s"Failed to retrieve a FOR Type for ${userData.downloadPDFReferenceNumber}")
              Redirect(controllers.downloadFORTypeForm.routes.DownloadPDFController.show("invalidType"))
            }
      )
  }
}
