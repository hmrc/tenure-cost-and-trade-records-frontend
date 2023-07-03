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
import form.requestReferenceNumber.DownloadPDFForm.downloadPDFForm
import models.Session
import models.submissions.common.Address
import models.submissions.requestReferenceNumber.RequestReferenceNumberDetails.updateRequestReferenceNumber
import navigation.ConnectionToPropertyNavigator
import navigation.identifiers.DownloadPDFReferenceNumberPageId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepo
import views.html.requestReferenceNumber.{downloadPDF, downloadPDFReferenceNumber}

import javax.inject.{Inject, Named, Singleton}
import scala.concurrent.Future

@Singleton
class DownloadPDFController @Inject()(
  mcc: MessagesControllerComponents,
  downloadPDFView: downloadPDF,
  @Named("session") val session: SessionRepo
) extends FORDataCaptureController(mcc)
    with I18nSupport {

  def show: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(downloadPDFView()))
  }
}
