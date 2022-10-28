/*
 * Copyright 2022 HM Revenue & Customs
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

package models

import models.submissions.Form6010.AddressConnectionType
import play.api.libs.json._
case class Session(
                      areYouSTillConnected: AddressConnectionType,
                      fullName: Option[String],
                      emailAddress: Option[String],
                      telephoneNumber: Option[String],
                      relationshipToProperty: Option[String]
                      ) {

}

object Session {
  implicit val format: OFormat[Session] = Json.format[Session]
}

case class areYouStillConnectedToAddress(areYouStillConnected: AddressConnectionType)

object areYouStillConnectedToAddress {

  implicit val format: OFormat[areYouStillConnectedToAddress] = Json.format[areYouStillConnectedToAddress]

}
