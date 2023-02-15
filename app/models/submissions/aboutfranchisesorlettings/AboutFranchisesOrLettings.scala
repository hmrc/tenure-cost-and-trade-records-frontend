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
import models.submissions.Form6010.LettingOtherPartOfProperty
import models.submissions.Form6010.FranchiseOrLettingsTiedToProperty
import play.api.libs.json.Json

case class AboutFranchisesOrLettings(
  franchisesOrLettingsTiedToProperty: Option[FranchiseOrLettingsTiedToProperty] = None,
  concessionOrFranchise: Option[ConcessionOrFranchise] = None,
  cateringOperationOrLettingAccommodation: Option[CateringOperation] = None,
  cateringOperationOrLettingAccommodationSections: IndexedSeq[CateringOperationOrLettingAccommodationSection] =
    IndexedSeq.empty,
  lettingOtherPartOfProperty: Option[LettingOtherPartOfProperty] = None,
  lettingSections: IndexedSeq[LettingSection] = IndexedSeq.empty
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
