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

package models.submissions.aboutfranchisesorlettings

import actions.SessionRequest
import models.Session
import models.submissions.MaxOfLettings
import models.submissions.common.AnswersYesNo
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class AboutFranchisesOrLettings(
  franchisesOrLettingsTiedToProperty: Option[AnswersYesNo] = None,
  cateringConcessionOrFranchise: Option[AnswersYesNo] = None,
  cateringOperationCurrentIndex: Int = 0,
  cateringMaxOfLettings: Option[MaxOfLettings] = None,
  cateringOperationSections: IndexedSeq[CateringOperationSection] = IndexedSeq.empty,
  cateringOperationBusinessSections: Option[IndexedSeq[CateringOperationBusinessSection]] = None, // 6030 journey
  lettingOtherPartOfProperty: Option[AnswersYesNo] = None,
  lettingCurrentIndex: Int = 0,
  currentMaxOfLetting: Option[MaxOfLettings] = None,
  lettingSections: IndexedSeq[LettingSection] = IndexedSeq.empty,
  checkYourAnswersAboutFranchiseOrLettings: Option[CheckYourAnswersAboutFranchiseOrLettings] = None,
  fromCYA: Option[Boolean] = None,
  cateringOrFranchiseFee: Option[AnswersYesNo] = None, //added for 6030 journey - Feb 2024
  lettings: Option[IndexedSeq[LettingPartOfProperty]] = None // 6020 lettings
)

object AboutFranchisesOrLettings {

  implicit val aboutFranchisesOrLettingsReads: Reads[AboutFranchisesOrLettings] = (
    (__ \ "franchisesOrLettingsTiedToProperty").readNullable[AnswersYesNo] and
      (__ \ "cateringConcessionOrFranchise").readNullable[AnswersYesNo] and
      (__ \ "cateringOperationCurrentIndex").read[Int] and
      (__ \ "cateringMaxOfLettings").readNullable[MaxOfLettings] and
      (__ \ "cateringOperationSections").read[IndexedSeq[CateringOperationSection]] and
      (__ \ "cateringOperationBusinessSections").readNullable[IndexedSeq[CateringOperationBusinessSection]] and
      (__ \ "lettingOtherPartOfProperty").readNullable[AnswersYesNo] and
      (__ \ "lettingCurrentIndex").read[Int] and
      (__ \ "currentMaxOfLetting").readNullable[MaxOfLettings] and
      (__ \ "lettingSections").read[IndexedSeq[LettingSection]] and
      (__ \ "checkYourAnswersAboutFranchiseOrLettings").readNullable[CheckYourAnswersAboutFranchiseOrLettings] and
      (__ \ "fromCYA").readNullable[Boolean] and
      (__ \ "cateringOrFranchiseFee").readNullable[AnswersYesNo] and
      (__ \ "lettings").readNullable[IndexedSeq[LettingPartOfProperty]]
  )(AboutFranchisesOrLettings.apply _)

  implicit val format: Format[AboutFranchisesOrLettings] =
    Format(aboutFranchisesOrLettingsReads, Json.writes[AboutFranchisesOrLettings])

  def updateAboutFranchisesOrLettings(
    copy: AboutFranchisesOrLettings => AboutFranchisesOrLettings
  )(implicit sessionRequest: SessionRequest[_]): Session = {

    val currentAboutFranchisesOrLettings = sessionRequest.sessionData.aboutFranchisesOrLettings

    val updateAboutFranchisesOrLettings = currentAboutFranchisesOrLettings match {
      case Some(_) => sessionRequest.sessionData.aboutFranchisesOrLettings.map(copy)
      case _       => Some(copy(AboutFranchisesOrLettings()))
    }

    sessionRequest.sessionData.copy(aboutFranchisesOrLettings = updateAboutFranchisesOrLettings)

  }

}
