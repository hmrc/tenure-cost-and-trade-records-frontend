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

import utils.TestBaseSpec
import connectors.Audit
import models.ForType.*
import models.Session
import models.submissions.aboutYourLeaseOrTenure.{AboutLeaseOrAgreementPartOne, AboutLeaseOrAgreementPartThree, AboutLeaseOrAgreementPartTwo, BenefitsGiven, CurrentRentFixedInterimRent, CurrentRentFixedNewLeaseAgreement, HowIsCurrentRentFixed, IncludedInYourRentDetails, IntervalsOfRentReview, MethodToFixCurrentRentDetails, MethodToFixCurrentRentsAgreement, PayACapitalSumDetails, PropertyUpdates, RentIncludeFixturesAndFittingsDetails, RentIncludeTradeServicesDetails, RentOpenMarketValueDetails, ServicePaidSeparately, ServicesPaid, TenantAdditionsDisregardedDetails, ThroughputAffectsRent, TradeServices, TradeServicesDetails, UltimatelyResponsibleBuildingInsurance, UltimatelyResponsibleOutsideRepairs}
import models.submissions.common.AnswersYesNo.*
import models.submissions.common.{BuildingInsuranceLandlord, OutsideRepairsLandlord}
import navigation.identifiers.*
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import scala.concurrent.ExecutionContext

class AboutYourLeaseOrTenure6020NavigatorSpec extends TestBaseSpec {

  val audit = mock[Audit]
  doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(using any[HeaderCarrier], any[ExecutionContext])

  val navigator = new AboutYourLeaseOrTenureNavigator(audit)

  val session6020 =
    Session("99996020004", FOR6020, prefilledAddress, "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=", isWelsh = false)

  implicit override val hc: HeaderCarrier = HeaderCarrier()

  "About your lease or tenure navigator" when {

    "go to sign in from an identifier that doesn't exist in the route map" in {
      case object UnknownIdentifier extends Identifier
      navigator
        .nextPage(UnknownIdentifier, session6020)
        .apply(session6020) shouldBe controllers.routes.LoginController.show
    }

    "return a function that goes to lease surrendered early page when disregarded addition has been completed no" in {

      val session = session6020.copy(
        aboutLeaseOrAgreementPartTwo = Some(
          session6020.aboutLeaseOrAgreementPartTwo.getOrElse(
            AboutLeaseOrAgreementPartTwo(tenantAdditionsDisregardedDetails =
              Some(TenantAdditionsDisregardedDetails(AnswerNo))
            )
          )
        )
      )

      navigator
        .nextPage(TenantsAdditionsDisregardedId, session)
        .apply(session) shouldBe controllers.aboutYourLeaseOrTenure.routes.LeaseSurrenderedEarlyController.show()
    }

    "return a function that goes to throughput page when disregarded addition details has been completed " in {
      navigator
        .nextPage(CurrentAnnualRentPageId, session6020)
        .apply(session6020) shouldBe controllers.aboutYourLeaseOrTenure.routes.ThroughputAffectsRentController
        .show()
    }

    "return a function that goes to lease surrendered early page when disregarded addition details has been completed " in {
      navigator
        .nextPage(TenantsAdditionsDisregardedDetailsId, session6020)
        .apply(session6020) shouldBe controllers.aboutYourLeaseOrTenure.routes.LeaseSurrenderedEarlyController
        .show()
    }

    "return a function that goes to benefits given page when lease surrendered has been completed " in {
      navigator
        .nextPage(LeaseSurrenderedEarlyId, session6020)
        .apply(session6020) shouldBe controllers.aboutYourLeaseOrTenure.routes.BenefitsGivenController.show()
    }

    "return a function that goes to connected to landlord details page when connected to landlord and answer is 'no'" in {
      val answers = session6020.copy(
        aboutLeaseOrAgreementPartOne = Some(AboutLeaseOrAgreementPartOne(connectedToLandlord = Some(AnswerNo)))
      )
      val result  = navigator.connectedToLandlordRouting(answers)
      result shouldBe controllers.aboutYourLeaseOrTenure.routes.PropertyUseLeasebackArrangementController.show()

    }

    "return a function that goes to trade services description page when include trade services with yes has been completed" in {
      val session = session6020.copy(
        aboutLeaseOrAgreementPartOne = Some(
          session6020.aboutLeaseOrAgreementPartOne.getOrElse(
            AboutLeaseOrAgreementPartOne(rentIncludeTradeServicesDetails =
              Some(RentIncludeTradeServicesDetails(AnswerYes))
            )
          )
        )
      )

      navigator
        .nextPage(RentIncludeTradeServicesPageId, session)
        .apply(session) shouldBe controllers.aboutYourLeaseOrTenure.routes.TradeServicesDescriptionController.show()
    }

    "return a function that goes to trade services description page when include trade services with no has been completed" in {

      val session = session6020.copy(
        aboutLeaseOrAgreementPartOne = Some(
          session6020.aboutLeaseOrAgreementPartOne.getOrElse(
            AboutLeaseOrAgreementPartOne(rentIncludeTradeServicesDetails =
              Some(RentIncludeTradeServicesDetails(AnswerNo))
            )
          )
        )
      )

      navigator
        .nextPage(RentIncludeTradeServicesPageId, session)
        .apply(session) shouldBe controllers.aboutYourLeaseOrTenure.routes.PaymentForTradeServicesController.show()
    }

    "return a function that goes to benefits given detail page when benefits given has been completed for yes answer " in {
      val session = session6020.copy(
        aboutLeaseOrAgreementPartThree = Some(
          session6020.aboutLeaseOrAgreementPartThree.getOrElse(
            AboutLeaseOrAgreementPartThree(benefitsGiven = Some(BenefitsGiven(AnswerYes)))
          )
        )
      )
      navigator
        .nextPage(BenefitsGivenId, session)
        .apply(session) shouldBe controllers.aboutYourLeaseOrTenure.routes.BenefitsGivenDetailsController.show()
    }

    "return a function that goes to benefits given detail page when benefits given has been completed for no answer " in {
      val session = session6020.copy(
        aboutLeaseOrAgreementPartThree = Some(
          session6020.aboutLeaseOrAgreementPartThree.getOrElse(
            AboutLeaseOrAgreementPartThree(benefitsGiven = Some(BenefitsGiven(AnswerNo)))
          )
        )
      )
      navigator
        .nextPage(BenefitsGivenId, session)
        .apply(session) shouldBe controllers.aboutYourLeaseOrTenure.routes.PayACapitalSumController.show()
    }

    "return a function that goes to capital sum description page when pay a capital sum has been completed for yes answer " in {
      val session = session6020.copy(
        aboutLeaseOrAgreementPartTwo = Some(
          session6020.aboutLeaseOrAgreementPartTwo.getOrElse(
            AboutLeaseOrAgreementPartTwo(payACapitalSumDetails = Some(PayACapitalSumDetails(AnswerYes)))
          )
        )
      )
      navigator
        .nextPage(PayCapitalSumId, session)
        .apply(session) shouldBe controllers.aboutYourLeaseOrTenure.routes.CapitalSumDescriptionController.show()
    }

    "return a function that goes to throughput affects rent details page when throughput affects rent answer " in {
      val session = session6020.copy(
        aboutLeaseOrAgreementPartThree = Some(
          session6020.aboutLeaseOrAgreementPartThree.getOrElse(
            AboutLeaseOrAgreementPartThree(throughputAffectsRent = Some(ThroughputAffectsRent(AnswerNo, Some("text"))))
          )
        )
      )
      navigator
        .nextPage(ThroughputAffectsRentId, session)
        .apply(session) shouldBe controllers.aboutYourLeaseOrTenure.routes.CurrentRentFirstPaidController.show()
    }

    "return a function that goes to other factors affecting the rent page when capital sum has been completed for no answer " in {
      val session = session6020.copy(
        aboutLeaseOrAgreementPartTwo = Some(
          session6020.aboutLeaseOrAgreementPartTwo.getOrElse(
            AboutLeaseOrAgreementPartTwo(payACapitalSumDetails = Some(PayACapitalSumDetails(AnswerNo)))
          )
        )
      )
      navigator
        .nextPage(PayCapitalSumId, session)
        .apply(session) shouldBe controllers.aboutYourLeaseOrTenure.routes.LegalOrPlanningRestrictionsController.show()
    }

    "return a function that goes to other factors affecting the rent page when pay a capital sum description has been completed " in {
      navigator
        .nextPage(CapitalSumDescriptionId, session6020)
        .apply(session6020) shouldBe controllers.aboutYourLeaseOrTenure.routes.LegalOrPlanningRestrictionsController
        .show()
    }

    "return a function that goes to lease or agreement page when about the landlord has been completed " in {
      navigator
        .nextPage(AboutTheLandlordPageId, session6020)
        .apply(session6020) shouldBe controllers.aboutYourLeaseOrTenure.routes.LeaseOrAgreementYearsController
        .show()
    }

    "return a function that goes to when the current rent was agreed  when does the rent payable include has been completed " in {
      navigator
        .nextPage(IncludedInRent6020Id, session6020)
        .apply(session6020) shouldBe controllers.aboutYourLeaseOrTenure.routes.RentOpenMarketValueController
        .show()
    }

    "return a function that goes to what is the rent based on when the current rent was agreed has been completed with No " in {

      val session = session6020.copy(
        aboutLeaseOrAgreementPartOne = Some(
          session6020.aboutLeaseOrAgreementPartOne.getOrElse(
            AboutLeaseOrAgreementPartOne(rentOpenMarketValueDetails = Some(RentOpenMarketValueDetails(AnswerNo)))
          )
        )
      )
      navigator
        .nextPage(RentOpenMarketPageId, session)
        .apply(session) shouldBe controllers.aboutYourLeaseOrTenure.routes.WhatIsYourRentBasedOnController.show()
    }

    "return a function that goes to ultimately responsible OR based on when is VAT Payable has been completed with No " in {

      val session = session6020.copy(
        aboutLeaseOrAgreementPartThree = Some(
          session6020.aboutLeaseOrAgreementPartThree.getOrElse(
            AboutLeaseOrAgreementPartThree(isVATPayableForWholeProperty = Some(AnswerNo))
          )
        )
      )
      navigator
        .nextPage(IsVATPayableForWholePropertyId, session)
        .apply(session) shouldBe controllers.aboutYourLeaseOrTenure.routes.UltimatelyResponsibleOutsideRepairsController
        .show()
    }

    "return a function that goes to IsVATPayableForWholePropertyController when 'vat' is included in includedInYourRent" in {
      val session = session6020.copy(
        aboutLeaseOrAgreementPartOne = Some(
          AboutLeaseOrAgreementPartOne(
            includedInYourRentDetails = Some(IncludedInYourRentDetails(includedInYourRent = List("vat")))
          )
        )
      )

      navigator
        .nextPage(IncludedInYourRentPageId, session)
        .apply(session) shouldBe controllers.aboutYourLeaseOrTenure.routes.IsVATPayableForWholePropertyController.show()
    }

    "return a function that goes to CanRentBeReducedOnReviewController when intervalsOfRentReview is defined for FOR6020" in {
      val session = session6020.copy(
        aboutLeaseOrAgreementPartTwo = Some(
          AboutLeaseOrAgreementPartTwo(
            intervalsOfRentReview = Some(
              IntervalsOfRentReview(
                currentRentWithin12Months = None,
                intervalsOfRentReview = Some(LocalDate.of(2000, 1, 2))
              )
            )
          )
        )
      )

      navigator
        .nextPage(IntervalsOfRentReviewId, session)
        .apply(session) shouldBe controllers.aboutYourLeaseOrTenure.routes.CanRentBeReducedOnReviewController.show()
    }

    "return a function that goes to setting the current rent when the current rent was agreed has been completed with Yes " in {

      val session = session6020.copy(
        aboutLeaseOrAgreementPartOne = Some(
          session6020.aboutLeaseOrAgreementPartOne.getOrElse(
            AboutLeaseOrAgreementPartOne(rentOpenMarketValueDetails = Some(RentOpenMarketValueDetails(AnswerYes)))
          )
        )
      )

      navigator
        .nextPage(RentOpenMarketPageId, session)
        .apply(session) shouldBe controllers.aboutYourLeaseOrTenure.routes.HowIsCurrentRentFixedController
        .show()
    }

    "return a function that goes to MethodToFixCurrentRentController when HowIsCurrentRentFixed has been completed " in {

      val session = session6020.copy(
        aboutLeaseOrAgreementPartTwo = Some(
          session6020.aboutLeaseOrAgreementPartTwo.getOrElse(
            AboutLeaseOrAgreementPartTwo(howIsCurrentRentFixed =
              Some(HowIsCurrentRentFixed(CurrentRentFixedInterimRent, LocalDate.of(2000, 1, 2)))
            )
          )
        )
      )

      navigator
        .nextPage(HowIsCurrentRentFixedId, session)
        .apply(session) shouldBe controllers.aboutYourLeaseOrTenure.routes.MethodToFixCurrentRentController
        .show()
    }
    "return a function that goes to Ultimately responsible BI page when Ultimately Responsible OR has been completed" in {

      val session = session6020.copy(
        aboutLeaseOrAgreementPartTwo = Some(
          session6020.aboutLeaseOrAgreementPartTwo.getOrElse(
            AboutLeaseOrAgreementPartTwo(ultimatelyResponsibleOutsideRepairs =
              Some(UltimatelyResponsibleOutsideRepairs(OutsideRepairsLandlord, Some("test")))
            )
          )
        )
      )
      navigator
        .nextPage(UltimatelyResponsibleOutsideRepairsPageId, session)
        .apply(
          session
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.UltimatelyResponsibleInsideRepairsController
        .show()
    }

    "return a function that goes to rented equipment details page when rent includes fixtures and fitting answer is Yes" in {

      val session = session6020.copy(
        aboutLeaseOrAgreementPartOne = Some(
          session6020.aboutLeaseOrAgreementPartOne.getOrElse(
            AboutLeaseOrAgreementPartOne(rentIncludeFixturesAndFittingsDetails =
              Some(RentIncludeFixturesAndFittingsDetails(AnswerYes))
            )
          )
        )
      )
      navigator
        .nextPage(RentFixtureAndFittingsPageId, session)
        .apply(
          session
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.RentedEquipmentDetailsController
        .show()
    }

    "return a function that goes to work carried out condition page when property updates answer is no" in {

      val session = session6020.copy(
        aboutLeaseOrAgreementPartThree = Some(
          session6020.aboutLeaseOrAgreementPartThree.getOrElse(
            AboutLeaseOrAgreementPartThree(propertyUpdates = Some(PropertyUpdates(AnswerNo)))
          )
        )
      )
      navigator
        .nextPage(PropertyUpdatesId, session)
        .apply(
          session
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.WorkCarriedOutConditionController
        .show()
    }

    "return a function that goes to fixture and fittings details page when fixture and fittings with yes has been completed" in {
      navigator
        .nextPage(RentFixtureAndFittingsPageId, session6020)
        .apply(
          session6020
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.IncludedInRent6020Controller
        .show()
    }

    "return a function that goes to rent vary on quantity of beer page when rent by gross or net turnover with no has been completed" in {
      navigator
        .nextPage(RentPayableVaryAccordingToGrossOrNetId, session6020)
        .apply(
          session6020
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.HowIsCurrentRentFixedController
        .show()
    }

    "return a function that goes to rent include trade services page when Ultimately Responsible BI has been completed" in {

      val session = session6020.copy(
        aboutLeaseOrAgreementPartTwo = Some(
          session6020.aboutLeaseOrAgreementPartTwo.getOrElse(
            AboutLeaseOrAgreementPartTwo(ultimatelyResponsibleBuildingInsurance =
              Some(UltimatelyResponsibleBuildingInsurance(BuildingInsuranceLandlord, Some("test")))
            )
          )
        )
      )
      navigator
        .nextPage(UltimatelyResponsibleBusinessInsurancePageId, session)
        .apply(
          session
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.RentIncludeTradeServicesController
        .show()
    }

    "return a function that goes to incentives payment when can rent be reduced has been completed" in {
      navigator
        .nextPage(CanRentBeReducedOnReviewId, session6020)
        .apply(session6020) shouldBe controllers.aboutYourLeaseOrTenure.routes.PropertyUpdatesController
        .show()
    }

    "return a function that goes to setting the current rent when What is your current rent has been completed" in {
      navigator
        .nextPage(WhatRentBasedOnPageId, session6020)
        .apply(
          session6020
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.HowIsCurrentRentFixedController
        .show()
    }

    "return a function that goes to method to fix current rent page when how is current rent fixed has been completed" in {

      val session = session6020.copy(
        aboutLeaseOrAgreementPartTwo = Some(
          session6020.aboutLeaseOrAgreementPartTwo.getOrElse(
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
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.MethodToFixCurrentRentController
        .show()
    }

    "return a function that goes to intervals of rent page when method to fix current rent has been completed" in {

      val session = session6020.copy(
        aboutLeaseOrAgreementPartTwo = Some(
          session6020.aboutLeaseOrAgreementPartTwo.getOrElse(
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
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.IntervalsOfRentReviewController
        .show()
    }

    "return a function that goes to is rent under review when intervals of rent has been completed " in {
      navigator
        .nextPage(IntervalsOfRentReviewId, session6020)
        .apply(session6020) shouldBe controllers.aboutYourLeaseOrTenure.routes.IsRentUnderReviewController
        .show()
    }

    "return a function that goes to how was the current rent agreed  when setting the current rent has been completed " in {
      navigator
        .nextPage(HowIsCurrentRentFixedId, session6020)
        .apply(session6020) shouldBe controllers.aboutYourLeaseOrTenure.routes.MethodToFixCurrentRentController
        .show()
    }

    "return a function that goes to does rent include parking  when payment for trade service has been completed   qw" in {
      navigator
        .nextPage(PaymentForTradeServicesId, session6020)
        .apply(session6020) shouldBe controllers.aboutYourLeaseOrTenure.routes.DoesRentIncludeParkingController
        .show()
    }

    "return a function that goes to ultimately responsible outside when included in your rent has been completed" in {
      navigator
        .nextPage(IncludedInYourRentPageId, session6020)
        .apply(
          session6020
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.UltimatelyResponsibleOutsideRepairsController
        .show()
    }

    "return a function that goes to trade services description page when addAnotherService is yes" in {

      val tradeServicesEntry  = TradeServices(
        details = TradeServicesDetails(description = "Some service"),
        addAnotherService = Some(AnswerYes)
      )
      val aboutLeasePartThree = AboutLeaseOrAgreementPartThree(tradeServices = IndexedSeq(tradeServicesEntry))

      val session = session6020.copy(aboutLeaseOrAgreementPartThree = Some(aboutLeasePartThree))

      navigator
        .nextPage(TradeServicesListId, session)
        .apply(session) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.TradeServicesDescriptionController.show(Some(1))
    }

    "return a function that goes to payment for trade services page when addAnotherService is no" in {

      val tradeServicesEntry  = TradeServices(
        details = TradeServicesDetails(description = "test"),
        addAnotherService = Some(AnswerNo)
      )
      val aboutLeasePartThree = AboutLeaseOrAgreementPartThree(tradeServices = IndexedSeq(tradeServicesEntry))

      val session = session6020.copy(aboutLeaseOrAgreementPartThree = Some(aboutLeasePartThree))

      navigator
        .nextPage(TradeServicesListId, session)
        .apply(session) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.PaymentForTradeServicesController.show()
    }

    "return a function that goes to ServicePaidSeparatelyController when addAnotherPaidService is Some(AnswerYes)" in {
      val paidServiceEntry    = ServicesPaid(
        details = ServicePaidSeparately(description = "test"),
        addAnotherPaidService = Some(AnswerYes)
      )
      val aboutLeasePartThree = AboutLeaseOrAgreementPartThree(servicesPaid = IndexedSeq(paidServiceEntry))
      val session             = session6020.copy(aboutLeaseOrAgreementPartThree = Some(aboutLeasePartThree))

      navigator
        .nextPage(ServicePaidSeparatelyListId, session)
        .apply(session) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.ServicePaidSeparatelyController.show(Some(1))
    }

  }

}
