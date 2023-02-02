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

package navigation

import connectors.Audit
import models.{ForTypes, Session}
import navigation.identifiers._
import play.api.mvc.Call
import play.api.Logging

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class AboutTheTradingHistoryNavigator @Inject() (audit: Audit)(implicit ec: ExecutionContext) extends Navigator(audit)
  with Logging {

  private def turnoverRouting: Session => Call = answers => {
    if (answers.userLoginDetails.forNumber == ForTypes.for6015)
      controllers.aboutthetradinghistory.routes.TotalPayrollCostsController.show()
    else
      controllers.Form6010.routes.FranchiseOrLettingsTiedToPropertyController.show()
}

  private def costOfSalesOrGrossProfitDetailsRouting: Session => Call = answers => {
    answers.aboutTheTradingHistory.flatMap(_.costOfSalesOrGrossProfit.map(_.name)) match {
      case Some("costOfSales") => controllers.aboutthetradinghistory.routes.CostOfSalesController.show()
      case Some("grossProfit")  => controllers.aboutthetradinghistory.routes.GrossProfitsController.show()
      case _           =>
        logger.warn(
          s"Navigation for about the property reached without correct selection of tied goods by controller"
        )
        throw new RuntimeException("Invalid option exception for tied goods routing")
    }
  }

  override val routeMap: Map[Identifier, Session => Call] = Map(
    AboutYourTradingHistoryPageId     -> (_ => controllers.aboutthetradinghistory.routes.TurnoverController.show()),
    TurnoverPageId                    -> turnoverRouting,
    CostOfSalesOrGrossProfitId        -> costOfSalesOrGrossProfitDetailsRouting,
    TotalPayrollCostId                -> (_ => controllers.aboutthetradinghistory.routes.VariableOperatingExpensesController.show()),
    VariableOperatingExpensesId       -> (_ => controllers.aboutthetradinghistory.routes.FixedOperatingExpensesController.show()),
    FixedOperatingExpensesId          -> (_ => controllers.aboutthetradinghistory.routes.OtherCostsController.show()),
    OtherCostsId                      -> (_ => controllers.aboutthetradinghistory.routes.NetProfitController.show()),
    NetProfitId                       -> (_ => controllers.Form6010.routes.FranchiseOrLettingsTiedToPropertyController.show())
  )
}
