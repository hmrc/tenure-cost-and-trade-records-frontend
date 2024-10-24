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

package models.submissions.aboutthetradinghistory

import actions.SessionRequest
import models.Session
import models.submissions.common.AnswersYesNo
import play.api.libs.json.*

case class AboutTheTradingHistoryPartOne(
  isFinancialYearEndDatesCorrect: Option[Boolean] = Some(false),
  isFinancialYearsCorrect: Option[Boolean] = None,
  // 6076
  whatYouWillNeed: Option[String] = None, // Added July 2024
  turnoverSections6076: Option[Seq[TurnoverSection6076]] = None,
  otherIncomeDetails: Option[String] = None,
  otherOperationalExpensesDetails: Option[String] = None, // Reused for 6048
  otherSalesDetails: Option[String] = None,
  furtherInformationOrRemarks: Option[String] = None,
  incomeExpenditureConfirmation6076: Option[String] = None,
  // 6045/6046
  turnoverSections6045: Option[Seq[TurnoverSection6045]] = None,
  caravans: Option[Caravans] = None,
  otherHolidayAccommodation: Option[OtherHolidayAccommodation] = None,
  touringAndTentingPitches: Option[TouringAndTentingPitches] = None,
  additionalActivities: Option[AdditionalActivities] = None,
  additionalMiscDetails: Option[AdditionalMiscDetails] = None,
  fromCYA: Option[Boolean] = None,
  // 6048
  turnoverSections6048: Option[Seq[TurnoverSection6048]] = None,
  areYouVATRegistered: Option[AnswersYesNo] = None
)

object AboutTheTradingHistoryPartOne {
  implicit val format: OFormat[AboutTheTradingHistoryPartOne] = Json.format

  def updateAboutTheTradingHistoryPartOne(
    copy: AboutTheTradingHistoryPartOne => AboutTheTradingHistoryPartOne
  )(implicit sessionRequest: SessionRequest[?]): Session = {

    val currentAboutTheTradingHistoryPartOne = sessionRequest.sessionData.aboutTheTradingHistoryPartOne

    val updateAboutTheTradingHistoryPartOne = currentAboutTheTradingHistoryPartOne match {
      case Some(_) => sessionRequest.sessionData.aboutTheTradingHistoryPartOne.map(copy)
      case _       => Some(copy(AboutTheTradingHistoryPartOne()))
    }

    sessionRequest.sessionData.copy(aboutTheTradingHistoryPartOne = updateAboutTheTradingHistoryPartOne)
  }

  def updateCaravans(
    update: Caravans => Caravans
  )(implicit sessionRequest: SessionRequest[?]): Session =
    updateAboutTheTradingHistoryPartOne { aboutTheTradingHistoryPartOne =>
      val caravans = update(aboutTheTradingHistoryPartOne.caravans getOrElse Caravans())
      aboutTheTradingHistoryPartOne.copy(caravans = Some(caravans))
    }

  def updateOtherHolidayAccommodation(
    update: OtherHolidayAccommodation => OtherHolidayAccommodation
  )(implicit sessionRequest: SessionRequest[?]): Session =
    updateAboutTheTradingHistoryPartOne { aboutTheTradingHistoryPartOne =>
      val otherHolidayAccommodation =
        update(aboutTheTradingHistoryPartOne.otherHolidayAccommodation.getOrElse(OtherHolidayAccommodation()))
      aboutTheTradingHistoryPartOne.copy(otherHolidayAccommodation = Some(otherHolidayAccommodation))
    }

  def updateTouringAndTentingPitches(
    update: TouringAndTentingPitches => TouringAndTentingPitches
  )(implicit sessionRequest: SessionRequest[?]): Session =
    updateAboutTheTradingHistoryPartOne { aboutTheTradingHistoryPartOne =>
      val touringAndTentingPitches =
        update(aboutTheTradingHistoryPartOne.touringAndTentingPitches.getOrElse(TouringAndTentingPitches()))
      aboutTheTradingHistoryPartOne.copy(touringAndTentingPitches = Some(touringAndTentingPitches))
    }

  def updateAdditionalActivities(
    update: AdditionalActivities => AdditionalActivities
  )(implicit sessionRequest: SessionRequest[?]): Session =
    updateAboutTheTradingHistoryPartOne { aboutTheTradingHistoryPartOne =>
      val additionalActivities =
        update(aboutTheTradingHistoryPartOne.additionalActivities.getOrElse(AdditionalActivities()))
      aboutTheTradingHistoryPartOne.copy(additionalActivities = Some(additionalActivities))
    }
}
