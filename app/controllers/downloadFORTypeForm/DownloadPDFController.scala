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

package controllers.downloadFORTypeForm

import connectors.Audit
import models.audit.DownloadPDFAudit
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.downloadFORTypeForm.downloadPDF

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class DownloadPDFController @Inject() (
  mcc: MessagesControllerComponents,
  audit: Audit,
  downloadPDFView: downloadPDF
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc) {

  def show(forType: String): Action[AnyContent] = Action { implicit request =>
    val referenceNumber = request.session.get("referenceNumber").getOrElse("")

    audit.sendExplicitAudit(
      "ForRequestedFromReference",
      DownloadPDFAudit(referenceNumber, forType, request.uri)
    )
    Ok(downloadPDFView(forType, referenceNumber))
  }

}
