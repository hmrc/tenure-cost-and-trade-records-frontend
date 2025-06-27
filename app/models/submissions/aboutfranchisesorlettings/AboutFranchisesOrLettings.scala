/*
 * Copyright 2025 HM Revenue & Customs
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

package models.submissions.aboutfranchisesorlettings

import actions.SessionRequest
import models.Session
import models.submissions.common.AnswersYesNo
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class AboutFranchisesOrLettings(
  franchisesOrLettingsTiedToProperty: Option[AnswersYesNo] = None,
  currentMaxOfLetting: Option[Boolean] = None,
  checkYourAnswersAboutFranchiseOrLettings: Option[AnswersYesNo] = None,
  fromCYA: Option[Boolean] = None,
  lettings: Option[IndexedSeq[LettingPartOfProperty]] = None, // 6020 lettings
  rentalIncome: Option[IndexedSeq[IncomeRecord]] = None,
  rentalIncomeIndex: Int = 0,
  lettingCurrentIndex: Int = 0,
  rentalIncomeMax: Option[Boolean] = None
)

object AboutFranchisesOrLettings:

  implicit val aboutFranchisesOrLettingsReads: Reads[AboutFranchisesOrLettings] = (
    (__ \ "franchisesOrLettingsTiedToProperty").readNullable[AnswersYesNo] and
      (__ \ "currentMaxOfLetting").readNullable[Boolean] and
      (__ \ "checkYourAnswersAboutFranchiseOrLettings").readNullable[AnswersYesNo] and
      (__ \ "fromCYA").readNullable[Boolean] and
      (__ \ "lettings").readNullable[IndexedSeq[LettingPartOfProperty]] and
      (__ \ "rentalIncome").readNullable[IndexedSeq[IncomeRecord]] and
      (__ \ "rentalIncomeIndex").read[Int] and
      (__ \ "lettingCurrentIndex").read[Int] and
      (__ \ "rentalIncomeMax").readNullable[Boolean]
  )(AboutFranchisesOrLettings.apply)

  implicit val format: Format[AboutFranchisesOrLettings] =
    Format(aboutFranchisesOrLettingsReads, Json.writes[AboutFranchisesOrLettings])

  def updateAboutFranchisesOrLettings(
    copy: AboutFranchisesOrLettings => AboutFranchisesOrLettings
  )(implicit sessionRequest: SessionRequest[?]): Session =

    val currentAboutFranchisesOrLettings = sessionRequest.sessionData.aboutFranchisesOrLettings

    val updateAboutFranchisesOrLettings = currentAboutFranchisesOrLettings match {
      case Some(_) => sessionRequest.sessionData.aboutFranchisesOrLettings.map(copy)
      case _       => Some(copy(AboutFranchisesOrLettings()))
    }

    sessionRequest.sessionData.copy(aboutFranchisesOrLettings = updateAboutFranchisesOrLettings)
