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
import models.submissions.aboutYourLeaseOrTenure.IncludedInYourRentInformation.*
import models.submissions.aboutYourLeaseOrTenure.MethodToFixCurrentRent.*
import models.submissions.common.AnswersYesNo.*
import models.submissions.common.ResponsibilityParty.BuildingInsurance.*
import models.submissions.common.ResponsibilityParty.OutsideRepairs.*
import navigation.identifiers.*
import test.{InjectedNavigation, TCTRAppSpec}

import java.time.LocalDate
import scala.language.implicitConversions

class AboutYourLeaseOrTenure6020NavigatorSpec extends TCTRAppSpec with InjectedNavigation:

  private val session6020 = Session("99996020004", FOR6020, prefilledAddress, "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=", isWelsh = false)

  "Lease or agreement navigator for 6020" should {
    "redirect to default page for identifier that doesn't exist in the route map" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(UnknownIdentifier, session6020)
        .apply(session6020) shouldBe controllers.routes.LoginController.show
    }

    "redirect to lease surrendered early page when disregarded addition has been completed no" in {
      val session = session6020.copy(
        aboutLeaseOrAgreementPartTwo =
          session6020.aboutLeaseOrAgreementPartTwo.getOrElse(
            AboutLeaseOrAgreementPartTwo(tenantAdditionsDisregarded = AnswerNo)
          )
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(TenantsAdditionsDisregardedId, session)
        .apply(session) shouldBe controllers.aboutYourLeaseOrTenure.routes.LeaseSurrenderedEarlyController.show()
    }

    "redirect to throughput page when disregarded addition details has been completed " in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(CurrentAnnualRentPageId, session6020)
        .apply(session6020) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.ThroughputAffectsRentController
          .show()
    }

    "redirect to lease surrendered early page when disregarded addition details has been completed " in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(TenantsAdditionsDisregardedDetailsId, session6020)
        .apply(session6020) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.LeaseSurrenderedEarlyController
          .show()
    }

    "redirect to benefits given page when lease surrendered has been completed " in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(LeaseSurrenderedEarlyId, session6020)
        .apply(session6020) shouldBe controllers.aboutYourLeaseOrTenure.routes.BenefitsGivenController.show()
    }

    "redirect to connected to landlord details page when connected to landlord and answer is 'no'" in {
      val answers = session6020.copy(
        aboutLeaseOrAgreementPartOne = AboutLeaseOrAgreementPartOne(connectedToLandlord = AnswerNo)
      )
      val result  = aboutYourLeaseOrTenureNavigator.connectedToLandlordRouting(answers)
      result shouldBe controllers.aboutYourLeaseOrTenure.routes.PropertyUseLeasebackArrangementController.show()
    }

    "redirect to trade services description page when include trade services with yes has been completed" in {
      val session = session6020.copy(
        aboutLeaseOrAgreementPartOne =
          session6020.aboutLeaseOrAgreementPartOne.getOrElse(
            AboutLeaseOrAgreementPartOne(rentIncludeTradeServicesDetails = AnswerYes)
          )
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(RentIncludeTradeServicesPageId, session)
        .apply(session) shouldBe controllers.aboutYourLeaseOrTenure.routes.TradeServicesDescriptionController.show()
    }

    "redirect to trade services description page when include trade services with no has been completed" in {
      val session = session6020.copy(
        aboutLeaseOrAgreementPartOne =
          session6020.aboutLeaseOrAgreementPartOne.getOrElse(
            AboutLeaseOrAgreementPartOne(rentIncludeTradeServicesDetails = AnswerNo)
          )
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(RentIncludeTradeServicesPageId, session)
        .apply(session) shouldBe controllers.aboutYourLeaseOrTenure.routes.PaymentForTradeServicesController.show()
    }

    "redirect to benefits given detail page when benefits given has been completed for yes answer " in {
      val session = session6020.copy(
        aboutLeaseOrAgreementPartThree =
          session6020.aboutLeaseOrAgreementPartThree.getOrElse(
            AboutLeaseOrAgreementPartThree(benefitsGiven = AnswerYes)
          )
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(BenefitsGivenId, session)
        .apply(session) shouldBe controllers.aboutYourLeaseOrTenure.routes.BenefitsGivenDetailsController.show()
    }

    "redirect to benefits given detail page when benefits given has been completed for no answer " in {
      val session = session6020.copy(
        aboutLeaseOrAgreementPartThree =
          session6020.aboutLeaseOrAgreementPartThree.getOrElse(
            AboutLeaseOrAgreementPartThree(benefitsGiven = AnswerNo)
          )
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(BenefitsGivenId, session)
        .apply(session) shouldBe controllers.aboutYourLeaseOrTenure.routes.PayACapitalSumController.show()
    }

    "redirect to capital sum description page when pay a capital sum has been completed for yes answer " in {
      val session = session6020.copy(
        aboutLeaseOrAgreementPartTwo =
          session6020.aboutLeaseOrAgreementPartTwo.getOrElse(
            AboutLeaseOrAgreementPartTwo(payACapitalSumOrPremium = AnswerYes)
          )
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(PayCapitalSumId, session)
        .apply(session) shouldBe controllers.aboutYourLeaseOrTenure.routes.CapitalSumDescriptionController.show()
    }

    "redirect to throughput affects rent details page when throughput affects rent answer " in {
      val session = session6020.copy(
        aboutLeaseOrAgreementPartThree =
          session6020.aboutLeaseOrAgreementPartThree.getOrElse(
            AboutLeaseOrAgreementPartThree(throughputAffectsRent = ThroughputAffectsRent(AnswerNo, "text"))
          )
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(ThroughputAffectsRentId, session)
        .apply(session) shouldBe controllers.aboutYourLeaseOrTenure.routes.CurrentRentFirstPaidController.show()
    }

    "redirect to other factors affecting the rent page when capital sum has been completed for no answer " in {
      val session = session6020.copy(
        aboutLeaseOrAgreementPartTwo =
          session6020.aboutLeaseOrAgreementPartTwo.getOrElse(
            AboutLeaseOrAgreementPartTwo(payACapitalSumOrPremium = AnswerNo)
          )
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(PayCapitalSumId, session)
        .apply(session) shouldBe controllers.aboutYourLeaseOrTenure.routes.LegalOrPlanningRestrictionsController.show()
    }

    "redirect to other factors affecting the rent page when pay a capital sum description has been completed " in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(CapitalSumDescriptionId, session6020)
        .apply(session6020) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.LegalOrPlanningRestrictionsController
          .show()
    }

    "redirect to lease or agreement page when about the landlord has been completed " in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(AboutTheLandlordPageId, session6020)
        .apply(session6020) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.LeaseOrAgreementYearsController
          .show()
    }

    "redirect to when the current rent was agreed  when does the rent payable include has been completed " in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(IncludedInRent6020Id, session6020)
        .apply(session6020) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.RentOpenMarketValueController
          .show()
    }

    "redirect to what is the rent based on when the current rent was agreed has been completed with No " in {
      val session = session6020.copy(
        aboutLeaseOrAgreementPartOne =
          session6020.aboutLeaseOrAgreementPartOne.getOrElse(
            AboutLeaseOrAgreementPartOne(rentOpenMarketValue = AnswerNo)
          )
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(RentOpenMarketPageId, session)
        .apply(session) shouldBe controllers.aboutYourLeaseOrTenure.routes.WhatIsYourRentBasedOnController.show()
    }

    "redirect to ultimately responsible OR based on when is VAT Payable has been completed with No " in {
      val session = session6020.copy(
        aboutLeaseOrAgreementPartThree =
          session6020.aboutLeaseOrAgreementPartThree.getOrElse(
            AboutLeaseOrAgreementPartThree(isVATPayableForWholeProperty = AnswerNo)
          )
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(IsVATPayableForWholePropertyId, session)
        .apply(session) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.UltimatelyResponsibleOutsideRepairsController
          .show()
    }

    "redirect to IsVATPayableForWholePropertyController when 'vat' is included in includedInYourRent" in {
      val session = session6020.copy(
        aboutLeaseOrAgreementPartOne =
          AboutLeaseOrAgreementPartOne(
            includedInYourRentDetails =
              IncludedInYourRentDetails(includedInYourRent = List(IncludedInYourRentInformationVat))
          )
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(IncludedInYourRentPageId, session)
        .apply(session) shouldBe controllers.aboutYourLeaseOrTenure.routes.IsVATPayableForWholePropertyController.show()
    }

    "redirect to CanRentBeReducedOnReviewController when intervalsOfRentReview is defined for FOR6020" in {
      val session = session6020.copy(
        aboutLeaseOrAgreementPartTwo =
          AboutLeaseOrAgreementPartTwo(
            isRentReviewPlanned = AnswerYes,
            intervalsOfRentReview =
              IntervalsOfRentReview(
                currentRentWithin12Months = None,
                intervalsOfRentReview = LocalDate.of(2000, 1, 2)
              )
          )
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(IntervalsOfRentReviewId, session)
        .apply(session) shouldBe controllers.aboutYourLeaseOrTenure.routes.CanRentBeReducedOnReviewController.show()
    }

    "redirect to setting the current rent when the current rent was agreed has been completed with Yes " in {
      val session = session6020.copy(
        aboutLeaseOrAgreementPartOne =
          session6020.aboutLeaseOrAgreementPartOne.getOrElse(
            AboutLeaseOrAgreementPartOne(rentOpenMarketValue = AnswerYes)
          )
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(RentOpenMarketPageId, session)
        .apply(session) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.HowIsCurrentRentFixedController
          .show()
    }

    "redirect to MethodToFixCurrentRentController when HowIsCurrentRentFixed has been completed " in {
      val session = session6020.copy(
        aboutLeaseOrAgreementPartTwo =
          session6020.aboutLeaseOrAgreementPartTwo.getOrElse(
            AboutLeaseOrAgreementPartTwo(howIsCurrentRentFixed =
              HowIsCurrentRentFixed(CurrentRentFixedInterimRent, LocalDate.of(2000, 1, 2))
            )
          )
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(HowIsCurrentRentFixedId, session)
        .apply(session) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.MethodToFixCurrentRentController
          .show()
    }
    "redirect to Ultimately responsible BI page when Ultimately Responsible OR has been completed" in {
      val session = session6020.copy(
        aboutLeaseOrAgreementPartTwo =
          session6020.aboutLeaseOrAgreementPartTwo.getOrElse(
            AboutLeaseOrAgreementPartTwo(ultimatelyResponsibleOutsideRepairs =
              UltimatelyResponsibleOutsideRepairs(OutsideRepairsLandlord, "test")
            )
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

    "redirect to rented equipment details page when rent includes fixtures and fitting answer is Yes" in {
      val session = session6020.copy(
        aboutLeaseOrAgreementPartOne =
          session6020.aboutLeaseOrAgreementPartOne.getOrElse(
            AboutLeaseOrAgreementPartOne(rentIncludeFixturesAndFittings = AnswerYes)
          )
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(RentFixtureAndFittingsPageId, session)
        .apply(
          session
        ) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.RentedEquipmentDetailsController
          .show()
    }

    "redirect to work carried out condition page when property updates answer is no" in {
      val session = session6020.copy(
        aboutLeaseOrAgreementPartThree =
          session6020.aboutLeaseOrAgreementPartThree.getOrElse(
            AboutLeaseOrAgreementPartThree(propertyUpdates = AnswerNo)
          )
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(PropertyUpdatesId, session)
        .apply(
          session
        ) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.WorkCarriedOutConditionController
          .show()
    }

    "redirect to fixture and fittings details page when fixture and fittings with yes has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(RentFixtureAndFittingsPageId, session6020)
        .apply(
          session6020
        ) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.IncludedInRent6020Controller
          .show()
    }

    "redirect to rent vary on quantity of beer page when rent by gross or net turnover with no has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(RentPayableVaryAccordingToGrossOrNetId, session6020)
        .apply(
          session6020
        ) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.HowIsCurrentRentFixedController
          .show()
    }

    "redirect to rent include trade services page when Ultimately Responsible BI has been completed" in {
      val session = session6020.copy(
        aboutLeaseOrAgreementPartTwo =
          session6020.aboutLeaseOrAgreementPartTwo.getOrElse(
            AboutLeaseOrAgreementPartTwo(ultimatelyResponsibleBuildingInsurance =
              UltimatelyResponsibleBuildingInsurance(BuildingInsuranceLandlord, "test")
            )
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

    "redirect to incentives payment when can rent be reduced has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(CanRentBeReducedOnReviewId, session6020)
        .apply(session6020) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.PropertyUpdatesController
          .show()
    }

    "redirect to setting the current rent when What is your current rent has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(WhatRentBasedOnPageId, session6020)
        .apply(
          session6020
        ) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.HowIsCurrentRentFixedController
          .show()
    }

    "redirect to method to fix current rent page when how is current rent fixed has been completed" in {
      val session = session6020.copy(
        aboutLeaseOrAgreementPartTwo =
          session6020.aboutLeaseOrAgreementPartTwo.getOrElse(
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
      val session = session6020.copy(
        aboutLeaseOrAgreementPartTwo =
          session6020.aboutLeaseOrAgreementPartTwo.getOrElse(
            AboutLeaseOrAgreementPartTwo(methodToFixCurrentRentDetails = MethodToFixCurrentRentAgreement)
          )
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(MethodToFixCurrentRentsId, session)
        .apply(
          session
        ) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.IntervalsOfRentReviewController
          .show()
    }

    "redirect to is rent under review when intervals of rent has been completed " in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(IntervalsOfRentReviewId, session6020)
        .apply(session6020) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.IsRentUnderReviewController
          .show()
    }

    "redirect to how was the current rent agreed  when setting the current rent has been completed " in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(HowIsCurrentRentFixedId, session6020)
        .apply(session6020) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.MethodToFixCurrentRentController
          .show()
    }

    "redirect to does rent include parking  when payment for trade service has been completed   qw" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(PaymentForTradeServicesId, session6020)
        .apply(session6020) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.DoesRentIncludeParkingController
          .show()
    }

    "redirect to ultimately responsible outside when included in your rent has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(IncludedInYourRentPageId, session6020)
        .apply(
          session6020
        ) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.UltimatelyResponsibleOutsideRepairsController
          .show()
    }

    "redirect to trade services description page when addAnotherService is yes" in {
      val tradeServicesEntry  = TradeService(
        details = "Some service",
        addAnotherService = AnswerYes
      )
      val aboutLeasePartThree = AboutLeaseOrAgreementPartThree(tradeServices = IndexedSeq(tradeServicesEntry))
      val session             = session6020.copy(aboutLeaseOrAgreementPartThree = aboutLeasePartThree)

      aboutYourLeaseOrTenureNavigator
        .nextPage(TradeServicesListId, session)
        .apply(session) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.TradeServicesDescriptionController.show(Some(1))
    }

    "redirect to payment for trade services page when addAnotherService is no" in {
      val tradeServicesEntry  = TradeService(
        details = "test",
        addAnotherService = AnswerNo
      )
      val aboutLeasePartThree = AboutLeaseOrAgreementPartThree(tradeServices = IndexedSeq(tradeServicesEntry))
      val session             = session6020.copy(aboutLeaseOrAgreementPartThree = aboutLeasePartThree)

      aboutYourLeaseOrTenureNavigator
        .nextPage(TradeServicesListId, session)
        .apply(session) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.PaymentForTradeServicesController.show()
    }

    "redirect to ServicePaidSeparatelyController when addAnotherPaidService is AnswerYes" in {
      val paidServiceEntry    = ServicesPaid(
        details = "test",
        addAnotherPaidService = AnswerYes
      )
      val aboutLeasePartThree = AboutLeaseOrAgreementPartThree(servicesPaid = IndexedSeq(paidServiceEntry))
      val session             = session6020.copy(aboutLeaseOrAgreementPartThree = aboutLeasePartThree)

      aboutYourLeaseOrTenureNavigator
        .nextPage(ServicePaidSeparatelyListId, session)
        .apply(session) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.ServicePaidSeparatelyController.show(Some(1))
    }
  }
