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

package models

import models.submissions.aboutLeaseOrAgreement.AboutLeaseOrAgreementPartOne
import models.submissions.aboutfranchisesorlettings.AboutFranchisesOrLettings
import models.submissions.abouttheproperty.AboutTheProperty
import models.submissions.aboutthetradinghistory.AboutTheTradingHistory
import models.submissions.aboutyou.AboutYou
import models.submissions.additionalinformation.{AdditionalInformation, AltContactInformation}
import models.submissions.connectiontoproperty.StillConnectedDetails
import models.submissions.notconnected.RemoveConnectionDetails
import play.api.libs.json._

case class Session(
  userLoginDetails: UserLoginDetails,
  stillConnectedDetails: Option[StillConnectedDetails] = None,
  removeConnectionDetails: Option[RemoveConnectionDetails] = None,
  aboutYou: Option[AboutYou] = None,
  aboutTheProperty: Option[AboutTheProperty] = None,
  additionalInformation: Option[AdditionalInformation] = None,
  altContactInformation: Option[AltContactInformation] = None,
  aboutTheTradingHistory: Option[AboutTheTradingHistory] = None,
  aboutFranchisesOrLettings: Option[AboutFranchisesOrLettings] = None,
  aboutLeaseOrAgreementPartOne: Option[AboutLeaseOrAgreementPartOne] = None
)

object Session {
  implicit val format = Json.format[Session]

}
