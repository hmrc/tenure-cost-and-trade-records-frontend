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
import models.Session
import play.api.libs.json.Json

case class AboutTheTradingHistory(
  aboutYourTradingHistory: Option[AboutYourTradingHistory] = None,
  turnoverSection: Seq[TurnoverSection] = Seq.empty,
  costOfSales: Option[CostOfSales] = None,
  fixedOperatingExpenses: Option[FixedOperatingExpenses] = None,
  grossProfit: Option[GrossProfit] = None,
  netProfit: Option[NetProfit] = None,
  otherCosts: Option[OtherCosts] = None,
  totalPayrollCost: Option[TotalPayrollCost] = None,
  variableOperatingExpenses: Option[VariableOperatingExpenses] = None,
  checkYourAnswersAboutTheTradingHistory: Option[CheckYourAnswersAboutTheTradingHistory] = None
)

object AboutTheTradingHistory {
  implicit val format = Json.format[AboutTheTradingHistory]

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
