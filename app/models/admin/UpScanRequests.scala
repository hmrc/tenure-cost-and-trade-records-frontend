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

package models.admin

import org.joda.time.DateTime
import play.api.libs.json.{JsObject, JsResult, JsValue, Json, OFormat}

import play.api.libs.json.JodaReads._
import play.api.libs.json.JodaWrites._

object UpScanRequests {

  implicit val initiateRequest: OFormat[InitiateRequest]                    = Json.format[InitiateRequest]
  implicit val uploadRequests: OFormat[UploadRequest]                       = Json.format[UploadRequest]
  implicit val initialResponse: OFormat[InitiateResponse]                   = Json.format[InitiateResponse]
  implicit val uploadDetails: OFormat[UploadDetails]                        = Json.format[UploadDetails]
  implicit val uploadConfirmationSucess: OFormat[UploadConfirmationSuccess] = Json.format[UploadConfirmationSuccess]
  implicit val failureDetails: OFormat[FailureDetails]                      = Json.format[FailureDetails]
  implicit val uploadConfirmationError: OFormat[UploadConfirmationError]    = Json.format[UploadConfirmationError]
  case class InitiateRequest(
    callbackUrl: String,
    successRedirect: Option[String] = None,
    maxFileSize: Int
  )

  case class InitiateResponse(
    reference: String,
    uploadRequest: UploadRequest
  )

  case class UploadRequest(
    href: String,
    fields: Map[String, String]
  )

  sealed trait UploadConfirmation {
    val reference: String
    val fileStatus: String
  }

  object UploadConfirmation {
    implicit val format: OFormat[UploadConfirmation] = new OFormat[UploadConfirmation] {
      override def writes(o: UploadConfirmation): JsObject = o match {
        case x: UploadConfirmationError   => uploadConfirmationError.writes(x)
        case x: UploadConfirmationSuccess => uploadConfirmationSucess.writes(x)
      }

      override def reads(json: JsValue): JsResult[UploadConfirmation] =
        uploadConfirmationSucess.reads(json).orElse(uploadConfirmationError.reads(json))
    }
  }

  case class UploadConfirmationSuccess(
    reference: String,
    downloadUrl: String,
    fileStatus: String,
    uploadDetails: UploadDetails
  ) extends UploadConfirmation

  case class UploadConfirmationError(
    reference: String,
    fileStatus: String,
    failureDetails: FailureDetails
  ) extends UploadConfirmation

  case class FailureDetails(
    failureReason: String,
    message: String
  )

  case class UploadDetails(
    uploadTimestamp: DateTime,
    checksum: String,
    fileMimeType: String,
    fileName: String
  )

}
