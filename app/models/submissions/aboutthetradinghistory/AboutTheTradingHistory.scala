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

package models.submissions.aboutthetradinghistory

import actions.SessionRequest
import models.Session
import models.submissions.common.AnswersYesNo
import play.api.libs.functional.syntax.*
import play.api.libs.json.*

import java.time.LocalDate

case class AboutTheTradingHistory(
  occupationAndAccountingInformation: Option[OccupationalAndAccountingInformation] = None,
  // TODO turnoverSections: Option[Seq[TurnoverSection]] = None,
  turnoverSections: Seq[TurnoverSection] = Seq.empty,
  turnoverSections6020: Option[Seq[TurnoverSection6020]] = None,
  // TODO turnoverSections6030: Option[Seq[TurnoverSection6030]] = None,
  turnoverSections6030: Seq[TurnoverSection6030] = Seq.empty,
  costOfSales: Seq[CostOfSales] = Seq.empty,
  fixedOperatingExpensesSections: Seq[FixedOperatingExpenses] = Seq.empty,
  otherCosts: Option[OtherCosts] = None,
  totalPayrollCostSections: Seq[TotalPayrollCost] = Seq.empty,
  variableOperatingExpenses: Option[VariableOperatingExpensesSections] = None,
  incomeExpenditureSummary: Option[String] = None,
  incomeExpenditureSummaryData: Seq[IncomeExpenditureSummaryData] = Seq.empty,
  unusualCircumstances: Option[UnusualCircumstances] = None,
  electricVehicleChargingPoints: Option[ElectricVehicleChargingPoints] = None, // added March 2024
  totalFuelSold: Option[Seq[TotalFuelSold]] = None,
  bunkeredFuelQuestion: Option[AnswersYesNo] = None,
  bunkeredFuelSold: Option[Seq[BunkeredFuelSold]] = None,
  bunkerFuelCardsDetails: Option[IndexedSeq[BunkerFuelCardsDetails]] = None,
  customerCreditAccounts: Option[Seq[CustomerCreditAccounts]] = None,
  doYouAcceptLowMarginFuelCard: Option[AnswersYesNo] = None,
  percentageFromFuelCards: Option[Seq[PercentageFromFuelCards]] = None,
  lowMarginFuelCardsDetails: Option[IndexedSeq[LowMarginFuelCardsDetails]] = None,
  checkYourAnswersAboutTheTradingHistory: Option[AnswersYesNo] = None
)

object AboutTheTradingHistory {

  implicit val aboutTheTradingHistoryReads: Reads[AboutTheTradingHistory] = (
    (__ \ "occupationAndAccountingInformation").readNullable[OccupationalAndAccountingInformation] and
      (__ \ "turnoverSections").read[Seq[TurnoverSection]] and
      (__ \ "turnoverSections6020").readNullable[Seq[TurnoverSection6020]] and
      (__ \ "turnoverSections6030").readNullable[Seq[TurnoverSection6030]].map(_.getOrElse(Seq.empty)) and
      (__ \ "costOfSales").read[Seq[CostOfSales]] and
      (__ \ "fixedOperatingExpensesSections").read[Seq[FixedOperatingExpenses]] and
      (__ \ "otherCosts").readNullable[OtherCosts] and
      (__ \ "totalPayrollCostSections").read[Seq[TotalPayrollCost]] and
      (__ \ "variableOperatingExpenses").readNullable[VariableOperatingExpensesSections] and
      (__ \ "incomeExpenditureSummary").readNullable[String] and
      (__ \ "incomeExpenditureSummaryData").read[Seq[IncomeExpenditureSummaryData]] and
      (__ \ "unusualCircumstances").readNullable[UnusualCircumstances] and
      (__ \ "electricVehicleChargingPoints").readNullable[ElectricVehicleChargingPoints] and
      (__ \ "totalFuelSold").readNullable[Seq[TotalFuelSold]] and
      (__ \ "bunkeredFuelQuestion").readNullable[AnswersYesNo] and
      (__ \ "bunkeredFuelSold").readNullable[Seq[BunkeredFuelSold]] and
      (__ \ "bunkerFuelCardsDetails").readNullable[IndexedSeq[BunkerFuelCardsDetails]] and
      (__ \ "customerCreditAccounts").readNullable[Seq[CustomerCreditAccounts]] and
      (__ \ "doYouAcceptLowMarginFuelCard").readNullable[AnswersYesNo] and
      (__ \ "percentageFromFuelCards").readNullable[Seq[PercentageFromFuelCards]] and
      (__ \ "lowMarginFuelCardsDetails").readNullable[IndexedSeq[LowMarginFuelCardsDetails]] and
      (__ \ "checkYourAnswersAboutTheTradingHistory").readNullable[AnswersYesNo]
  )(AboutTheTradingHistory.apply)

  implicit val format: Format[AboutTheTradingHistory] =
    Format(aboutTheTradingHistoryReads, Json.writes[AboutTheTradingHistory])

  def updateAboutTheTradingHistory(
    copy: AboutTheTradingHistory => AboutTheTradingHistory
  )(implicit sessionRequest: SessionRequest[?]): Session = {

    val currentAboutTheTradingHistory = sessionRequest.sessionData.aboutTheTradingHistory

    val updateAboutTheTradingHistory = currentAboutTheTradingHistory match {
      case Some(_) => sessionRequest.sessionData.aboutTheTradingHistory.map(copy)
      case _       => Some(copy(AboutTheTradingHistory()))
    }

    sessionRequest.sessionData.copy(aboutTheTradingHistory = updateAboutTheTradingHistory)

  }

  type TurnoverSectionUnion =
    TurnoverSection6020 | TurnoverSection6030 | TurnoverSection6045 | TurnoverSection6048 | TurnoverSection6076 |
      TurnoverSection

  class WrappedTurnoverSection(wrapped: TurnoverSectionUnion) {
    def financialYearEnd: LocalDate = wrapped match
      case TurnoverSection6020(financialYearEnd, _, _, _, _, _, _)                               => financialYearEnd
      case TurnoverSection6030(financialYearEnd, _, _, _)                                        => financialYearEnd
      case TurnoverSection6045(financialYearEnd, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _) => financialYearEnd
      case TurnoverSection6048(financialYearEnd, _, _, _, _, _, _)                               => financialYearEnd
      case TurnoverSection6076(financialYearEnd, _, _, _, _, _, _, _, _, _, _, _, _)             => financialYearEnd
      case TurnoverSection(financialYearEnd, _, _, _, _, _, _)                                   => financialYearEnd
  }
}
