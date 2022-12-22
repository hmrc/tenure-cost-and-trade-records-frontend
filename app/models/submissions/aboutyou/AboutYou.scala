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

package models.submissions.aboutyou

import actions.SessionRequest
import models.Session
import play.api.libs.json.Json

case class AboutYou(customerDetails: Option[CustomerDetails] = None)

object AboutYou {
  implicit val format = Json.format[AboutYou]

  def updateAboutYou(copy: AboutYou => AboutYou)(implicit sessionRequest: SessionRequest[_]): Session = {

    val currentAboutYou = sessionRequest.sessionData.aboutYou

    val updatedAboutYou = currentAboutYou match {
      case Some(_) => sessionRequest.sessionData.aboutYou.map(copy)
      case _       => Some(copy(AboutYou()))
    }

    sessionRequest.sessionData.copy(aboutYou = updatedAboutYou)

  }
}
