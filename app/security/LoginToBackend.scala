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

package security

import models.FORLoginResponse
import models.submissions.common.Address
import org.joda.time.DateTime

import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

object LoginToBackend {
  type RefNumber         = String
  type Postcode          = String
  type StartTime         = DateTime
  type SessionID         = String
  type AuthToken         = String
  type LoginToBackend    = (RefNumber, Postcode, StartTime) => Future[LoginResult]
  type VerifyCredentials = (RefNumber, Postcode) => Future[FORLoginResponse]

  def apply(
    v: VerifyCredentials
  )(rn: RefNumber, pc: Postcode, st: StartTime)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[LoginResult] =
    for {
      lr <- v(rn, pc)
    } yield ned(
      lr.forAuthToken,
      lr.forType,
      lr.address
    )

  private def ned = NoExistingDocument.apply _
}

sealed trait LoginResult

case class NoExistingDocument(token: String, forNum: String, address: Address) extends LoginResult
