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
import navigation.identifiers._
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec

import scala.concurrent.ExecutionContext

class AboutYourLeaseOrTenure6010NavigatorSpec extends TestBaseSpec {

  val audit = mock[Audit]
  doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(any[HeaderCarrier], any[ExecutionContext])

  val navigator = new AboutYourLeaseOrTenureNavigator(audit)

  val session6010   = Session(
    "99996010004",
    "FOR6010",
    prefilledAddress,
    "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
    aboutLeaseOrAgreementPartOne = Some(prefilledAboutLeaseOrAgreement6010Route),
    aboutLeaseOrAgreementPartTwo = Some(prefilledAboutLeaseOrAgreementPartTwo)
  )
  val session6010No = Session(
    "99996010004",
    "FOR6010",
    prefilledAddress,
    "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
    aboutLeaseOrAgreementPartOne = Some(prefilledAboutLeaseOrAgreementPartOneNo),
    aboutLeaseOrAgreementPartTwo = Some(prefilledAboutLeaseOrAgreementPartTwoNo)
  )

  implicit override val hc: HeaderCarrier = HeaderCarrier()

  "About your lease or tenure navigator" when {

    "go to sign in from an identifier that doesn't exist in the route map" in {
      case object UnknownIdentifier extends Identifier
      navigator
        .nextPage(UnknownIdentifier, session6010)
        .apply(session6010) mustBe controllers.routes.LoginController.show()
    }

    "return a function that goes to lease or agreement years page when about your landlord has been completed" in {
      navigator
        .nextPage(AboutTheLandlordPageId, session6010)
        .apply(session6010) mustBe controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordController.show()
    }

    "return a function that goes to current annual rent page when lease or agreement details with yes has been completed" in {
      navigator
        .nextPage(LeaseOrAgreementDetailsPageId, session6010)
        .apply(session6010) mustBe controllers.aboutYourLeaseOrTenure.routes.PropertyUseLeasebackArrangementController
        .show()
    }

    "return a function that goes to current annual rent page when property use leaseback agreement with yes has been completed" in {
      navigator
        .nextPage(LeaseOrAgreementDetailsPageId, session6010)
        .apply(session6010) mustBe controllers.aboutYourLeaseOrTenure.routes.PropertyUseLeasebackArrangementController
        .show()
    }

    "return a function that goes to payable within 12 months page when lease or agreement details with no has been completed" in {
      navigator
        .nextPage(LeaseOrAgreementDetailsPageId, session6010No)
        .apply(
          session6010No
        ) mustBe controllers.aboutYourLeaseOrTenure.routes.CurrentRentPayableWithin12MonthsController.show()
    }

    "return a function that goes to task list page when current rent payable within 12 months has been completed" in {
      navigator
        .nextPage(CurrentRentPayableWithin12monthsPageId, session6010)
        .apply(
          session6010
        ) mustBe controllers.aboutYourLeaseOrTenure.routes.CheckYourAnswersAboutYourLeaseOrTenureController.show()
    }

    "return a function that goes to current rent first paid page when current annual rent has been completed" in {
      navigator
        .nextPage(CurrentAnnualRentPageId, session6010)
        .apply(session6010) mustBe controllers.aboutYourLeaseOrTenure.routes.CurrentRentFirstPaidController.show()
    }

    "return a function that goes to current lease or agreement begin page when current rent first paid has been completed" in {
      navigator
        .nextPage(CurrentRentFirstPaidPageId, session6010)
        .apply(session6010) mustBe controllers.aboutYourLeaseOrTenure.routes.CurrentLeaseOrAgreementBeginController
        .show()
    }

    "return a function that goes to included in your rent page when current lease or agreement begin has been completed" in {
      navigator
        .nextPage(CurrentLeaseBeginPageId, session6010)
        .apply(session6010) mustBe controllers.aboutYourLeaseOrTenure.routes.IncludedInYourRentController
        .show()
    }

    "return a function that goes to does rent payable page when included in your rent has been completed" in {
      navigator
        .nextPage(IncludedInYourRentPageId, session6010)
        .apply(session6010) mustBe controllers.aboutYourLeaseOrTenure.routes.DoesTheRentPayableController
        .show()
    }

    "return a function that goes to ultimately responsible page when does rent payable has been completed" in {
      navigator
        .nextPage(DoesRentPayablePageId, session6010)
        .apply(
          session6010
        ) mustBe controllers.aboutYourLeaseOrTenure.routes.UltimatelyResponsibleInsideRepairsController
        .show()
    }

    "return a function that goes to rent include trade services details page when rent include trade services with yes has been completed" in {
      navigator
        .nextPage(RentIncludeTradeServicesPageId, session6010)
        .apply(session6010) mustBe controllers.aboutYourLeaseOrTenure.routes.RentIncludeTradeServicesDetailsController
        .show()
    }

    "return a function that goes to fixture and fittings page when include trade services with no has been completed" in {
      navigator
        .nextPage(RentIncludeTradeServicesPageId, session6010)
        .apply(session6010No) mustBe controllers.aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsController
        .show()
    }

    "return a function that goes to fixture and fittings page when include trade services details has been completed" in {
      navigator
        .nextPage(RentIncludeTradeServicesDetailsPageId, session6010)
        .apply(session6010) mustBe controllers.aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsController
        .show()
    }

    "return a function that goes to fixture and fittings details page when fixture and fittings with yes has been completed" in {
      navigator
        .nextPage(RentFixtureAndFittingsPageId, session6010)
        .apply(
          session6010
        ) mustBe controllers.aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsDetailsController
        .show()
    }

    "return a function that goes to open market page when fixture and fittings with no has been completed" in {
      navigator
        .nextPage(RentFixtureAndFittingsPageId, session6010No)
        .apply(session6010No) mustBe controllers.aboutYourLeaseOrTenure.routes.RentOpenMarketValueController
        .show()
    }

    "return a function that goes to open market page when fixture and fittings details has been completed" in {
      navigator
        .nextPage(RentFixtureAndFittingsDetailsPageId, session6010)
        .apply(session6010) mustBe controllers.aboutYourLeaseOrTenure.routes.RentOpenMarketValueController
        .show()
    }

    "return a function that goes to increase by RPI page when open market rent with yes has been completed" in {
      navigator
        .nextPage(RentOpenMarketPageId, session6010)
        .apply(session6010) mustBe controllers.aboutYourLeaseOrTenure.routes.RentIncreaseAnnuallyWithRPIController
        .show()
    }

    "return a function that goes to what rent based on page when open market rent with no has been completed" in {
      navigator
        .nextPage(RentOpenMarketPageId, session6010No)
        .apply(session6010No) mustBe controllers.aboutYourLeaseOrTenure.routes.WhatIsYourRentBasedOnController
        .show()
    }

    "return a function that goes to increase by RPI when what rent based on has been completed" in {
      navigator
        .nextPage(WhatRentBasedOnPageId, session6010)
        .apply(
          session6010
        ) mustBe controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryAccordingToGrossOrNetController
        .show()
    }

    "return a function that goes to rent by gross or net turnover when increase by RPI has been completed" in {
      navigator
        .nextPage(RentIncreaseByRPIPageId, session6010)
        .apply(
          session6010
        ) mustBe controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryAccordingToGrossOrNetController
        .show()
    }

    "return a function that goes to gross or net turnover details page when rent by gross or net turnover with yes has been completed" in {
      navigator
        .nextPage(RentPayableVaryAccordingToGrossOrNetId, session6010)
        .apply(
          session6010
        ) mustBe controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryAccordingToGrossOrNetDetailsController
        .show()
    }

    "return a function that goes to rent vary on quantity of beer page when rent by gross or net turnover with no has been completed" in {
      navigator
        .nextPage(RentPayableVaryAccordingToGrossOrNetId, session6010No)
        .apply(
          session6010No
        ) mustBe controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryOnQuantityOfBeersController
        .show()
    }

    "return a function that goes to how is current rent fixed on page when rent by gross or net turnover details has been completed" in {
      navigator
        .nextPage(RentPayableVaryAccordingToGrossOrNetDetailsId, session6010)
        .apply(session6010) mustBe controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryOnQuantityOfBeersController
        .show()
    }

    "return a function that goes to rent vary quantity of beer details when rent vary quantity of beer with yes has been completed" in {
      navigator
        .nextPage(rentVaryQuantityOfBeersId, session6010)
        .apply(
          session6010
        ) mustBe controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryOnQuantityOfBeersDetailsController
        .show()
    }

    "return a function that goes to how is rent fixed page page when rent vary quantity of beer with no has been completed" in {
      navigator
        .nextPage(rentVaryQuantityOfBeersId, session6010No)
        .apply(session6010No) mustBe controllers.aboutYourLeaseOrTenure.routes.HowIsCurrentRentFixedController
        .show()
    }

    "return a function that goes to how is rent fixed page when rent vary quantity of beer has been completed" in {
      navigator
        .nextPage(rentVaryQuantityOfBeersDetailsId, session6010)
        .apply(session6010) mustBe controllers.aboutYourLeaseOrTenure.routes.HowIsCurrentRentFixedController
        .show()
    }

    "return a function that goes to method fix rent when how is rent fixed has been completed" in {
      navigator
        .nextPage(HowIsCurrentRentFixedId, session6010)
        .apply(session6010) mustBe controllers.aboutYourLeaseOrTenure.routes.MethodToFixCurrentRentController
        .show()
    }

    "return a function that goes to intervals rent review when method fix rent has been completed" in {
      navigator
        .nextPage(MethodToFixCurrentRentsId, session6010)
        .apply(session6010) mustBe controllers.aboutYourLeaseOrTenure.routes.IntervalsOfRentReviewController
        .show()
    }

    "return a function that goes to can rent be reduced when intervals rent review has been completed" in {
      navigator
        .nextPage(IntervalsOfRentReviewId, session6010)
        .apply(session6010) mustBe controllers.aboutYourLeaseOrTenure.routes.CanRentBeReducedOnReviewController
        .show()
    }

    "return a function that goes to incentives payment when can rent be reduced has been completed" in {
      navigator
        .nextPage(CanRentBeReducedOnReviewId, session6010)
        .apply(session6010) mustBe controllers.aboutYourLeaseOrTenure.routes.IncentivesPaymentsConditionsController
        .show()
    }

    "return a function that goes to tenants additional disregarded when incentives payment has been completed" in {
      navigator
        .nextPage(IncentivesPaymentsConditionsId, session6010)
        .apply(session6010) mustBe controllers.aboutYourLeaseOrTenure.routes.TenantsAdditionsDisregardedController
        .show()
    }

    "return a function that goes to tenants additional disregarded details page when tenants additional disregarded with yes has been completed" in {
      navigator
        .nextPage(TenantsAdditionsDisregardedId, session6010)
        .apply(
          session6010
        ) mustBe controllers.aboutYourLeaseOrTenure.routes.TenantsAdditionsDisregardedDetailsController
        .show()
    }

    "return a function that goes to pay a capital sum page when tenants additional disregarded with no has been completed" in {
      navigator
        .nextPage(TenantsAdditionsDisregardedId, session6010)
        .apply(session6010No) mustBe controllers.aboutYourLeaseOrTenure.routes.PayACapitalSumController
        .show()
    }

    "return a function that goes to pay a capital sum page when tenants additional disregarded details has been completed" in {
      navigator
        .nextPage(TenantsAdditionsDisregardedDetailsId, session6010)
        .apply(session6010) mustBe controllers.aboutYourLeaseOrTenure.routes.PayACapitalSumController.show()
    }

    "return a function that goes to pay when lease granted page when pay a capital sum has been completed" in {
      navigator
        .nextPage(PayCapitalSumId, session6010)
        .apply(session6010) mustBe controllers.aboutYourLeaseOrTenure.routes.PaymentWhenLeaseIsGrantedController.show()
    }

    "return a function that goes to legal or planning restrictions page when pay when lease granted has been completed" in {
      navigator
        .nextPage(PayWhenLeaseGrantedId, session6010)
        .apply(session6010) mustBe controllers.aboutYourLeaseOrTenure.routes.LegalOrPlanningRestrictionsController
        .show()
    }

    "return a function that goes to legal or planning restrictions details page when legal or planning restrictions with yes has been completed" in {
      navigator
        .nextPage(LegalOrPlanningRestrictionId, session6010)
        .apply(
          session6010
        ) mustBe controllers.aboutYourLeaseOrTenure.routes.LegalOrPlanningRestrictionsDetailsController.show()
    }

    "return a function that goes to CYA page when legal or planning restrictions with no has been completed" in {
      navigator
        .nextPage(LegalOrPlanningRestrictionId, session6010No)
        .apply(
          session6010No
        ) mustBe controllers.aboutYourLeaseOrTenure.routes.CheckYourAnswersAboutYourLeaseOrTenureController.show()
    }

    "return a function that goes to CYA page when legal or planning restrictions details has been completed" in {
      navigator
        .nextPage(LegalOrPlanningRestrictionDetailsId, session6010)
        .apply(
          session6010
        ) mustBe controllers.aboutYourLeaseOrTenure.routes.CheckYourAnswersAboutYourLeaseOrTenureController.show()
    }

    "return a function that goes to task list page when CYA has been completed" in {
      navigator
        .nextPage(CheckYourAnswersAboutYourLeaseOrTenureId, session6010)
        .apply(session6010) mustBe controllers.routes.TaskListController.show()
    }

  }
}
