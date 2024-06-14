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

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import utils.FakeObjects

/**
  * @author Yuriy Tumakha
  */
class AboutTheTradingHistorySpec extends AnyFlatSpec with Matchers with FakeObjects {

  "AboutTheTradingHistory" should "handle turnover models" in {
    val tradingHistory = prefilledAboutYourTradingHistory
    tradingHistory.costOfSales.map(_.total).sum                                                shouldBe 12
    tradingHistory.fixedOperatingExpensesSections.map(_.total).sum                             shouldBe 15
    tradingHistory.otherCosts.map(_.otherCosts.map(_.total).sum)                               shouldBe Some(6)
    tradingHistory.variableOperatingExpenses.map(_.variableOperatingExpenses.map(_.total).sum) shouldBe Some(24)
  }

}
