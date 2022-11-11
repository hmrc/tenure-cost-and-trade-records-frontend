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

import form.MappingSupport._
import models.submissions.Form6010.{AddressConnectionType, CustomerDetails}
import models.submissions.{ConnectionToThePropertyDetails, UserType}
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.libs.json._

case class Session (
                     areYouStillConnected: AddressConnectionType
                   )

object Session {
  implicit val format = Json.format[Session]

  def apply(addressConnectionType: AddressConnectionType): Session = {
    Session(
      addressConnectionType)
  }

//  def apply(connectionToThePropertyDetails: ConnectionToThePropertyDetails)(implicit ses: Session): Session = {
//    Session(
//      ses.areYouStillConnected,
//      connectionToTheProperty = Some(connectionToThePropertyDetails)
//    )
//  }

}


object areYouStillConnectedToAddress{
  object AreYouStillConnectedForm {

    lazy val baseAreYouStillConnectedForm: Form[AddressConnectionType] = Form(baseAreYouStillConnectedMapping)

    val baseAreYouStillConnectedMapping = mapping(
      "isRelated" -> addressConnectionType
    )(x => x)(b => Some(b))

    val areYouStillConnectedForm = Form(baseAreYouStillConnectedMapping)



  }
}
