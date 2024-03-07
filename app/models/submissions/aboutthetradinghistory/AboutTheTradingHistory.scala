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

package models.submissions.aboutthetradinghistory

import actions.SessionRequest
import controllers.aboutthetradinghistory.ElectricVehicleChargingPointsController
import models.Session
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class AboutTheTradingHistory(
  occupationAndAccountingInformation: Option[OccupationalAndAccountingInformation] = None,
  turnoverSections: Seq[TurnoverSection] = Seq.empty,
  turnoverSections6030: Seq[TurnoverSection6030] = Seq.empty,
  costOfSales: Seq[CostOfSales] = Seq.empty,
  fixedOperatingExpensesSections: Seq[FixedOperatingExpenses] = Seq.empty,
  otherCosts: Option[OtherCosts] = None,
  totalPayrollCostSections: Seq[TotalPayrollCost] = Seq.empty,
  variableOperatingExpenses: Option[VariableOperatingExpensesSections] = None,
  incomeExpenditureSummary: Option[IncomeExpenditureSummary] = None,
  incomeExpenditureSummaryData: Seq[IncomeExpenditureSummaryData] = Seq.empty,
  unusualCircumstances: Option[UnusualCircumstances] = None,
  electricVehicleChargingPoints: Option[ElectricVehicleChargingPoints] = None, // added March 2024
  checkYourAnswersAboutTheTradingHistory: Option[CheckYourAnswersAboutTheTradingHistory] = None
)

object AboutTheTradingHistory {

  implicit val aboutTheTradingHistoryReads: Reads[AboutTheTradingHistory] = (
    (__ \ "occupationAndAccountingInformation").readNullable[OccupationalAndAccountingInformation] and
      (__ \ "turnoverSections").read[Seq[TurnoverSection]] and
      (__ \ "turnoverSections6030").readNullable[Seq[TurnoverSection6030]].map(_.getOrElse(Seq.empty)) and
      (__ \ "costOfSales").read[Seq[CostOfSales]] and
      (__ \ "fixedOperatingExpensesSections").read[Seq[FixedOperatingExpenses]] and
      (__ \ "otherCosts").readNullable[OtherCosts] and
      (__ \ "totalPayrollCostSections").read[Seq[TotalPayrollCost]] and
      (__ \ "variableOperatingExpenses").readNullable[VariableOperatingExpensesSections] and
      (__ \ "incomeExpenditureSummary").readNullable[IncomeExpenditureSummary] and
      (__ \ "incomeExpenditureSummaryData").read[Seq[IncomeExpenditureSummaryData]] and
      (__ \ "unusualCircumstances").readNullable[UnusualCircumstances] and
      (__ \ "electricVehicleChargingPoints").readNullable[ElectricVehicleChargingPoints] and
      (__ \ "checkYourAnswersAboutTheTradingHistory").readNullable[CheckYourAnswersAboutTheTradingHistory]
  )(AboutTheTradingHistory.apply _)

  implicit val format: Format[AboutTheTradingHistory] =
    Format(aboutTheTradingHistoryReads, Json.writes[AboutTheTradingHistory])

  def updateAboutTheTradingHistory(
    copy: AboutTheTradingHistory => AboutTheTradingHistory
  )(implicit sessionRequest: SessionRequest[_]): Session = {

    val currentAboutTheTradingHistory = sessionRequest.sessionData.aboutTheTradingHistory

    val updateAboutTheTradingHistory = currentAboutTheTradingHistory match {
      case Some(_) => sessionRequest.sessionData.aboutTheTradingHistory.map(copy)
      case _       => Some(copy(AboutTheTradingHistory()))
    }

    sessionRequest.sessionData.copy(aboutTheTradingHistory = updateAboutTheTradingHistory)

  }

}
