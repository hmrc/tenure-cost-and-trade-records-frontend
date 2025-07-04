/*
 * Copyright 2025 HM Revenue & Customs
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
import navigation.identifiers.*
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec
import controllers.connectiontoproperty.routes
import models.submissions.common.AnswersYesNo.*
import models.submissions.connectiontoproperty.{LettingPartOfPropertyDetails, LettingPartOfPropertyRentDetails, TenantDetails}

import scala.concurrent.ExecutionContext

class ConnectionToPropertyNavigatorSpec extends TestBaseSpec {

  val audit = mock[Audit]
  doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(using any[HeaderCarrier], any[ExecutionContext])

  val navigator = new ConnectionToPropertyNavigator(audit)

  implicit override val hc: HeaderCarrier = HeaderCarrier()

  "Connection to property navigator" when {

    "go to sign in from an identifier that doesn't exist in the route map" in {
      case object UnknownIdentifier extends Identifier
      navigator
        .nextPage(UnknownIdentifier, stillConnectedDetailsYesSession)
        .apply(stillConnectedDetailsYesSession) shouldBe controllers.routes.LoginController.show
    }

    "cyaPageDependsOnSession() returns CYA page depending on session data" in {
      navigator.cyaPageDependsOnSession(
        stillConnectedDetailsYesToAllSession
      ) shouldBe Some(routes.CheckYourAnswersConnectionToVacantPropertyController.show())

      navigator.cyaPageDependsOnSession(
        stillConnectedDetailsYesSession
      ) shouldBe Some(routes.CheckYourAnswersConnectionToPropertyController.show())

      navigator.cyaPageDependsOnSession(
        stillConnectedDetailsNoSession
      ) shouldBe Some(controllers.notconnected.routes.CheckYourAnswersNotConnectedController.show())
    }

    "return a function that goes to the type of connection to the property page when still connected has been selected and the selection is yes" in {
      navigator
        .nextPage(AreYouStillConnectedPageId, stillConnectedDetailsYesSession)
        .apply(stillConnectedDetailsYesSession) shouldBe routes.VacantPropertiesController
        .show()
    }

    "return a function that goes to the edit address page when still connected has been selected and the selection is edit address" in {
      navigator
        .nextPage(AreYouStillConnectedPageId, stillConnectedDetailsEditSession)
        .apply(stillConnectedDetailsEditSession) shouldBe routes.EditAddressController.show()
    }

    "return a function that goes to the not connected page when still connected has been selected and the selection is no" in {
      navigator
        .nextPage(AreYouStillConnectedPageId, stillConnectedDetailsNoSession)
        .apply(stillConnectedDetailsNoSession) shouldBe controllers.notconnected.routes.PastConnectionController
        .show()
    }

    "return a function that goes to the vacancy status page when edit address has been completed" in {
      navigator
        .nextPage(EditAddressPageId, stillConnectedDetailsEditSession)
        .apply(stillConnectedDetailsEditSession) shouldBe routes.VacantPropertiesController.show()
    }

    "return a function that goes to the vacant properties start date page when the property currently vacant has been answered with 'yes'" in {
      val nextPage = navigator
        .nextPage(PropertyBecomeVacantPageId, stillConnectedDetailsYesToAllSession)
        .apply(stillConnectedDetailsYesToAllSession)
      nextPage shouldBe controllers.connectiontoproperty.routes.IsRentReceivedFromLettingController.show()
    }

    "return a function that goes to the trading name operating from property page when PropertyBecomeVacantPageId has been answered with 'no'" in {
      val nextPage = navigator
        .nextPage(PropertyBecomeVacantPageId, stillConnectedDetailsNoToAllSession)
        .apply(stillConnectedDetailsNoToAllSession)
      nextPage shouldBe controllers.connectiontoproperty.routes.IsRentReceivedFromLettingController.show()
    }

    "return a function that goes to the are you third party page when TradingNameOwnThePropertyPageId has been answered with 'yes'" in {
      val nextPage = navigator
        .nextPage(TradingNameOwnThePropertyPageId, stillConnectedDetailsYesToAllSession)
        .apply(stillConnectedDetailsYesToAllSession)
      nextPage shouldBe controllers.connectiontoproperty.routes.AreYouThirdPartyController.show()
    }

    "return a function that goes to the trading name paying rent page when TradingNameOwnThePropertyPageId has been answered with 'no'" in {
      val nextPage = navigator
        .nextPage(TradingNameOwnThePropertyPageId, stillConnectedDetailsNoToAllSession)
        .apply(stillConnectedDetailsNoToAllSession)
      nextPage shouldBe controllers.connectiontoproperty.routes.TradingNamePayingRentController.show()
    }

    "return a function that goes to the check your answers page when AreYouThirdPartyPageId has been answered" in {
      val nextPage = navigator
        .nextPage(AreYouThirdPartyPageId, stillConnectedDetailsYesSession)
        .apply(stillConnectedDetailsYesSession)
      nextPage shouldBe controllers.connectiontoproperty.routes.CheckYourAnswersConnectionToPropertyController.show()
    }

    "return a function that goes to the task list page when connection to the property has been selected" in {
      navigator
        .nextPage(ConnectionToPropertyPageId, stillConnectedDetailsYesSession)
        .apply(stillConnectedDetailsYesSession) shouldBe controllers.routes.TaskListController
        .show()
    }

  }

  "return a function that goes from the vacant property page to vacant property Start Date page if answer yes" in {
    navigator
      .nextPage(VacantPropertiesPageId, stillConnectedDetailsYesToAllSession)
      .apply(
        stillConnectedDetailsYesToAllSession
      ) shouldBe controllers.connectiontoproperty.routes.VacantPropertiesStartDateController.show()
  }

  "return a function that goes from the vacant property page to vacant property Start Date page if answer no" in {
    navigator
      .nextPage(VacantPropertiesPageId, stillConnectedDetailsNoToAllSession)
      .apply(
        stillConnectedDetailsNoToAllSession
      ) shouldBe controllers.connectiontoproperty.routes.TradingNameOperatingFromPropertyController.show()
  }

  "return a function that goes from Letting Income page to vacant properties start date page if there is an income" in {
    navigator
      .nextPage(LettingIncomePageId, stillConnectedDetailsYesToAllSession)
      .apply(
        stillConnectedDetailsYesToAllSession
      ) shouldBe controllers.connectiontoproperty.routes.AddAnotherLettingPartOfPropertyController.show(0)
  }

  "return a function that goes from Letting Income page to provide contact details page" in {
    navigator
      .nextPage(LettingIncomePageId, stillConnectedDetailsNoToAllSession)
      .apply(
        stillConnectedDetailsNoToAllSession
      ) shouldBe controllers.connectiontoproperty.routes.ProvideContactDetailsController.show()
  }

  "return a function that goes from letting income  page to Letting Details page, when no details" in {
    val sessionWithEmptyLettingPartOfPropertyDetails = stillConnectedDetailsYesToAllSession.copy(
      stillConnectedDetails =
        Option(prefilledStillConnectedDetailsYesToAll.copy(lettingPartOfPropertyDetails = IndexedSeq()))
    )

    navigator
      .nextPage(LettingIncomePageId, sessionWithEmptyLettingPartOfPropertyDetails)
      .apply(
        sessionWithEmptyLettingPartOfPropertyDetails
      ) shouldBe controllers.connectiontoproperty.routes.LettingPartOfPropertyDetailsController.show()
  }

  "return a function that goes from trading name page to trading name own the property page" in {
    navigator
      .nextPage(TradingNameOperatingFromPropertyPageId, stillConnectedDetailsYesSession)
      .apply(
        stillConnectedDetailsYesSession
      ) shouldBe controllers.connectiontoproperty.routes.TradingNameOwnThePropertyController.show()
  }

  "return a function that goes from provide your details page to check your answers - vacant property page" in {
    navigator
      .nextPage(ProvideYourContactDetailsPageId, stillConnectedDetailsYesToAllSession)
      .apply(
        stillConnectedDetailsYesToAllSession
      ) shouldBe controllers.connectiontoproperty.routes.CheckYourAnswersConnectionToVacantPropertyController.show()
  }

  "return a function that goes from trading name paying rent page to are you third party page" in {
    navigator
      .nextPage(TradingNamePayingRentPageId, stillConnectedDetailsYesSession)
      .apply(
        stillConnectedDetailsYesSession
      ) shouldBe controllers.connectiontoproperty.routes.AreYouThirdPartyController.show()
  }

  "return a function that goes from letting part of the property details page to annual rent page" in {
    navigator
      .nextPage(LettingPartOfPropertyDetailsPageId, stillConnectedDetailsYesSession)
      .apply(
        stillConnectedDetailsYesSession
      ) shouldBe controllers.connectiontoproperty.routes.LettingPartOfPropertyDetailsRentController.show(0)
  }

  "return a function that goes from annual rent page to items included in rent page" in {
    navigator
      .nextPage(LettingPartOfPropertyRentDetailsPageId, stillConnectedDetailsYesSession)
      .apply(
        stillConnectedDetailsYesSession
      ) shouldBe controllers.connectiontoproperty.routes.LettingPartOfPropertyItemsIncludedInRentController.show(0)
  }

  "return a function that goes from items included in rent page to add another letting page" in {
    navigator
      .nextPage(LettingPartOfPropertyItemsIncludedInRentPageId, stillConnectedDetailsYesSession)
      .apply(
        stillConnectedDetailsYesSession
      ) shouldBe controllers.connectiontoproperty.routes.AddAnotherLettingPartOfPropertyController.show(0)
  }

  "return a function that goes from add another letting page to letting part of property details page" in {
    navigator
      .nextPage(AddAnotherLettingPartOfPropertyPageId, stillConnectedDetailsYesToAllSession)
      .apply(
        stillConnectedDetailsYesToAllSession
      ) shouldBe controllers.connectiontoproperty.routes.LettingPartOfPropertyDetailsController.show(Some(1))
  }

  "return a function that goes from CYA page to tasklist" in {
    navigator
      .nextPage(CheckYourAnswersAboutThePropertyPageId, stillConnectedDetailsYesSession)
      .apply(
        stillConnectedDetailsYesSession
      ) shouldBe controllers.routes.LoginController.show
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
      val result = navigator.getIncompleteSectionCall(detail, 0)
      result shouldBe routes.LettingPartOfPropertyDetailsController.show(Some(0))
    }

    "return the correct Call when lettingPartOfPropertyRentDetails is None" in {
      val detail = createLettingDetails(rentDetails = None)
      val result = navigator.getIncompleteSectionCall(detail, 1)
      result shouldBe routes.LettingPartOfPropertyDetailsRentController.show(1)
    }

    "return the correct Call when itemsIncludedInRent is empty" in {
      val detail = createLettingDetails(itemsIncluded = List.empty)
      val result = navigator.getIncompleteSectionCall(detail, 2)
      result shouldBe routes.LettingPartOfPropertyItemsIncludedInRentController.show(2)
    }

    "return the correct Call when all fields are populated" in {
      val detail = createLettingDetails()
      val result = navigator.getIncompleteSectionCall(detail, 3)
      result shouldBe routes.LettingPartOfPropertyDetailsController.show(Some(3))
    }
  }
// 6076

  "return a function that goes to the trading name page when still connected has been selected and the selection is yes" in {
    navigator
      .nextPage(AreYouStillConnectedPageId, stillConnectedDetails6076YesSession)
      .apply(stillConnectedDetails6076YesSession) shouldBe routes.TradingNameOperatingFromPropertyController
      .show()
  }

  "return a function that goes from edit the address to the trading name page " in {
    navigator
      .nextPage(EditAddressPageId, stillConnectedDetails6076YesSession)
      .apply(stillConnectedDetails6076YesSession) shouldBe routes.TradingNameOperatingFromPropertyController
      .show()
  }

  "redirect to LettingPartOfPropertyDetails when details are incomplete" in {
    val incompleteDetails = IndexedSeq(
      testlettingPartOfPropertyDetails.copy(tenantDetails = null)
    )
    val session           = stillConnectedDetailsYesToAllSession.copy(
      stillConnectedDetails = Some(
        prefilledStillConnectedDetailsYesToAll.copy(
          lettingPartOfPropertyDetails = incompleteDetails,
          isAnyRentReceived = Some(AnswerYes)
        )
      )
    )

    navigator
      .nextPage(LettingIncomePageId, session)
      .apply(session) shouldBe routes.LettingPartOfPropertyDetailsController.show(Some(0))
  }

  "redirect to ProvideContactDetails when no letting details" in {
    val session = stillConnectedDetailsYesToAllSession.copy(
      stillConnectedDetails = Some(
        prefilledStillConnectedDetailsYesToAll.copy(
          lettingPartOfPropertyDetails = IndexedSeq.empty,
          lettingPartOfPropertyDetailsIndex = 0
        )
      )
    )

    navigator
      .nextPage(AddAnotherLettingPartOfPropertyPageId, session)
      .apply(session) shouldBe routes.ProvideContactDetailsController.show()
  }

  "redirect to ProvideContactDetails when section is complete and addAnother is No" in {
    val completeDetails = IndexedSeq(
      testlettingPartOfPropertyDetails.copy(
        tenantDetails = testTenantDetails,
        lettingPartOfPropertyRentDetails = testLettingDetails,
        itemsIncludedInRent = List("test"),
        addAnotherLettingToProperty = Some(AnswerNo)
      )
    )

    val session = stillConnectedDetailsYesToAllSession.copy(
      stillConnectedDetails = Some(
        prefilledStillConnectedDetailsYesToAll.copy(
          lettingPartOfPropertyDetails = completeDetails,
          lettingPartOfPropertyDetailsIndex = 0,
          isAnyRentReceived = Some(AnswerYes)
        )
      )
    )

    navigator
      .nextPage(AddAnotherLettingPartOfPropertyPageId, session)
      .apply(session) shouldBe routes.ProvideContactDetailsController.show()
  }

  "redirect to LettingPartOfPropertyDetails page when section is complete and addAnother is Yes" in {
    val completeDetails = IndexedSeq(
      testlettingPartOfPropertyDetails.copy(addAnotherLettingToProperty = Some(AnswerYes))
    )
    val session         = stillConnectedDetailsYesToAllSession.copy(
      stillConnectedDetails = Some(
        prefilledStillConnectedDetailsYesToAll.copy(
          lettingPartOfPropertyDetails = completeDetails,
          lettingPartOfPropertyDetailsIndex = 0
        )
      )
    )

    navigator
      .nextPage(AddAnotherLettingPartOfPropertyPageId, session)
      .apply(session) shouldBe routes.LettingPartOfPropertyDetailsController.show(Some(0))
  }

}
