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
import models.submissions.common.AnswersYesNo.*
import navigation.identifiers.*
import test.{InjectedNavigation, TCTRAppSpec}

import scala.language.implicitConversions

class AboutYourLeaseOrTenure6045NavigatorSpec extends TCTRAppSpec with InjectedNavigation:

  val session6045: Session = Session(
    "99996045004",
    FOR6045,
    prefilledAddress,
    "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
    isWelsh = false,
    aboutLeaseOrAgreementPartOne = prefilledAboutLeaseOrAgreement6045TextArea,
    aboutLeaseOrAgreementPartTwo = prefilledAboutLeaseOrAgreementPartTwo6045,
    aboutLeaseOrAgreementPartThree = prefilledAboutLeaseOrAgreementPartThree6045TextArea
  )

  "Lease or agreement navigator for 6045" should {
    "redirect to Intervals Of Rent Review when is Rent Under Review has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(IsRentUnderReviewId, session6045)
        .apply(session6045) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.IntervalsOfRentReviewController
          .show()
    }

    "redirect to IsRentUnderReviewController when is methodToFixCurrentRent has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(MethodToFixCurrentRentsId, session6045)
        .apply(session6045) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.IsRentUnderReviewController
          .show()
    }

    "redirect to lease or agreement years page when about your landlord has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(ConnectedToLandlordDetailsPageId, session6045)
        .apply(session6045) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.PropertyUseLeasebackArrangementController
          .show()
    }

    "redirect to rent include trade services details page when rent include trade services has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(RentIncludeTradeServicesPageId, session6045)
        .apply(session6045) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.RentIncludeTradeServicesDetailsController
          .show()
    }

    "redirect to setting the current rent when What is your current rent has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(WhatRentBasedOnPageId, session6045)
        .apply(
          session6045
        ) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.HowIsCurrentRentFixedController
          .show()
    }

    "redirect to describe the developments included in the rent page when rentDevelopedLand is completed with Yes" in {
      val answers = session6045.copy(
        aboutLeaseOrAgreementPartThree = prefilledAboutLeaseOrAgreementPartThree
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(RentDevelopedLandId, answers)
        .apply(answers) shouldBe controllers.aboutYourLeaseOrTenure.routes.RentDevelopedLandDetailsController.show()
    }

    "redirect to does the rent payable include any structures when rentDevelopedLand is completed with No" in {
      val answers = session6045.copy(
        aboutLeaseOrAgreementPartThree =
          prefilledAboutLeaseOrAgreementPartThree.copy(rentDevelopedLand = AnswerNo)
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(RentDevelopedLandId, answers)
        .apply(answers) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.RentIncludeStructuresBuildingsController
          .show()
    }

    "redirect to does the rent payable include any structures when rentDevelopedLandDetails is completed" in {
      val answers = session6045.copy(
        aboutLeaseOrAgreementPartThree = prefilledAboutLeaseOrAgreementPartThree
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(RentDevelopedLandDetailsId, answers)
        .apply(answers) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.RentIncludeStructuresBuildingsController
          .show()
    }

    "redirect to structures details page when does the rent payable include any structures is completed with  Yes" in {
      val answers = session6045.copy(
        aboutLeaseOrAgreementPartFour = prefilledAboutLeaseOrAgreementPartFour
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(RentIncludeStructuresBuildingsId, answers)
        .apply(
          answers
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.RentIncludeStructuresBuildingsDetailsController.show()
    }

    "redirect to Ultimately Responsible Outside Repairs page when does the rent payable include any structures is completed with  No" in {
      val answers = session6045.copy(
        aboutLeaseOrAgreementPartFour =
          prefilledAboutLeaseOrAgreementPartFour.copy(rentIncludeStructuresBuildings = AnswerNo)
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(RentIncludeStructuresBuildingsId, answers)
        .apply(answers) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.UltimatelyResponsibleOutsideRepairsController
          .show()
    }

    "redirect to Ultimately Responsible Outside Repairs page when rent payable include any structures details is completed" in {
      val answers = session6045.copy(
        aboutLeaseOrAgreementPartFour = prefilledAboutLeaseOrAgreementPartFour
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(RentIncludeStructuresBuildingsDetailsId, answers)
        .apply(answers) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.UltimatelyResponsibleOutsideRepairsController
          .show()
    }

    "redirect to lease surrendered early page when disregarded addition details has been completed " in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(TenantsAdditionsDisregardedDetailsId, session6045)
        .apply(session6045) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.PropertyUpdatesController
          .show()
    }

    "redirect to RentDevelopedLand page when DoesRentPayablePage has been completed " in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(DoesRentPayablePageId, session6045)
        .apply(session6045) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.RentDevelopedLandController
          .show()
    }

    "redirect to IsGivenRentFreePeriodController page when WorkCarriedOutCondition has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(WorkCarriedOutConditionId, session6045)
        .apply(session6045) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.IsGivenRentFreePeriodController
          .show()
    }

    "redirect to tenants additional disregarded details page when tenants additional disregarded with yes has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(TenantsAdditionsDisregardedId, session6045)
        .apply(
          session6045
        ) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.TenantsAdditionsDisregardedDetailsController
          .show()
    }

    "redirect to PropertyUpdatesController  when tenantAdditionalDisregarded is 'no'" in {
      val answers = session6045.copy(
        aboutLeaseOrAgreementPartTwo =
          prefilledAboutLeaseOrAgreementPartTwo6045.copy(
            tenantAdditionsDisregarded = AnswerNo
          )
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(TenantsAdditionsDisregardedDetailsId, answers)
        .apply(answers) shouldBe controllers.aboutYourLeaseOrTenure.routes.PropertyUpdatesController.show()
    }

    "redirect to TenantsAdditionsDisregardedController when formerLeaseSurrendered is 'no'" in {
      val answers = session6045.copy(
        aboutLeaseOrAgreementPartTwo =
          prefilledAboutLeaseOrAgreementPartTwo6045.copy(
            incentivesPaymentsConditionsDetails = AnswerNo
          )
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(IncentivesPaymentsConditionsId, answers)
        .apply(answers) shouldBe controllers.aboutYourLeaseOrTenure.routes.TenantsAdditionsDisregardedController.show()
    }

    "redirect to SurrenderLeaseAgreementDetailsController when formerLeaseSurrendered is 'yes'" in {
      val answers = session6045.copy(
        forType = FOR6045,
        aboutLeaseOrAgreementPartTwo =
          prefilledAboutLeaseOrAgreementPartTwo6045.copy(
            incentivesPaymentsConditionsDetails = AnswerYes
          )
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(IncentivesPaymentsConditionsId, answers)
        .apply(answers) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.SurrenderLeaseAgreementDetailsController
          .show()
    }

    "redirect to PropertyUpdatesController when tenantAdditionalDisregarded is not 'yes' and forType is FOR6045" in {
      val answers = session6045.copy(
        forType = FOR6045,
        aboutLeaseOrAgreementPartTwo =
          prefilledAboutLeaseOrAgreementPartTwo6045.copy(
            tenantAdditionsDisregarded = AnswerNo
          )
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(TenantsAdditionsDisregardedId, answers)
        .apply(answers) shouldBe controllers.aboutYourLeaseOrTenure.routes.PropertyUpdatesController.show()
    }
  }
