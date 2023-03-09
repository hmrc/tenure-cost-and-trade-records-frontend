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

package navigation.identifiers

import utils.TestBaseSpec

class AboutTheTradingHistoryIdentifiersSpec extends TestBaseSpec {

  "About your trading history identifiers" when {

    "Identifier for about your trading history page" in {
      assert(AboutYourTradingHistoryPageId.toString.equals("aboutYourTradingHistoryPage"))
    }

    "Identifier for turnover page" in {
      assert(TurnoverPageId.toString.equals("turnoverPage"))
    }

    "Identifier for cost of sales or gross profit page" in {
      assert(CostOfSalesOrGrossProfitId.toString.equals("costOfSalesOrGrossProfit"))
    }

    "Identifier for cost of sales page" in {
      assert(CostOfSalesId.toString.equals("costOfSales"))
    }

    "Identifier for gross profit page" in {
      assert(GrossProfitsId.toString.equals("grossProfit"))
    }

    "Identifier for total payroll page" in {
      assert(TotalPayrollCostId.toString.equals("totalPayrollCost"))
    }

    "Identifier for about variable operating expenses page" in {
      assert(VariableOperatingExpensesId.toString.equals("variableOperatingExpenses"))
    }

    "Identifier for fixed operating expenses page" in {
      assert(FixedOperatingExpensesId.toString.equals("fixedOperatingExpenses"))
    }

    "Identifier for other costs page" in {
      assert(OtherCostsId.toString.equals("otherCosts"))
    }

    "Identifier for net profit page" in {
      assert(NetProfitId.toString.equals("netProfit"))
    }

    "Identifier for check your answers about the trading history page" in {
      assert(CheckYourAnswersAboutTheTradingHistoryId.toString.equals("checkYourAnswersAboutTheTradingHistory"))
    }
  }
}
