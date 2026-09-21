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
import models.submissions.common.AnswersYesNo.*
import navigation.identifiers.*
import test.{InjectedNavigation, TCTRAppSpec}

import java.time.LocalDate
import scala.language.implicitConversions

class AboutYourLeaseOrTenure6016NavigatorSpec extends TCTRAppSpec with InjectedNavigation:

  private val session6016 = Session("99996016004", FOR6016, prefilledAddress, "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=", isWelsh = false)

  "Lease or agreement navigator for 6016" should {
    "redirect to default page for identifier that doesn't exist in the route map" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(UnknownIdentifier, session6016)
        .apply(session6016) shouldBe controllers.routes.LoginController.show
    }

    "redirect to connected to landlord details page when connected to landlord and answer is 'yes'" in {
      val answers = session6016.copy(
        aboutLeaseOrAgreementPartOne = AboutLeaseOrAgreementPartOne(connectedToLandlord = AnswerYes)
      )
      val result  = aboutYourLeaseOrTenureNavigator.connectedToLandlordRouting(answers)
      result shouldBe controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordDetailsController.show()
    }

    "redirect to connected to landlord details page when connected to landlord and answer is 'no'" in {
      val answers = session6016.copy(
        aboutLeaseOrAgreementPartOne = AboutLeaseOrAgreementPartOne(connectedToLandlord = AnswerNo)
      )
      val result  = aboutYourLeaseOrTenureNavigator.connectedToLandlordRouting(answers)
      result shouldBe controllers.aboutYourLeaseOrTenure.routes.LeaseOrAgreementYearsController.show()
    }

    "redirect to current annual rent agreement page when current rent first paid has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(CurrentAnnualRentPageId, session6016)
        .apply(
          session6016
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.CurrentRentFirstPaidController.show()
    }

    "redirect to lease length and start date page when current rent first paid has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(CurrentRentFirstPaidPageId, session6016)
        .apply(
          session6016
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.CurrentLeaseOrAgreementBeginController.show()
    }

    "redirect to included in your rent page when current lease begin has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(CurrentLeaseBeginPageId, session6016)
        .apply(
          session6016
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.IncludedInYourRentController.show()
    }

    "redirect to does the rent payable page when included in your rent has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(IncludedInYourRentPageId, session6016)
        .apply(
          session6016
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.DoesTheRentPayableController.show()
    }

    "redirect to ultimately responsible OR page when does the rent payable has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(DoesRentPayablePageId, session6016)
        .apply(
          session6016
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.UltimatelyResponsibleOutsideRepairsController.show()
    }

    "redirect to ultimately responsible IR page when ultimately responsible OR has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(UltimatelyResponsibleOutsideRepairsPageId, session6016)
        .apply(
          session6016
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.UltimatelyResponsibleInsideRepairsController.show()
    }

    "redirect to ultimately responsible BI page when ultimately responsible IR has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(UltimatelyResponsibleInsideRepairsPageId, session6016)
        .apply(
          session6016
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.UltimatelyResponsibleBuildingInsuranceController.show()
    }

    "redirect to trade services page when ultimately responsible BI has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(UltimatelyResponsibleBusinessInsurancePageId, session6016)
        .apply(
          session6016
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.RentIncludeTradeServicesController.show()
    }

    "redirect to rent include fixture and fittings when trade services details has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(RentFixtureAndFittingsDetailsPageId, session6016)
        .apply(
          session6016
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.RentOpenMarketValueController.show()
    }

    "redirect to how is current rent fixed when RentPayableVaryAccordingToGrossOrNetDetails has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(RentPayableVaryAccordingToGrossOrNetDetailsId, session6016)
        .apply(
          session6016
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryOnQuantityOfBeersController.show()
    }

    "redirect to can rent be reduced on view when intervals of rent review has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(IntervalsOfRentReviewId, session6016)
        .apply(
          session6016
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.CanRentBeReducedOnReviewController.show()
    }

    "redirect to capital sum when tenants additions has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(TenantsAdditionsDisregardedDetailsId, session6016)
        .apply(
          session6016
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.PayACapitalSumController.show()
    }
    "redirect to 'does the rent payable vary(...)', when' what is the rent based on (...)' has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(WhatRentBasedOnPageId, session6016)
        .apply(
          session6016
        ) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryAccordingToGrossOrNetController
          .show()
    }

    "redirect to method to fix current rent page when how is current rent fixed has been completed" in {
      val session = session6016.copy(
        aboutLeaseOrAgreementPartTwo =
          session6016.aboutLeaseOrAgreementPartTwo.getOrElse(
            AboutLeaseOrAgreementPartTwo(howIsCurrentRentFixed =
              HowIsCurrentRentFixed(CurrentRentFixedNewLeaseAgreement, LocalDate.of(2000, 2, 1))
            )
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
      val session = session6016.copy(
        aboutLeaseOrAgreementPartTwo =
          session6016.aboutLeaseOrAgreementPartTwo.getOrElse(
            AboutLeaseOrAgreementPartTwo(methodToFixCurrentRentDetails = MethodToFixCurrentRentAgreement)
          )
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(MethodToFixCurrentRentsId, session)
        .apply(
          session
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.IsRentReviewPlannedController.show()
    }

    "redirect to CYA when legal or planning restrictions has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(LegalOrPlanningRestrictionDetailsId, session6016)
        .apply(
          session6016
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.CheckYourAnswersAboutYourLeaseOrTenureController.show()
    }
  }
