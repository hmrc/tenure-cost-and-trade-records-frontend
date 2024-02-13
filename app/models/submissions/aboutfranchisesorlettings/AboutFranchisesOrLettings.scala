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

package models.submissions.aboutfranchisesorlettings

import actions.SessionRequest
import models.Session
import models.submissions.MaxOfLettings
import models.submissions.common.AnswersYesNo
import play.api.libs.json.Json

case class AboutFranchisesOrLettings(
  franchisesOrLettingsTiedToProperty: Option[AnswersYesNo] = None,
  cateringConcessionOrFranchise: Option[AnswersYesNo] = None,
  cateringOperationCurrentIndex: Int = 0,
  cateringMaxOfLettings: Option[MaxOfLettings] = None,
  cateringOperationSections: IndexedSeq[CateringOperationSection] = IndexedSeq.empty,
  lettingOtherPartOfProperty: Option[AnswersYesNo] = None,
  lettingCurrentIndex: Int = 0,
  currentMaxOfLetting: Option[MaxOfLettings] = None,
  lettingSections: IndexedSeq[LettingSection] = IndexedSeq.empty,
  checkYourAnswersAboutFranchiseOrLettings: Option[CheckYourAnswersAboutFranchiseOrLettings] = None,
  fromCYA: Option[Boolean] = None,
  cateringOrFranchiseFee: Option[AnswersYesNo] = None //added for 6030 journey - Feb 2024
)

object AboutFranchisesOrLettings {
  implicit val format = Json.format[AboutFranchisesOrLettings]

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
