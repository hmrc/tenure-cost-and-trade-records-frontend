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

import models.ForType.*
import models.Session
import models.submissions.aboutYourLeaseOrTenure.*
import models.submissions.aboutYourLeaseOrTenure.CurrentRentFixed.*
import models.submissions.aboutYourLeaseOrTenure.MethodToFixCurrentRent.*
import models.submissions.common.ResponsibilityParty.BuildingInsurance.*
import models.submissions.common.ResponsibilityParty.InsideRepairs.*
import models.submissions.common.ResponsibilityParty.OutsideRepairs.*
import navigation.identifiers.*
import test.{InjectedNavigation, TCTRAppSpec}

import java.time.LocalDate
import scala.language.implicitConversions

class AboutYourLeaseOrTenure6010NavigatorSpec extends TCTRAppSpec with InjectedNavigation:

  private val session6010 = Session(
    "99996010004",
    FOR6010,
    prefilledAddress,
    "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
    isWelsh = false,
    aboutLeaseOrAgreementPartOne = prefilledAboutLeaseOrAgreement6010Route,
    aboutLeaseOrAgreementPartTwo = prefilledAboutLeaseOrAgreementPartTwo
  )

  private val session6010No: Session = session6010.copy(
    aboutLeaseOrAgreementPartOne = prefilledAboutLeaseOrAgreementPartOneNo,
    aboutLeaseOrAgreementPartTwo = prefilledAboutLeaseOrAgreementPartTwoNo
  )

  private val session6010NoLeaseback: Session = session6010.copy(
    aboutLeaseOrAgreementPartOne = prefilledAboutLeaseOrAgreementPartOneNo,
    aboutLeaseOrAgreementPartTwo = prefilledAboutLeaseOrAgreementPartTwoNo
  )

  private val session6010PayNavigation: Session = session6010.copy(
    aboutLeaseOrAgreementPartOne = prefilledAboutLeaseOrAgreementPartOneNo,
    aboutLeaseOrAgreementPartTwo = prefilledAboutLeaseOrAgreementPayPartTwo
  )

  "Lease or agreement navigator for 6010" should {
    "redirect to default page for identifier that doesn't exist in the route map" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(UnknownIdentifier, session6010)
        .apply(session6010) shouldBe controllers.routes.LoginController.show
    }

    "redirect to lease or agreement years page when about your landlord has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(AboutTheLandlordPageId, session6010)
        .apply(session6010) shouldBe controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordController.show()
    }

    "redirect to current annual rent page when lease or agreement details with yes has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(LeaseOrAgreementDetailsPageId, session6010)
        .apply(session6010) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.PropertyUseLeasebackArrangementController
          .show()
    }

    "redirect to current annual rent page when property use leaseback agreement with yes has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(LeaseOrAgreementDetailsPageId, session6010)
        .apply(session6010) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.PropertyUseLeasebackArrangementController
          .show()
    }

    "redirect to payable within 12 months page when lease or agreement details with no has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(LeaseOrAgreementDetailsPageId, session6010No)
        .apply(
          session6010No
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.CurrentRentPayableWithin12MonthsController.show()
    }

    "redirect to task list page when current rent payable within 12 months has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(CurrentRentPayableWithin12monthsPageId, session6010)
        .apply(
          session6010
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.CheckYourAnswersAboutYourLeaseOrTenureController.show()
    }

    "redirect to current rent first paid page when current annual rent has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(CurrentAnnualRentPageId, session6010)
        .apply(session6010) shouldBe controllers.aboutYourLeaseOrTenure.routes.CurrentRentFirstPaidController.show()
    }

    "redirect to current lease or agreement begin page when current rent first paid has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(CurrentRentFirstPaidPageId, session6010)
        .apply(session6010) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.CurrentLeaseOrAgreementBeginController
          .show()
    }

    "redirect to included in your rent page when current lease or agreement begin has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(CurrentLeaseBeginPageId, session6010)
        .apply(session6010) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.IncludedInYourRentController
          .show()
    }

    "redirect to does rent payable page when included in your rent has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(IncludedInYourRentPageId, session6010)
        .apply(session6010) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.DoesTheRentPayableController
          .show()
    }

    "redirect to Ultimately responsible BI page when Ultimately Responsible OR has been completed" in {

      val session = session6010.copy(
        aboutLeaseOrAgreementPartTwo =
          AboutLeaseOrAgreementPartTwo(ultimatelyResponsibleOutsideRepairs =
            UltimatelyResponsibleOutsideRepairs(OutsideRepairsLandlord, "test")
          )
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(UltimatelyResponsibleOutsideRepairsPageId, session)
        .apply(
          session
        ) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.UltimatelyResponsibleInsideRepairsController
          .show()
    }

    "redirect to Ultimately responsible IR page when Ultimately Responsible OR has been completed" in {
      val session = session6010.copy(
        aboutLeaseOrAgreementPartTwo =
          AboutLeaseOrAgreementPartTwo(
            ultimatelyResponsibleInsideRepairs =
              UltimatelyResponsibleInsideRepairs(InsideRepairsLandlord, "test")
          )
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(UltimatelyResponsibleInsideRepairsPageId, session)
        .apply(
          session
        ) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.UltimatelyResponsibleBuildingInsuranceController
          .show()
    }

    "redirect to rent include trade services page when Ultimately Responsible BI has been completed" in {
      val session = session6010.copy(
        aboutLeaseOrAgreementPartTwo =
          AboutLeaseOrAgreementPartTwo(
            ultimatelyResponsibleBuildingInsurance =
              UltimatelyResponsibleBuildingInsurance(BuildingInsuranceLandlord, "test")
          )
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(UltimatelyResponsibleBusinessInsurancePageId, session)
        .apply(
          session
        ) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.RentIncludeTradeServicesController
          .show()
    }

    "redirect to 'does the rent payable vary(...)', when 'what is the rent based on(...)' has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(WhatRentBasedOnPageId, session6010)
        .apply(
          session6010
        ) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryAccordingToGrossOrNetController
          .show()
    }

    "redirect to method to fix current rent page when how is current rent fixed has been completed" in {
      val session = session6010.copy(
        aboutLeaseOrAgreementPartTwo =
          AboutLeaseOrAgreementPartTwo(howIsCurrentRentFixed =
            HowIsCurrentRentFixed(CurrentRentFixedNewLeaseAgreement, LocalDate.of(2000, 2, 1))
          )
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(HowIsCurrentRentFixedId, session)
        .apply(
          session
        ) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.MethodToFixCurrentRentController
          .show()
    }

    "redirect to intervals of rent page when method to fix current rent has been completed" in {
      val session = session6010.copy(
        aboutLeaseOrAgreementPartTwo =
          AboutLeaseOrAgreementPartTwo(methodToFixCurrentRentDetails = MethodToFixCurrentRentAgreement)
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(MethodToFixCurrentRentsId, session)
        .apply(
          session
        ) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.IsRentReviewPlannedController
          .show()
    }

    "redirect to rent include trade services details page when rent include trade services with yes has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(RentIncludeTradeServicesPageId, session6010)
        .apply(session6010) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.RentIncludeTradeServicesDetailsController
          .show()
    }

    "redirect to fixture and fittings page when include trade services with no has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(RentIncludeTradeServicesPageId, session6010)
        .apply(session6010No) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsController
          .show()
    }

    "redirect to fixture and fittings page when include trade services details has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(RentIncludeTradeServicesDetailsPageId, session6010)
        .apply(session6010) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsController
          .show()
    }

    "redirect to fixture and fittings details page when fixture and fittings with yes has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(RentFixtureAndFittingsPageId, session6010)
        .apply(
          session6010
        ) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsDetailsController
          .show()
    }

    "redirect to open market page when fixture and fittings with no has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(RentFixtureAndFittingsPageId, session6010No)
        .apply(session6010No) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.RentOpenMarketValueController
          .show()
    }

    "redirect to open market page when fixture and fittings details has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(RentFixtureAndFittingsDetailsPageId, session6010)
        .apply(session6010) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.RentOpenMarketValueController
          .show()
    }

    "redirect to increase by RPI page when open market rent with yes has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(RentOpenMarketPageId, session6010)
        .apply(session6010) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.RentIncreaseAnnuallyWithRPIController
          .show()
    }

    "redirect to what rent based on page when open market rent with no has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(RentOpenMarketPageId, session6010No)
        .apply(session6010No) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.WhatIsYourRentBasedOnController
          .show()
    }

    "redirect to rent by gross or net turnover when increase by RPI has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(RentIncreaseByRPIPageId, session6010)
        .apply(
          session6010
        ) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryAccordingToGrossOrNetController
          .show()
    }

    "redirect to gross or net turnover details page when rent by gross or net turnover with yes has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(RentPayableVaryAccordingToGrossOrNetId, session6010)
        .apply(
          session6010
        ) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryAccordingToGrossOrNetDetailsController
          .show()
    }

    "redirect to rent vary on quantity of beer page when rent by gross or net turnover with no has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(RentPayableVaryAccordingToGrossOrNetId, session6010No)
        .apply(
          session6010No
        ) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryOnQuantityOfBeersController
          .show()
    }

    "redirect to how is current rent fixed on page when rent by gross or net turnover details has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(RentPayableVaryAccordingToGrossOrNetDetailsId, session6010)
        .apply(
          session6010
        ) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryOnQuantityOfBeersController
          .show()
    }

    "redirect to rent vary quantity of beer details when rent vary quantity of beer with yes has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(RentVaryQuantityOfBeersId, session6010)
        .apply(
          session6010
        ) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryOnQuantityOfBeersDetailsController
          .show()
    }

    "redirect to how is rent fixed page page when rent vary quantity of beer with no has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(RentVaryQuantityOfBeersId, session6010No)
        .apply(session6010No) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.HowIsCurrentRentFixedController
          .show()
    }

    "redirect to how is rent fixed page when rent vary quantity of beer has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(RentVaryQuantityOfBeersDetailsId, session6010)
        .apply(session6010) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.HowIsCurrentRentFixedController
          .show()
    }

    "redirect to method fix rent when how is rent fixed has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(HowIsCurrentRentFixedId, session6010)
        .apply(session6010) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.MethodToFixCurrentRentController
          .show()
    }

    "redirect to intervals rent review when method fix rent has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(MethodToFixCurrentRentsId, session6010)
        .apply(session6010) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.IsRentReviewPlannedController
          .show()
    }

    "redirect to can rent be reduced when intervals rent review has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(IntervalsOfRentReviewId, session6010)
        .apply(session6010) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.CanRentBeReducedOnReviewController
          .show()
    }

    "redirect to incentives payment when can rent be reduced has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(CanRentBeReducedOnReviewId, session6010)
        .apply(session6010) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.IncentivesPaymentsConditionsController
          .show()
    }

    "redirect to tenants additional disregarded when incentives payment has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(IncentivesPaymentsConditionsId, session6010)
        .apply(session6010) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.TenantsAdditionsDisregardedController
          .show()
    }

    "redirect to tenants additional disregarded details page when tenants additional disregarded with yes has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(TenantsAdditionsDisregardedId, session6010)
        .apply(
          session6010
        ) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.TenantsAdditionsDisregardedDetailsController
          .show()
    }

    "redirect to pay a capital sum page when tenants additional disregarded with no has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(TenantsAdditionsDisregardedId, session6010)
        .apply(session6010No) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.PayACapitalSumController
          .show()
    }

    "redirect to pay a capital sum page when tenants additional disregarded details has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(TenantsAdditionsDisregardedDetailsId, session6010)
        .apply(session6010) shouldBe controllers.aboutYourLeaseOrTenure.routes.PayACapitalSumController.show()
    }

    "redirect to pay when lease granted page when pay a capital sum has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(PayCapitalSumId, session6010PayNavigation)
        .apply(
          session6010PayNavigation
        ) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.PaymentWhenLeaseIsGrantedController
          .show()
    }

    "redirect to legal or planning restrictions page when pay when lease granted has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(PayWhenLeaseGrantedId, session6010)
        .apply(session6010) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.LegalOrPlanningRestrictionsController
          .show()
    }

    "redirect to legal or planning restrictions details page when legal or planning restrictions with yes has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(LegalOrPlanningRestrictionId, session6010)
        .apply(
          session6010
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.LegalOrPlanningRestrictionsDetailsController.show()
    }

    "redirect to CurrentAnnualRent when PropertyUseLeasebackAgreement has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(PropertyUseLeasebackAgreementId, session6010NoLeaseback)
        .apply(
          session6010NoLeaseback
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.CurrentAnnualRentController.show()
    }

    "redirect to CYA page when legal or planning restrictions with no has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(LegalOrPlanningRestrictionId, session6010No)
        .apply(
          session6010No
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.CheckYourAnswersAboutYourLeaseOrTenureController.show()
    }

    "redirect to CYA page when legal or planning restrictions details has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(LegalOrPlanningRestrictionDetailsId, session6010)
        .apply(
          session6010
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.CheckYourAnswersAboutYourLeaseOrTenureController.show()
    }

    "redirect to task list page when CYA has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(CheckYourAnswersAboutYourLeaseOrTenureId, session6010)
        .apply(session6010) shouldBe controllers.routes.TaskListController.show.withFragment("leaseOrAgreement")
    }

    "redirect to does rent include fixture and fittings  when payment for trade service has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(PaymentForTradeServicesId, session6010)
        .apply(session6010) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsController
          .show()
    }

    "redirect to is parking rent paid separately  when does rent include parking has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(DoesRentIncludeParkingId, session6010)
        .apply(session6010) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.IsParkingRentPaidSeparatelyController
          .show()
    }

    "redirect to does rent include fixture and fittings  when is parking rent paid separately has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(IsParkingRentPaidSeparatelyId, session6010)
        .apply(session6010) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsController
          .show()
    }

    "redirect to PayACapitalSum  when IsGivenRentFree has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(IsGivenRentFreePeriodId, session6010No)
        .apply(session6010No) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.PayACapitalSumController
          .show()
    }

    "redirect to TenantsAdditionsDisregarded page when IncentivesPaymentsCondition has been completed " in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(IncentivesPaymentsConditionsId, session6010)
        .apply(session6010) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.TenantsAdditionsDisregardedController
          .show()
    }
  }
