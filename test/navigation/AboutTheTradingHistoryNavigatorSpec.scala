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
import models.Session
import models.submissions.common.ContactDetails
import models.submissions.connectiontoproperty.{AddressConnectionTypeYes, StillConnectedDetails}
import models.submissions.notconnected.{RemoveConnectionDetails, RemoveConnectionsDetails}
import navigation.identifiers._
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec

import scala.concurrent.ExecutionContext

class AboutTheTradingHistoryNavigatorSpec extends TestBaseSpec {

  val audit: Audit = mock[Audit]
  doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(any[HeaderCarrier], any[ExecutionContext])

  val navigator = new AboutTheTradingHistoryNavigator(audit)

  val stillConnectedDetailsYes: Option[StillConnectedDetails] = Some(
    StillConnectedDetails(Some(AddressConnectionTypeYes))
  )
  val removeConnection: Option[RemoveConnectionDetails]       = Some(
    RemoveConnectionDetails(
      Some(
        RemoveConnectionsDetails(
          "John Smith",
          ContactDetails("12345678909", "test@email.com"),
          Some("Additional Information is here")
        )
      )
    )
  )

  val sessionAboutYou: Session =
    Session(
      "99996010004",
      "FOR6010",
      prefilledAddress,
      "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
      stillConnectedDetailsYes,
      removeConnection
    )

  implicit override val hc: HeaderCarrier = HeaderCarrier()

  "About the trading history navigator" when {

    "go to sign in from an identifier that doesn't exist in the route map" in {
      case object UnknownIdentifier extends Identifier
      navigator.nextPage(UnknownIdentifier).apply(sessionAboutYou) mustBe controllers.routes.LoginController.show()
    }

    "return a function that goes the turnover page when about your trading history has been completed" in {
      navigator
        .nextPage(AboutYourTradingHistoryPageId)
        .apply(sessionAboutYou) mustBe controllers.aboutthetradinghistory.routes.TurnoverController.show()
    }

    "return a function that goes the total payroll costs page when about your cost of sales has been completed" in {
      navigator
        .nextPage(CostOfSalesId)
        .apply(sessionAboutYou) mustBe controllers.aboutthetradinghistory.routes.TotalPayrollCostsController.show()
    }

    "return a function that goes the variable operating expenses page when total payroll page has been completed" in {
      navigator
        .nextPage(TotalPayrollCostId)
        .apply(
          sessionAboutYou
        ) mustBe controllers.aboutthetradinghistory.routes.VariableOperatingExpensesController.show()
    }

    "return a function that goes the fixed operating expenses page when variable operating expenses has been completed" in {
      navigator
        .nextPage(VariableOperatingExpensesId)
        .apply(sessionAboutYou) mustBe controllers.aboutthetradinghistory.routes.FixedOperatingExpensesController.show()
    }

    "return a function that goes other costs page when fixed operating expenses has been completed" in {
      navigator
        .nextPage(FixedOperatingExpensesId)
        .apply(sessionAboutYou) mustBe controllers.aboutthetradinghistory.routes.OtherCostsController.show()
    }

    "return a function that goes the task lists page when total payroll cost has been completed" in {
      navigator
        .nextPage(CheckYourAnswersAboutTheTradingHistoryId)
        .apply(sessionAboutYou) mustBe controllers.routes.TaskListController.show()
    }

  }
}
