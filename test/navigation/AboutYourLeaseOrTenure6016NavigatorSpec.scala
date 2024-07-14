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

package navigation

import connectors.Audit
import models.Session
import models.submissions.aboutYourLeaseOrTenure.{AboutLeaseOrAgreementPartOne, AboutLeaseOrAgreementPartTwo, CurrentRentBasedOnPercentageOpenMarket, CurrentRentFixedNewLeaseAgreement, HowIsCurrentRentFixed, MethodToFixCurrentRentDetails, MethodToFixCurrentRentsAgreement, WhatIsYourCurrentRentBasedOnDetails}
import models.submissions.common.{AnswerNo, AnswerYes}
import navigation.identifiers._
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec

import java.time.LocalDate
import scala.concurrent.ExecutionContext

class AboutYourLeaseOrTenure6016NavigatorSpec extends TestBaseSpec {

  val audit = mock[Audit]
  doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(any[HeaderCarrier], any[ExecutionContext])

  val navigator = new AboutYourLeaseOrTenureNavigator(audit)

  val session6016 = Session("99996016004", "FOR6016", prefilledAddress, "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=")

  implicit override val hc: HeaderCarrier = HeaderCarrier()

  "About your lease or tenure navigator" when {

    "go to sign in from an identifier that doesn't exist in the route map" in {
      case object UnknownIdentifier extends Identifier
      navigator.nextPage(UnknownIdentifier, session6016).apply(session6016) mustBe controllers.routes.LoginController
        .show
    }

    "return a function that goes to connected to landlord page when about your landlord has been completed" in {
      navigator
        .nextPage(AboutTheLandlordPageId, session6016)
        .apply(session6016) mustBe controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordController.show()
    }

    "return a function that goes to connected to landlord details page when connected to landlord and answer is 'yes'" in {
      val answers = session6016.copy(
        aboutLeaseOrAgreementPartOne = Some(AboutLeaseOrAgreementPartOne(connectedToLandlord = Some(AnswerYes)))
      )
      val result  = navigator.connectedToLandlordRouting(answers)
      result mustBe controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordDetailsController.show()

    }

    "return a function that goes to connected to landlord details page when connected to landlord and answer is 'no'" in {
      val answers = session6016.copy(
        aboutLeaseOrAgreementPartOne = Some(AboutLeaseOrAgreementPartOne(connectedToLandlord = Some(AnswerNo)))
      )
      val result  = navigator.connectedToLandlordRouting(answers)
      result mustBe controllers.aboutYourLeaseOrTenure.routes.LeaseOrAgreementYearsController.show()

    }

    "return a function that goes to current rent payable within 12 months page when cya has been completed" in {
      navigator
        .nextPage(CurrentRentPayableWithin12monthsPageId, session6016)
        .apply(
          session6016
        ) mustBe controllers.aboutYourLeaseOrTenure.routes.CheckYourAnswersAboutYourLeaseOrTenureController.show()
    }

    "return a function that goes to current annual rent agreement page when current rent first paid has been completed" in {
      navigator
        .nextPage(CurrentAnnualRentPageId, session6016)
        .apply(
          session6016
        ) mustBe controllers.aboutYourLeaseOrTenure.routes.CurrentRentFirstPaidController.show()
    }

    "return a function that goes to lease length and start date page when current rent first paid has been completed" in {
      navigator
        .nextPage(CurrentRentFirstPaidPageId, session6016)
        .apply(
          session6016
        ) mustBe controllers.aboutYourLeaseOrTenure.routes.CurrentLeaseOrAgreementBeginController.show()
    }

    "return a function that goes to included in your rent page when current lease begin has been completed" in {
      navigator
        .nextPage(CurrentLeaseBeginPageId, session6016)
        .apply(
          session6016
        ) mustBe controllers.aboutYourLeaseOrTenure.routes.IncludedInYourRentController.show()
    }

    "return a function that goes to does the rent payable page when included in your rent has been completed" in {
      navigator
        .nextPage(IncludedInYourRentPageId, session6016)
        .apply(
          session6016
        ) mustBe controllers.aboutYourLeaseOrTenure.routes.DoesTheRentPayableController.show()
    }

    "return a function that goes to ultimately responsible OR page when does the rent payable has been completed" in {
      navigator
        .nextPage(DoesRentPayablePageId, session6016)
        .apply(
          session6016
        ) mustBe controllers.aboutYourLeaseOrTenure.routes.UltimatelyResponsibleOutsideRepairsController.show()
    }

    "return a function that goes to ultimately responsible IR page when ultimately responsible OR has been completed" in {
      navigator
        .nextPage(UltimatelyResponsibleOutsideRepairsPageId, session6016)
        .apply(
          session6016
        ) mustBe controllers.aboutYourLeaseOrTenure.routes.UltimatelyResponsibleInsideRepairsController.show()
    }

    "return a function that goes to ultimately responsible BI page when ultimately responsible IR has been completed" in {
      navigator
        .nextPage(UltimatelyResponsibleInsideRepairsPageId, session6016)
        .apply(
          session6016
        ) mustBe controllers.aboutYourLeaseOrTenure.routes.UltimatelyResponsibleBuildingInsuranceController.show()
    }

    "return a function that goes to trade services page when ultimately responsible BI has been completed" in {
      navigator
        .nextPage(UltimatelyResponsibleBusinessInsurancePageId, session6016)
        .apply(
          session6016
        ) mustBe controllers.aboutYourLeaseOrTenure.routes.RentIncludeTradeServicesController.show()
    }

    "return a function that goes to rent include fixture and fittings when trade services details has been completed" in {
      navigator
        .nextPage(RentFixtureAndFittingsDetailsPageId, session6016)
        .apply(
          session6016
        ) mustBe controllers.aboutYourLeaseOrTenure.routes.RentOpenMarketValueController.show()
    }

    "return a function that goes to how is current rent fixed when RentPayableVaryAccordingToGrossOrNetDetails has been completed" in {
      navigator
        .nextPage(RentPayableVaryAccordingToGrossOrNetDetailsId, session6016)
        .apply(
          session6016
        ) mustBe controllers.aboutYourLeaseOrTenure.routes.HowIsCurrentRentFixedController.show()
    }

    "return a function that goes to can rent be reduced on view when intervals of rent review has been completed" in {
      navigator
        .nextPage(IntervalsOfRentReviewId, session6016)
        .apply(
          session6016
        ) mustBe controllers.aboutYourLeaseOrTenure.routes.CanRentBeReducedOnReviewController.show()
    }

    "return a function that goes to capital sum when tenants additions has been completed" in {
      navigator
        .nextPage(TenantsAdditionsDisregardedDetailsId, session6016)
        .apply(
          session6016
        ) mustBe controllers.aboutYourLeaseOrTenure.routes.PayACapitalSumController.show()
    }
    "return a function that goes to how is current rent fixed when What is your current rent has been completed" in {

      val session = session6016.copy(
        aboutLeaseOrAgreementPartOne = Some(
          session6016.aboutLeaseOrAgreementPartOne.getOrElse(
            AboutLeaseOrAgreementPartOne(whatIsYourCurrentRentBasedOnDetails =
              Some(WhatIsYourCurrentRentBasedOnDetails(CurrentRentBasedOnPercentageOpenMarket, Some("test")))
            )
          )
        )
      )
      navigator
        .nextPage(WhatRentBasedOnPageId, session)
        .apply(
          session
        ) mustBe controllers.aboutYourLeaseOrTenure.routes.RentIncreaseAnnuallyWithRPIController
        .show()
    }

    "return a function that goes to method to fix current rent page when how is current rent fixed has been completed" in {

      val session = session6016.copy(
        aboutLeaseOrAgreementPartTwo = Some(
          session6016.aboutLeaseOrAgreementPartTwo.getOrElse(
            AboutLeaseOrAgreementPartTwo(howIsCurrentRentFixed =
              Some(HowIsCurrentRentFixed(CurrentRentFixedNewLeaseAgreement, LocalDate.of(2000, 2, 1)))
            )
          )
        )
      )
      navigator
        .nextPage(HowIsCurrentRentFixedId, session)
        .apply(
          session
        ) mustBe controllers.aboutYourLeaseOrTenure.routes.MethodToFixCurrentRentController
        .show()
    }

    "return a function that goes to intervals of rent page when method to fix current rent has been completed" in {

      val session = session6016.copy(
        aboutLeaseOrAgreementPartTwo = Some(
          session6016.aboutLeaseOrAgreementPartTwo.getOrElse(
            AboutLeaseOrAgreementPartTwo(methodToFixCurrentRentDetails =
              Some(MethodToFixCurrentRentDetails(MethodToFixCurrentRentsAgreement))
            )
          )
        )
      )
      navigator
        .nextPage(MethodToFixCurrentRentsId, session)
        .apply(
          session
        ) mustBe controllers.aboutYourLeaseOrTenure.routes.IntervalsOfRentReviewController
        .show()
    }

    "return a function that goes to CYA when legal or planning restrictions has been completed" in {
      navigator
        .nextPage(LegalOrPlanningRestrictionDetailsId, session6016)
        .apply(
          session6016
        ) mustBe controllers.aboutYourLeaseOrTenure.routes.CheckYourAnswersAboutYourLeaseOrTenureController.show()
    }
  }
}
