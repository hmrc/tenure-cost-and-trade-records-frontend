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
import navigation.identifiers._
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec
import controllers.connectiontoproperty.routes

import scala.concurrent.ExecutionContext

class ConnectionToPropertyNavigatorSpec extends TestBaseSpec {

  val audit = mock[Audit]
  doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(any[HeaderCarrier], any[ExecutionContext])

  val navigator = new ConnectionToPropertyNavigator(audit)

  implicit override val hc: HeaderCarrier = HeaderCarrier()

  "Connection to property navigator" when {

    "go to sign in from an identifier that doesn't exist in the route map" in {
      case object UnknownIdentifier extends Identifier
      navigator
        .nextPage(UnknownIdentifier, stillConnectedDetailsYesSession)
        .apply(stillConnectedDetailsYesSession) mustBe controllers.routes.LoginController.show()
    }

    "cyaPageDependsOnSession() returns CYA page depending on session data" in {
      navigator.cyaPageDependsOnSession(
        stillConnectedDetailsYesToAllSession
      ) mustBe Some(routes.CheckYourAnswersConnectionToVacantPropertyController.show())

      navigator.cyaPageDependsOnSession(
        stillConnectedDetailsYesSession
      ) mustBe Some(routes.CheckYourAnswersConnectionToPropertyController.show())

      navigator.cyaPageDependsOnSession(
        stillConnectedDetailsNoSession
      ) mustBe Some(controllers.notconnected.routes.CheckYourAnswersNotConnectedController.show())
    }

    "return a function that goes to the type of connection to the property page when still connected has been selected and the selection is yes" in {
      navigator
        .nextPage(AreYouStillConnectedPageId, stillConnectedDetailsYesSession)
        .apply(stillConnectedDetailsYesSession) mustBe routes.VacantPropertiesController
        .show()
    }

    "return a function that goes to the edit address page when still connected has been selected and the selection is edit address" in {
      navigator
        .nextPage(AreYouStillConnectedPageId, stillConnectedDetailsEditSession)
        .apply(stillConnectedDetailsEditSession) mustBe routes.EditAddressController.show()
    }

    "return a function that goes to the not connected page when still connected has been selected and the selection is no" in {
      navigator
        .nextPage(AreYouStillConnectedPageId, stillConnectedDetailsNoSession)
        .apply(stillConnectedDetailsNoSession) mustBe controllers.notconnected.routes.PastConnectionController
        .show()
    }

    "return a function that goes to the vacancy status page when edit address has been completed" in {
      navigator
        .nextPage(EditAddressPageId, stillConnectedDetailsEditSession)
        .apply(stillConnectedDetailsEditSession) mustBe routes.VacantPropertiesController.show()
    }

    "return a function that goes to the vacant properties start date page when the property currently vacant has been answered with 'yes'" in {
      val nextPage = navigator
        .nextPage(PropertyBecomeVacantPageId, stillConnectedDetailsYesToAllSession)
        .apply(stillConnectedDetailsYesToAllSession)
      nextPage mustBe controllers.connectiontoproperty.routes.IsRentReceivedFromLettingController.show()
    }

    "return a function that goes to the trading name operating from property page when PropertyBecomeVacantPageId has been answered with 'no'" in {
      val nextPage = navigator
        .nextPage(PropertyBecomeVacantPageId, stillConnectedDetailsNoToAllSession)
        .apply(stillConnectedDetailsNoToAllSession)
      nextPage mustBe controllers.connectiontoproperty.routes.IsRentReceivedFromLettingController.show()
    }

    "return a function that goes to the are you third party page when TradingNameOwnThePropertyPageId has been answered with 'yes'" in {
      val nextPage = navigator
        .nextPage(TradingNameOwnThePropertyPageId, stillConnectedDetailsYesToAllSession)
        .apply(stillConnectedDetailsYesToAllSession)
      nextPage mustBe controllers.connectiontoproperty.routes.AreYouThirdPartyController.show()
    }

    "return a function that goes to the trading name paying rent page when TradingNameOwnThePropertyPageId has been answered with 'no'" in {
      val nextPage = navigator
        .nextPage(TradingNameOwnThePropertyPageId, stillConnectedDetailsNoToAllSession)
        .apply(stillConnectedDetailsNoToAllSession)
      nextPage mustBe controllers.connectiontoproperty.routes.TradingNamePayingRentController.show()
    }

    "return a function that goes to the check your answers page when AreYouThirdPartyPageId has been answered" in {
      val nextPage = navigator
        .nextPage(AreYouThirdPartyPageId, stillConnectedDetailsYesSession)
        .apply(stillConnectedDetailsYesSession)
      nextPage mustBe controllers.connectiontoproperty.routes.CheckYourAnswersConnectionToPropertyController.show()
    }

    "return a function that goes to the task list page when connection to the property has been selected" in {
      navigator
        .nextPage(ConnectionToPropertyPageId, stillConnectedDetailsYesSession)
        .apply(stillConnectedDetailsYesSession) mustBe controllers.routes.TaskListController
        .show()
    }

  }

  "return a function that goes from the vacant property page to Vacent property Start Date page if answer yes" in {
    navigator
      .nextPage(VacantPropertiesPageId, stillConnectedDetailsYesToAllSession)
      .apply(
        stillConnectedDetailsYesToAllSession
      ) mustBe controllers.connectiontoproperty.routes.VacantPropertiesStartDateController.show()
  }

  "return a function that goes from Letting Income page to vacant properties start date page if there is an income" in {
    navigator
      .nextPage(LettingIncomePageId, stillConnectedDetailsYesToAllSession)
      .apply(
        stillConnectedDetailsYesToAllSession
      ) mustBe controllers.connectiontoproperty.routes.AddAnotherLettingPartOfPropertyController.show(0)
  }

  "return a function that goes from trading name page to trading name own the property page" in {
    navigator
      .nextPage(TradingNameOperatingFromPropertyPageId, stillConnectedDetailsYesSession)
      .apply(
        stillConnectedDetailsYesSession
      ) mustBe controllers.connectiontoproperty.routes.TradingNameOwnThePropertyController.show()
  }

  "return a function that goes from provide your details page to check your answers - vacant property page" in {
    navigator
      .nextPage(ProvideYourContactDetailsPageId, stillConnectedDetailsYesToAllSession)
      .apply(
        stillConnectedDetailsYesToAllSession
      ) mustBe controllers.connectiontoproperty.routes.CheckYourAnswersConnectionToVacantPropertyController.show()
  }

  "return a function that goes from trading name paying rent page to are you third party page" in {
    navigator
      .nextPage(TradingNamePayingRentPageId, stillConnectedDetailsYesSession)
      .apply(
        stillConnectedDetailsYesSession
      ) mustBe controllers.connectiontoproperty.routes.AreYouThirdPartyController.show()
  }

  "return a function that goes from letting part of the property details page to annual rent page" in {
    navigator
      .nextPage(LettingPartOfPropertyDetailsPageId, stillConnectedDetailsYesSession)
      .apply(
        stillConnectedDetailsYesSession
      ) mustBe controllers.connectiontoproperty.routes.LettingPartOfPropertyDetailsRentController.show(0)
  }

  "return a function that goes from annual rent page to items included in rent page" in {
    navigator
      .nextPage(LettingPartOfPropertyRentDetailsPageId, stillConnectedDetailsYesSession)
      .apply(
        stillConnectedDetailsYesSession
      ) mustBe controllers.connectiontoproperty.routes.LettingPartOfPropertyItemsIncludedInRentController.show(0)
  }

  "return a function that goes from items included in rent page to add another letting page" in {
    navigator
      .nextPage(LettingPartOfPropertyItemsIncludedInRentPageId, stillConnectedDetailsYesSession)
      .apply(
        stillConnectedDetailsYesSession
      ) mustBe controllers.connectiontoproperty.routes.AddAnotherLettingPartOfPropertyController.show(0)
  }

  "return a function that goes from add another letting page to letting part of property details page" in {
    navigator
      .nextPage(AddAnotherLettingPartOfPropertyPageId, stillConnectedDetailsYesToAllSession)
      .apply(
        stillConnectedDetailsYesToAllSession
      ) mustBe controllers.connectiontoproperty.routes.LettingPartOfPropertyDetailsController.show(Some(1))
  }

  "return a function that goes from CYA page to tasklist" in {
    navigator
      .nextPage(CheckYourAnswersAboutThePropertyPageId, stillConnectedDetailsYesSession)
      .apply(
        stillConnectedDetailsYesSession
      ) mustBe controllers.routes.LoginController.show()
  }
// 6076

  "return a function that goes to the trading name page when still connected has been selected and the selection is yes" in {
    navigator
      .nextPage(AreYouStillConnectedPageId, stillConnectedDetails6076YesSession)
      .apply(stillConnectedDetails6076YesSession) mustBe routes.TradingNameOperatingFromPropertyController
      .show()
  }

  "return a function that goes from edit the address to the trading name page " in {
    navigator
      .nextPage(EditAddressPageId, stillConnectedDetails6076YesSession)
      .apply(stillConnectedDetails6076YesSession) mustBe routes.TradingNameOperatingFromPropertyController
      .show()
  }

}
