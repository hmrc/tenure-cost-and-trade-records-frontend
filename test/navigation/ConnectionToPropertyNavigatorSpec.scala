/*
 * Copyright 2026 HM Revenue & Customs
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

import controllers.connectiontoproperty.routes
import models.submissions.common.AnswersYesNo.*
import models.submissions.connectiontoproperty.{LettingPartOfPropertyDetails, LettingPartOfPropertyRentDetails, TenantDetails}
import navigation.identifiers.*
import test.{InjectedNavigation, TCTRAppSpec}

import scala.language.implicitConversions

class ConnectionToPropertyNavigatorSpec extends TCTRAppSpec with InjectedNavigation:

  "Connection to the property navigator" should {
    "redirect to default page for identifier that doesn't exist in the route map" in {
      connectedToPropertyNavigator
        .nextPage(UnknownIdentifier, stillConnectedDetailsYesSession)
        .apply(stillConnectedDetailsYesSession) shouldBe controllers.routes.LoginController.show
    }

    "cyaPageDependsOnSession() returns CYA page depending on session data" in {
      connectedToPropertyNavigator.cyaPageDependsOnSession(
        stillConnectedDetailsYesToAllSession
      ) shouldBe Some(routes.CheckYourAnswersConnectionToVacantPropertyController.show())

      connectedToPropertyNavigator.cyaPageDependsOnSession(
        stillConnectedDetailsYesSession
      ) shouldBe Some(routes.CheckYourAnswersConnectionToPropertyController.show())

      connectedToPropertyNavigator.cyaPageDependsOnSession(
        stillConnectedDetailsNoSession
      ) shouldBe Some(controllers.notconnected.routes.CheckYourAnswersNotConnectedController.show())
    }

    "redirect to the type of connection to the property page when still connected has been selected and the selection is yes" in {
      connectedToPropertyNavigator
        .nextPage(AreYouStillConnectedPageId, stillConnectedDetailsYesSession)
        .apply(stillConnectedDetailsYesSession) shouldBe
        routes.VacantPropertiesController
          .show()
    }

    "redirect to the edit address page when still connected has been selected and the selection is edit address" in {
      connectedToPropertyNavigator
        .nextPage(AreYouStillConnectedPageId, stillConnectedDetailsEditSession)
        .apply(stillConnectedDetailsEditSession) shouldBe routes.EditAddressController.show()
    }

    "redirect to the not connected page when still connected has been selected and the selection is no" in {
      connectedToPropertyNavigator
        .nextPage(AreYouStillConnectedPageId, stillConnectedDetailsNoSession)
        .apply(stillConnectedDetailsNoSession) shouldBe
        controllers.notconnected.routes.PastConnectionController
          .show()
    }

    "redirect to the vacancy status page when edit address has been completed" in {
      connectedToPropertyNavigator
        .nextPage(EditAddressPageId, stillConnectedDetailsEditSession)
        .apply(stillConnectedDetailsEditSession) shouldBe routes.VacantPropertiesController.show()
    }

    "redirect to the vacant properties start date page when the property currently vacant has been answered with 'yes'" in {
      val nextPage = connectedToPropertyNavigator
        .nextPage(PropertyBecomeVacantPageId, stillConnectedDetailsYesToAllSession)
        .apply(stillConnectedDetailsYesToAllSession)
      nextPage shouldBe controllers.connectiontoproperty.routes.IsRentReceivedFromLettingController.show()
    }

    "redirect to the trading name operating from property page when PropertyBecomeVacantPageId has been answered with 'no'" in {
      val nextPage = connectedToPropertyNavigator
        .nextPage(PropertyBecomeVacantPageId, stillConnectedDetailsNoToAllSession)
        .apply(stillConnectedDetailsNoToAllSession)
      nextPage shouldBe controllers.connectiontoproperty.routes.IsRentReceivedFromLettingController.show()
    }

    "redirect to the are you third party page when TradingNameOwnThePropertyPageId has been answered with 'yes'" in {
      val nextPage = connectedToPropertyNavigator
        .nextPage(TradingNameOwnThePropertyPageId, stillConnectedDetailsYesToAllSession)
        .apply(stillConnectedDetailsYesToAllSession)
      nextPage shouldBe controllers.connectiontoproperty.routes.AreYouThirdPartyController.show()
    }

    "redirect to the trading name paying rent page when TradingNameOwnThePropertyPageId has been answered with 'no'" in {
      val nextPage = connectedToPropertyNavigator
        .nextPage(TradingNameOwnThePropertyPageId, stillConnectedDetailsNoToAllSession)
        .apply(stillConnectedDetailsNoToAllSession)
      nextPage shouldBe controllers.connectiontoproperty.routes.TradingNamePayingRentController.show()
    }

    "redirect to the check your answers page when AreYouThirdPartyPageId has been answered" in {
      val nextPage = connectedToPropertyNavigator
        .nextPage(AreYouThirdPartyPageId, stillConnectedDetailsYesSession)
        .apply(stillConnectedDetailsYesSession)
      nextPage shouldBe controllers.connectiontoproperty.routes.CheckYourAnswersConnectionToPropertyController.show()
    }

    "redirect to the task list page when connection to the property has been selected" in {
      connectedToPropertyNavigator
        .nextPage(ConnectionToPropertyPageId, stillConnectedDetailsYesSession)
        .apply(stillConnectedDetailsYesSession) shouldBe controllers.routes.TaskListController.show
    }

    "redirect from the vacant property page to vacant property Start Date page if answer yes" in {
      connectedToPropertyNavigator
        .nextPage(VacantPropertiesPageId, stillConnectedDetailsYesToAllSession)
        .apply(
          stillConnectedDetailsYesToAllSession
        ) shouldBe controllers.connectiontoproperty.routes.VacantPropertiesStartDateController.show()
    }

    "redirect from the vacant property page to vacant property Start Date page if answer no" in {
      connectedToPropertyNavigator
        .nextPage(VacantPropertiesPageId, stillConnectedDetailsNoToAllSession)
        .apply(
          stillConnectedDetailsNoToAllSession
        ) shouldBe controllers.connectiontoproperty.routes.TradingNameOperatingFromPropertyController.show()
    }

    "redirect from Letting Income page to vacant properties start date page if there is an income" in {
      connectedToPropertyNavigator
        .nextPage(LettingIncomePageId, stillConnectedDetailsYesToAllSession)
        .apply(
          stillConnectedDetailsYesToAllSession
        ) shouldBe controllers.connectiontoproperty.routes.AddAnotherLettingPartOfPropertyController.show(0)
    }

    "redirect from Letting Income page to provide contact details page" in {
      connectedToPropertyNavigator
        .nextPage(LettingIncomePageId, stillConnectedDetailsNoToAllSession)
        .apply(
          stillConnectedDetailsNoToAllSession
        ) shouldBe controllers.connectiontoproperty.routes.ProvideContactDetailsController.show()
    }

    "redirect from letting income  page to Letting Details page, when no details" in {
      val sessionWithEmptyLettingPartOfPropertyDetails = stillConnectedDetailsYesToAllSession.copy(
        stillConnectedDetails =
          Option(prefilledStillConnectedDetailsYesToAll.copy(lettingPartOfPropertyDetails = IndexedSeq()))
      )

      connectedToPropertyNavigator
        .nextPage(LettingIncomePageId, sessionWithEmptyLettingPartOfPropertyDetails)
        .apply(
          sessionWithEmptyLettingPartOfPropertyDetails
        ) shouldBe controllers.connectiontoproperty.routes.LettingPartOfPropertyDetailsController.show()
    }

    "redirect from trading name page to trading name own the property page" in {
      connectedToPropertyNavigator
        .nextPage(TradingNameOperatingFromPropertyPageId, stillConnectedDetailsYesSession)
        .apply(
          stillConnectedDetailsYesSession
        ) shouldBe controllers.connectiontoproperty.routes.TradingNameOwnThePropertyController.show()
    }

    "redirect from provide your details page to check your answers - vacant property page" in {
      connectedToPropertyNavigator
        .nextPage(ProvideYourContactDetailsPageId, stillConnectedDetailsYesToAllSession)
        .apply(
          stillConnectedDetailsYesToAllSession
        ) shouldBe controllers.connectiontoproperty.routes.CheckYourAnswersConnectionToVacantPropertyController.show()
    }

    "redirect from trading name paying rent page to are you third party page" in {
      connectedToPropertyNavigator
        .nextPage(TradingNamePayingRentPageId, stillConnectedDetailsYesSession)
        .apply(
          stillConnectedDetailsYesSession
        ) shouldBe controllers.connectiontoproperty.routes.AreYouThirdPartyController.show()
    }

    "redirect from letting part of the property details page to annual rent page" in {
      connectedToPropertyNavigator
        .nextPage(LettingPartOfPropertyDetailsPageId, stillConnectedDetailsYesSession)
        .apply(
          stillConnectedDetailsYesSession
        ) shouldBe controllers.connectiontoproperty.routes.LettingPartOfPropertyDetailsRentController.show(0)
    }

    "redirect from annual rent page to items included in rent page" in {
      connectedToPropertyNavigator
        .nextPage(LettingPartOfPropertyRentDetailsPageId, stillConnectedDetailsYesSession)
        .apply(
          stillConnectedDetailsYesSession
        ) shouldBe controllers.connectiontoproperty.routes.LettingPartOfPropertyItemsIncludedInRentController.show(0)
    }

    "redirect from items included in rent page to add another letting page" in {
      connectedToPropertyNavigator
        .nextPage(LettingPartOfPropertyItemsIncludedInRentPageId, stillConnectedDetailsYesSession)
        .apply(
          stillConnectedDetailsYesSession
        ) shouldBe controllers.connectiontoproperty.routes.AddAnotherLettingPartOfPropertyController.show(0)
    }

    "redirect from add another letting page to letting part of property details page" in {
      connectedToPropertyNavigator
        .nextPage(AddAnotherLettingPartOfPropertyPageId, stillConnectedDetailsYesToAllSession)
        .apply(
          stillConnectedDetailsYesToAllSession
        ) shouldBe controllers.connectiontoproperty.routes.LettingPartOfPropertyDetailsController.show(Some(1))
    }

    "redirect from CYA page to tasklist" in {
      connectedToPropertyNavigator
        .nextPage(CheckYourAnswersAboutThePropertyPageId, stillConnectedDetailsYesSession)
        .apply(
          stillConnectedDetailsYesSession
        ) shouldBe controllers.routes.LoginController.show
    }

    "redirect to the trading name page when still connected has been selected and the selection is yes" in {
      connectedToPropertyNavigator
        .nextPage(AreYouStillConnectedPageId, stillConnectedDetails6076YesSession)
        .apply(stillConnectedDetails6076YesSession) shouldBe
        routes.TradingNameOperatingFromPropertyController
          .show()
    }

    "redirect from edit the address to the trading name page " in {
      connectedToPropertyNavigator
        .nextPage(EditAddressPageId, stillConnectedDetails6076YesSession)
        .apply(stillConnectedDetails6076YesSession) shouldBe
        routes.TradingNameOperatingFromPropertyController
          .show()
    }

    "redirect to LettingPartOfPropertyDetailsRentController when lettingPartOfPropertyRentDetails are incomplete" in {
      val incompleteDetails = IndexedSeq(
        testLettingPartOfPropertyDetails.copy(
          tenantDetails = TenantDetails("Name", "Description", None),
          lettingPartOfPropertyRentDetails = None
        )
      )
      val session           = stillConnectedDetailsYesToAllSession.copy(
        stillConnectedDetails =
          prefilledStillConnectedDetailsYesToAll.copy(
            lettingPartOfPropertyDetails = incompleteDetails,
            isAnyRentReceived = AnswerYes
          )
      )

      connectedToPropertyNavigator
        .nextPage(LettingIncomePageId, session)
        .apply(session) shouldBe routes.LettingPartOfPropertyDetailsRentController.show(0)
    }

    "redirect to ProvideContactDetails when no letting details" in {
      val session = stillConnectedDetailsYesToAllSession.copy(
        stillConnectedDetails =
          prefilledStillConnectedDetailsYesToAll.copy(
            lettingPartOfPropertyDetails = IndexedSeq.empty,
            lettingPartOfPropertyDetailsIndex = 0
          )
      )

      connectedToPropertyNavigator
        .nextPage(AddAnotherLettingPartOfPropertyPageId, session)
        .apply(session) shouldBe routes.ProvideContactDetailsController.show()
    }

    "redirect to ProvideContactDetails when section is complete and addAnother is No" in {
      val completeDetails = IndexedSeq(
        testLettingPartOfPropertyDetails.copy(
          tenantDetails = testTenantDetails,
          lettingPartOfPropertyRentDetails = testLettingDetails,
          itemsIncludedInRent = List("test"),
          addAnotherLettingToProperty = AnswerNo
        )
      )

      val session = stillConnectedDetailsYesToAllSession.copy(
        stillConnectedDetails =
          prefilledStillConnectedDetailsYesToAll.copy(
            lettingPartOfPropertyDetails = completeDetails,
            lettingPartOfPropertyDetailsIndex = 0,
            isAnyRentReceived = AnswerYes
          )
      )

      connectedToPropertyNavigator
        .nextPage(AddAnotherLettingPartOfPropertyPageId, session)
        .apply(session) shouldBe routes.ProvideContactDetailsController.show()
    }

    "redirect to LettingPartOfPropertyDetails page when section is complete and addAnother is Yes" in {
      val completeDetails = IndexedSeq(
        testLettingPartOfPropertyDetails.copy(addAnotherLettingToProperty = AnswerYes)
      )

      val session = stillConnectedDetailsYesToAllSession.copy(
        stillConnectedDetails =
          prefilledStillConnectedDetailsYesToAll.copy(
            lettingPartOfPropertyDetails = completeDetails,
            lettingPartOfPropertyDetailsIndex = 0
          )
      )

      connectedToPropertyNavigator
        .nextPage(AddAnotherLettingPartOfPropertyPageId, session)
        .apply(session) shouldBe routes.LettingPartOfPropertyDetailsController.show(Some(1))
    }
  }

  "getIncompleteSectionCall" should {

    def createLettingDetails(
      tenantDetails: TenantDetails = testTenantDetails,
      rentDetails: Option[LettingPartOfPropertyRentDetails] = testLettingDetails,
      itemsIncluded: List[String] = List("item1", "item2")
    ): LettingPartOfPropertyDetails =
      LettingPartOfPropertyDetails(tenantDetails, rentDetails, itemsIncluded)

    "return the correct Call when tenantDetails is null" in {
      val detail = createLettingDetails(tenantDetails = null)
      val result = connectedToPropertyNavigator.getIncompleteSectionCall(detail, 0)
      result shouldBe routes.LettingPartOfPropertyDetailsController.show(Some(0))
    }

    "return the correct Call when lettingPartOfPropertyRentDetails is None" in {
      val detail = createLettingDetails(rentDetails = None)
      val result = connectedToPropertyNavigator.getIncompleteSectionCall(detail, 1)
      result shouldBe routes.LettingPartOfPropertyDetailsRentController.show(1)
    }

    "return the correct Call when itemsIncludedInRent is empty" in {
      val detail = createLettingDetails(itemsIncluded = List.empty)
      val result = connectedToPropertyNavigator.getIncompleteSectionCall(detail, 2)
      result shouldBe routes.LettingPartOfPropertyItemsIncludedInRentController.show(2)
    }

    "return the correct Call when all fields are populated" in {
      val detail = createLettingDetails()
      val result = connectedToPropertyNavigator.getIncompleteSectionCall(detail, 3)
      result shouldBe routes.LettingPartOfPropertyDetailsController.show(Some(3))
    }
  }
