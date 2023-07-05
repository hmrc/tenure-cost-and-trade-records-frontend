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

package models.submissions.downloadFORTypeForm

import actions.SessionRequest
import models.Session
import play.api.libs.json.{Json, OFormat}

case class DownloadPDFDetails(
  downloadPDFReferenceNumber: Option[DownloadPDFReferenceNumber] = None,
  downloadPDF: Option[DownloadPDF] = None
)

object DownloadPDFDetails {
  implicit val format: OFormat[DownloadPDFDetails] = Json.format[DownloadPDFDetails]

  def updateDownloadPDFDetails(
    copy: DownloadPDFDetails => DownloadPDFDetails
  )(implicit sessionRequest: SessionRequest[_]): Session = {

    val currentDownloadPDFDetails = sessionRequest.sessionData.downloadPDFDetails

    val updatedDownloadPDFDetails = currentDownloadPDFDetails match {
      case Some(_) => sessionRequest.sessionData.downloadPDFDetails.map(copy)
      case _       => Some(copy(DownloadPDFDetails()))
    }

    sessionRequest.sessionData.copy(downloadPDFDetails = updatedDownloadPDFDetails)

  }
}
