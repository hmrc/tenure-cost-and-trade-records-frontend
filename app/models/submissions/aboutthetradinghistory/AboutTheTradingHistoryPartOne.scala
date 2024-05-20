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
import play.api.libs.json._

case class AboutTheTradingHistoryPartOne(
  isFinancialYearEndDatesCorrect: Option[Boolean] = Some(false)
)
object AboutTheTradingHistoryPartOne {
  implicit val format: OFormat[AboutTheTradingHistoryPartOne] = Json.format[AboutTheTradingHistoryPartOne]

  def updateAboutTheTradingHistoryPartOne(
    copy: AboutTheTradingHistoryPartOne => AboutTheTradingHistoryPartOne
  )(implicit sessionRequest: SessionRequest[_]): Session = {

    val currentAboutTheTradingHistoryPartOne = sessionRequest.sessionData.aboutTheTradingHistoryPartOne

    val updateAboutTheTradingHistoryPartOne = currentAboutTheTradingHistoryPartOne match {
      case Some(_) => sessionRequest.sessionData.aboutTheTradingHistoryPartOne.map(copy)
      case _       => Some(copy(AboutTheTradingHistoryPartOne()))
    }

    sessionRequest.sessionData.copy(aboutTheTradingHistoryPartOne = updateAboutTheTradingHistoryPartOne)

  }
}
