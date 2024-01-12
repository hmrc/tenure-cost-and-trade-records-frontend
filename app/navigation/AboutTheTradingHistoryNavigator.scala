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
import controllers.aboutthetradinghistory
import controllers.aboutthetradinghistory.routes
import models.{ForTypes, Session}
import navigation.identifiers._
import play.api.mvc.{AnyContent, Call, Request}
import play.api.Logging
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject

class AboutTheTradingHistoryNavigator @Inject() (audit: Audit) extends Navigator(audit) with Logging {

  override def cyaPage: Option[Call] =
    Some(aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show())

  override def nextPage(id: Identifier, session: Session)(implicit
    hc: HeaderCarrier,
    request: Request[AnyContent]
  ): Session => Call = {
    val nextPageFunc: Session => Call = super.nextPage(id, session)
    session =>
      if (from(request) == "IES") {
        iesSpecificRoute(session)
      } else {
        nextPageFunc(session)
      }
  }

  private def iesSpecificRoute(session: Session): Call =
    routes.IncomeExpenditureSummaryController.show()

  override val postponeCYARedirectPages: Set[String] = Set(
    aboutthetradinghistory.routes.FinancialYearEndController.show(),
    aboutthetradinghistory.routes.FinancialYearEndDatesController.show(),
    aboutthetradinghistory.routes.TurnoverController.show(),
    aboutthetradinghistory.routes.CostOfSalesController.show()
  ).map(_.url)

  private def financialYearEndRouting: Session => Call =
    _.aboutTheTradingHistory.flatMap(_.occupationAndAccountingInformation.flatMap(_.yearEndChanged)) match {
      case Some(true) => aboutthetradinghistory.routes.FinancialYearEndDatesController.show()
      case _          => aboutthetradinghistory.routes.TurnoverController.show()
    }

  private def turnoverRouting: Session => Call = answers => {
    if (answers.forType == ForTypes.for6015)
      aboutthetradinghistory.routes.CostOfSalesController.show()
    else
      aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show()
  }

  override val routeMap: Map[Identifier, Session => Call] = Map(
    AboutYourTradingHistoryPageId            -> (_ => aboutthetradinghistory.routes.FinancialYearEndController.show()),
    FinancialYearEndPageId                   -> financialYearEndRouting,
    FinancialYearEndDatesPageId              -> (_ => aboutthetradinghistory.routes.TurnoverController.show()),
    TurnoverPageId                           -> turnoverRouting,
    CostOfSalesId                            -> (_ => aboutthetradinghistory.routes.TotalPayrollCostsController.show()),
    TotalPayrollCostId                       -> (_ => aboutthetradinghistory.routes.VariableOperatingExpensesController.show()),
    VariableOperatingExpensesId              -> (_ => aboutthetradinghistory.routes.FixedOperatingExpensesController.show()),
    FixedOperatingExpensesId                 -> (_ => aboutthetradinghistory.routes.OtherCostsController.show()),
    OtherCostsId                             -> (_ => aboutthetradinghistory.routes.IncomeExpenditureSummaryController.show()),
    IncomeExpenditureSummaryId               -> (_ => aboutthetradinghistory.routes.UnusualCircumstancesController.show()),
    UnusualCircumstancesId                   -> (_ =>
      aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show()
    ),
//    NetProfitId                              -> (_ => aboutthetradinghistory.routes.CheckYourAnswersAboutTheTradingHistoryController.show()),
    CheckYourAnswersAboutTheTradingHistoryId -> (_ => controllers.routes.TaskListController.show())
  )

}
