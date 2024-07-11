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

import utils.TestBaseSpec
import connectors.Audit
import models.Session
import models.submissions.aboutYourLeaseOrTenure.{AboutLeaseOrAgreementPartOne, AboutLeaseOrAgreementPartThree, AboutLeaseOrAgreementPartTwo, BenefitsGiven, CurrentRentBasedOnPercentageOpenMarket, CurrentRentFixedInterimRent, CurrentRentFixedNewLeaseAgreement, HowIsCurrentRentFixed, MethodToFixCurrentRentDetails, MethodToFixCurrentRentsAgreement, PayACapitalSumDetails, RentOpenMarketValueDetails, TenantAdditionsDisregardedDetails, ThroughputAffectsRent, UltimatelyResponsibleBuildingInsurance, UltimatelyResponsibleInsideRepairs, UltimatelyResponsibleOutsideRepairs, WhatIsYourCurrentRentBasedOnDetails}
import models.submissions.common.{AnswerNo, AnswerYes, BuildingInsuranceLandlord, InsideRepairsLandlord, OutsideRepairsLandlord}
import navigation.identifiers._
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import scala.concurrent.ExecutionContext

class AboutYourLeaseOrTenure6020NavigatorSpec extends TestBaseSpec {

  val audit = mock[Audit]
  doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(any[HeaderCarrier], any[ExecutionContext])

  val navigator = new AboutYourLeaseOrTenureNavigator(audit)

  val session6020 = Session("99996020004", "FOR6020", prefilledAddress, "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=")

  implicit override val hc: HeaderCarrier = HeaderCarrier()

  "About your lease or tenure navigator" when {

    "go to sign in from an identifier that doesn't exist in the route map" in {
      case object UnknownIdentifier extends Identifier
      navigator
        .nextPage(UnknownIdentifier, session6020)
        .apply(session6020) mustBe controllers.routes.LoginController.show()
    }

    "return a function that goes to connected to landlord page when about your landlord has been completed" in {
      navigator
        .nextPage(AboutTheLandlordPageId, session6020)
        .apply(session6020) mustBe controllers.aboutYourLeaseOrTenure.routes.LeaseOrAgreementYearsController.show()
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
        .apply(session) mustBe controllers.aboutYourLeaseOrTenure.routes.LeaseSurrenderedEarlyController.show()
    }

    "return a function that goes to throughput page when disregarded addition details has been completed " in {
      navigator
        .nextPage(CurrentAnnualRentPageId, session6020)
        .apply(session6020) mustBe controllers.aboutYourLeaseOrTenure.routes.ThroughputAffectsRentController
        .show()
    }

    "return a function that goes to lease surrendered early page when disregarded addition details has been completed " in {
      navigator
        .nextPage(TenantsAdditionsDisregardedDetailsId, session6020)
        .apply(session6020) mustBe controllers.aboutYourLeaseOrTenure.routes.LeaseSurrenderedEarlyController
        .show()
    }

    "return a function that goes to benefits given page when lease surrendered has been completed " in {
      navigator
        .nextPage(LeaseSurrenderedEarlyId, session6020)
        .apply(session6020) mustBe controllers.aboutYourLeaseOrTenure.routes.BenefitsGivenController.show()
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
        .apply(session) mustBe controllers.aboutYourLeaseOrTenure.routes.BenefitsGivenDetailsController.show()
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
        .apply(session) mustBe controllers.aboutYourLeaseOrTenure.routes.PayACapitalSumController.show()
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
        .apply(session) mustBe controllers.aboutYourLeaseOrTenure.routes.CapitalSumDescriptionController.show()
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
        .apply(session) mustBe controllers.aboutYourLeaseOrTenure.routes.CurrentRentFirstPaidController.show()
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
        .apply(session) mustBe controllers.aboutYourLeaseOrTenure.routes.LegalOrPlanningRestrictionsController.show()
    }

    "return a function that goes to other factors affecting the rent page when pay a capital sum description has been completed " in {
      navigator
        .nextPage(CapitalSumDescriptionId, session6020)
        .apply(session6020) mustBe controllers.aboutYourLeaseOrTenure.routes.LegalOrPlanningRestrictionsController
        .show()
    }

    "return a function that goes to when the current rent was agreed  when does the rent payable include has been completed " in {
      navigator
        .nextPage(IncludedInRent6020Id, session6020)
        .apply(session6020) mustBe controllers.aboutYourLeaseOrTenure.routes.RentOpenMarketValueController
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
        .apply(session) mustBe controllers.aboutYourLeaseOrTenure.routes.WhatIsYourRentBasedOnController.show()
    }

    "return a function that goes to what is the rent based on when the current rent was agreed has been completed with No123 " in {

      val session = session6020.copy(
        aboutLeaseOrAgreementPartThree = Some(
          session6020.aboutLeaseOrAgreementPartThree.getOrElse(
            AboutLeaseOrAgreementPartThree(isVATPayableForWholeProperty = Some(AnswerNo))
          )
        )
      )
      navigator
        .nextPage(IsVATPayableForWholePropertyId, session)
        .apply(session) mustBe controllers.aboutYourLeaseOrTenure.routes.UltimatelyResponsibleOutsideRepairsController
        .show()
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
        .apply(session) mustBe controllers.aboutYourLeaseOrTenure.routes.HowIsCurrentRentFixedController
        .show()
    }

    "return a function that goes to setting the current rent when the current r123ent was agreed has been completed with Yes " in {

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
        .apply(session) mustBe controllers.aboutYourLeaseOrTenure.routes.MethodToFixCurrentRentController
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
        ) mustBe controllers.aboutYourLeaseOrTenure.routes.UltimatelyResponsibleInsideRepairsController
        .show()
    }

    "return a function that goes to Ultimately responsible IR page when Ultimately Responsible OR has been completed" in {

      val session = session6020.copy(
        aboutLeaseOrAgreementPartTwo = Some(
          session6020.aboutLeaseOrAgreementPartTwo.getOrElse(
            AboutLeaseOrAgreementPartTwo(ultimatelyResponsibleInsideRepairs =
              Some(UltimatelyResponsibleInsideRepairs(InsideRepairsLandlord, Some("test")))
            )
          )
        )
      )
      navigator
        .nextPage(UltimatelyResponsibleInsideRepairsPageId, session)
        .apply(
          session
        ) mustBe controllers.aboutYourLeaseOrTenure.routes.UltimatelyResponsibleBuildingInsuranceController
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
        ) mustBe controllers.aboutYourLeaseOrTenure.routes.RentIncludeTradeServicesController
        .show()
    }

    "return a function that goes to rent increase with RPI page when What is your current rent has been completed" in {

      val session = session6020.copy(
        aboutLeaseOrAgreementPartOne = Some(
          session6020.aboutLeaseOrAgreementPartOne.getOrElse(
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
        ) mustBe controllers.aboutYourLeaseOrTenure.routes.HowIsCurrentRentFixedController
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
        ) mustBe controllers.aboutYourLeaseOrTenure.routes.MethodToFixCurrentRentController
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
        ) mustBe controllers.aboutYourLeaseOrTenure.routes.IntervalsOfRentReviewController
        .show()
    }

    "return a function that goes to how was the current rent agreed  when setting the current rent has been completed " in {
      navigator
        .nextPage(HowIsCurrentRentFixedId, session6020)
        .apply(session6020) mustBe controllers.aboutYourLeaseOrTenure.routes.MethodToFixCurrentRentController
        .show()
    }

  }

}
