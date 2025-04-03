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

package form.accommodation

import actions.SessionRequest
import form.MappingSupport.*
import models.submissions.accommodation.AccommodationLettingHistory
import play.api.data.Forms.{ignored, mapping}
import play.api.data.{Form, Mapping}
import play.api.i18n.Messages
import play.api.mvc.AnyContent
import util.AccountingInformationUtil

import java.time.LocalDate

/**
  * @author Yuriy Tumakha
  */
object AccommodationLettingHistory6048Form {

  private def columnMapping(
    year: String
  )(using request: SessionRequest[AnyContent], messages: Messages): Mapping[AccommodationLettingHistory] =
    val maxNights = AccountingInformationUtil.maxNightsInFinYear6048(year.toInt)
    val maxWeeks  = AccountingInformationUtil.maxWeeksInFinYear6048(year.toInt)

    mapping(
      "financialYearEnd"             -> ignored(LocalDate.EPOCH),
      "nightsAvailableToLet"         -> nonNegativeNumberWithYear(
        "accommodation.lettingHistory.nightsAvailableToLet",
        year,
        maxNights
      ),
      "nightsLet"                    -> nonNegativeNumberWithYear("accommodation.lettingHistory.nightsLet", year, maxNights),
      "weeksAvailableForPersonalUse" -> nonNegativeNumberWithYear(
        "accommodation.lettingHistory.weeksAvailableForPersonalUse",
        year,
        maxWeeks
      )
    )(AccommodationLettingHistory.apply)(o => Some(Tuple.fromProductTyped(o)))

  def accommodationLettingHistory6048Form(
    years: Seq[String]
  )(using request: SessionRequest[AnyContent], messages: Messages): Form[Seq[AccommodationLettingHistory]] =
    Form {
      mappingPerYear(years, (year, idx) => s"lettingHistory[$idx]" -> columnMapping(year))
    }

}
