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
import navigation.identifiers.{AreYouStillConnectedPageId, AreYouThirdPartyPageId, CheckYourAnswersAboutThePropertyPageId, CheckYourAnswersRequestReferenceNumberPageId, ConnectionToPropertyPageId, DownloadPDFReferenceNumberPageId, EditAddressPageId, Identifier, LettingIncomePageId, NoReferenceNumberContactDetailsPageId, NoReferenceNumberPageId, PropertyBecomeVacantPageId, TradingNameOperatingFromPropertyPageId, TradingNameOwnThePropertyPageId, VacantPropertiesPageId}
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
      nextPage mustBe controllers.connectiontoproperty.routes.VacantPropertiesStartDateController.show()
    }

    "return a function that goes to the trading name operating from property page when PropertyBecomeVacantPageId has been answered with 'no'" in {
      val nextPage = navigator
        .nextPage(PropertyBecomeVacantPageId, stillConnectedDetailsNoToAllSession)
        .apply(stillConnectedDetailsNoToAllSession)
      nextPage mustBe controllers.connectiontoproperty.routes.TradingNameOperatingFromPropertyController.show()
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

    "return a function that goes to the no reference number contact details page when connection to the property has been selected" in {
      navigator
        .nextPage(NoReferenceNumberPageId, stillConnectedDetailsYesSession)
        .apply(
          stillConnectedDetailsYesSession
        ) mustBe controllers.requestReferenceNumber.routes.RequestReferenceNumberContactDetailsController
        .show()
    }
  }

  "return a function that goes to the check your answers request reference number page when connection to the property has been selected" in {
    navigator
      .nextPage(CheckYourAnswersRequestReferenceNumberPageId, stillConnectedDetailsYesSession)
      .apply(
        stillConnectedDetailsYesSession
      ) mustBe controllers.routes.RequestReferenceNumberFormSubmissionController.submit()
  }

  "return a function that goes to the download pdf reference number page" in {
    navigator
      .nextPage(DownloadPDFReferenceNumberPageId, stillConnectedDetailsYesSession)
      .apply(
        stillConnectedDetailsYesSession
      ) mustBe controllers.downloadFORTypeForm.routes.DownloadPDFController.show()
  }

  "return a function that goes from the vacant property page to tasklist" in {
    navigator
      .nextPage(VacantPropertiesPageId, stillConnectedDetailsYesSession)
      .apply(
        stillConnectedDetailsYesSession
      ) mustBe controllers.routes.TaskListController.show()
  }

  "return a function that goes from Letting Income page to vacant properties page" in {
    navigator
      .nextPage(LettingIncomePageId, stillConnectedDetailsYesSession)
      .apply(
        stillConnectedDetailsYesSession
      ) mustBe controllers.connectiontoproperty.routes.VacantPropertiesController.show()
  }

  "return a function that goes from trading name page to trading name own the property page" in {
    navigator
      .nextPage(TradingNameOperatingFromPropertyPageId, stillConnectedDetailsYesSession)
      .apply(
        stillConnectedDetailsYesSession
      ) mustBe controllers.connectiontoproperty.routes.TradingNameOwnThePropertyController.show()
  }

  "return a function that goes from CYA page to tasklist" in {
    navigator
      .nextPage(CheckYourAnswersAboutThePropertyPageId, stillConnectedDetailsYesSession)
      .apply(
        stillConnectedDetailsYesSession
      ) mustBe controllers.routes.LoginController.show()
  }
}
