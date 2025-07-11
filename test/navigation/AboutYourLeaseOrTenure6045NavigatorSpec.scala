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
import models.ForType.*
import models.Session
import models.submissions.common.AnswersYesNo.*
import navigation.identifiers.*
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec

import scala.concurrent.ExecutionContext

class AboutYourLeaseOrTenure6045NavigatorSpec extends TestBaseSpec {

  val audit = mock[Audit]
  doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(using any[HeaderCarrier], any[ExecutionContext])

  val navigator = new AboutYourLeaseOrTenureNavigator(audit)

  val session6045 = Session(
    "99996045004",
    FOR6045,
    prefilledAddress,
    "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
    isWelsh = false,
    aboutLeaseOrAgreementPartOne = Some(prefilledAboutLeaseOrAgreement6045TextArea),
    aboutLeaseOrAgreementPartTwo = Some(prefilledAboutLeaseOrAgreementPartTwo6045),
    aboutLeaseOrAgreementPartThree = Some(prefilledAboutLeaseOrAgreementPartThree6045TextArea)
  )

  implicit override val hc: HeaderCarrier = HeaderCarrier()

  "About your lease or tenure navigator" when {

    "return a function that goes to Intervals Of Rent Review for 6045 when is Rent Under Review has been completed" in {
      navigator
        .nextPage(IsRentUnderReviewId, session6045)
        .apply(session6045) shouldBe controllers.aboutYourLeaseOrTenure.routes.IntervalsOfRentReviewController
        .show()
    }

    "return a function that goes to IsRentUnderReviewController for 6045 when is methodToFixCurrentRent has been completed" in {
      navigator
        .nextPage(MethodToFixCurrentRentsId, session6045)
        .apply(session6045) shouldBe controllers.aboutYourLeaseOrTenure.routes.IsRentUnderReviewController
        .show()
    }

    "return a function that goes to lease or agreement years page when about your landlord has been completed" in {
      navigator
        .nextPage(ConnectedToLandlordDetailsPageId, session6045)
        .apply(session6045) shouldBe controllers.aboutYourLeaseOrTenure.routes.PropertyUseLeasebackArrangementController
        .show()
    }

    "return a function that goes to rent include trade services details page when rent include trade services has been completed" in {
      navigator
        .nextPage(RentIncludeTradeServicesPageId, session6045)
        .apply(session6045) shouldBe controllers.aboutYourLeaseOrTenure.routes.RentIncludeTradeServicesDetailsController
        .show()
    }

    "return a function that goes to setting the current rent when What is your current rent has been completed" in {
      navigator
        .nextPage(WhatRentBasedOnPageId, session6045)
        .apply(
          session6045
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.HowIsCurrentRentFixedController
        .show()
    }

    "return a function that goes to describe the developments included in the rent page when rentDevelopedLand is completed with Yes" in {
      val answers = session6045.copy(
        aboutLeaseOrAgreementPartThree = Some(prefilledAboutLeaseOrAgreementPartThree)
      )
      navigator
        .nextPage(RentDevelopedLandId, answers)
        .apply(answers) shouldBe controllers.aboutYourLeaseOrTenure.routes.RentDevelopedLandDetailsController.show()
    }

    "return a function that goes to does the rent payable include any structures when rentDevelopedLand is completed with No" in {
      val answers = session6045.copy(
        aboutLeaseOrAgreementPartThree =
          Some(prefilledAboutLeaseOrAgreementPartThree.copy(rentDevelopedLand = Some(AnswerNo)))
      )
      navigator
        .nextPage(RentDevelopedLandId, answers)
        .apply(answers) shouldBe controllers.aboutYourLeaseOrTenure.routes.RentIncludeStructuresBuildingsController
        .show()
    }

    "return a function that goes to does the rent payable include any structures when rentDevelopedLandDetails is completed" in {
      val answers = session6045.copy(
        aboutLeaseOrAgreementPartThree = Some(prefilledAboutLeaseOrAgreementPartThree)
      )
      navigator
        .nextPage(RentDevelopedLandDetailsId, answers)
        .apply(answers) shouldBe controllers.aboutYourLeaseOrTenure.routes.RentIncludeStructuresBuildingsController
        .show()
    }

    "return a function that goes to structures details page when does the rent payable include any structures is completed with  Yes" in {
      val answers = session6045.copy(
        aboutLeaseOrAgreementPartFour = Some(prefilledAboutLeaseOrAgreementPartFour)
      )
      navigator
        .nextPage(RentIncludeStructuresBuildingsId, answers)
        .apply(
          answers
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.RentIncludeStructuresBuildingsDetailsController.show()
    }

    "return a function that goes to Ultimately Responsible Outside Repairs page when does the rent payable include any structures is completed with  No" in {
      val answers = session6045.copy(
        aboutLeaseOrAgreementPartFour =
          Some(prefilledAboutLeaseOrAgreementPartFour.copy(rentIncludeStructuresBuildings = Some(AnswerNo)))
      )
      navigator
        .nextPage(RentIncludeStructuresBuildingsId, answers)
        .apply(answers) shouldBe controllers.aboutYourLeaseOrTenure.routes.UltimatelyResponsibleOutsideRepairsController
        .show()
    }

    "return a function that goes to Ultimately Responsible Outside Repairs page when rent payable include any structures details is completed" in {
      val answers = session6045.copy(
        aboutLeaseOrAgreementPartFour = Some(prefilledAboutLeaseOrAgreementPartFour)
      )
      navigator
        .nextPage(RentIncludeStructuresBuildingsDetailsId, answers)
        .apply(answers) shouldBe controllers.aboutYourLeaseOrTenure.routes.UltimatelyResponsibleOutsideRepairsController
        .show()
    }

    "return a function that goes to lease surrendered early page when disregarded addition details has been completed " in {
      navigator
        .nextPage(TenantsAdditionsDisregardedDetailsId, session6045)
        .apply(session6045) shouldBe controllers.aboutYourLeaseOrTenure.routes.PropertyUpdatesController
        .show()
    }

    "return a function that goes to RentDevelopedLand page when DoesRentPayablePage has been completed " in {
      navigator
        .nextPage(DoesRentPayablePageId, session6045)
        .apply(session6045) shouldBe controllers.aboutYourLeaseOrTenure.routes.RentDevelopedLandController
        .show()
    }

    "return a function that goes to IsGivenRentFreePeriodController page when WorkCarriedOutCondition has been completed" in {
      navigator
        .nextPage(WorkCarriedOutConditionId, session6045)
        .apply(session6045) shouldBe controllers.aboutYourLeaseOrTenure.routes.IsGivenRentFreePeriodController
        .show()
    }

    "return a function that goes to tenants additional disregarded details page when tenants additional disregarded with yes has been completed" in {
      navigator
        .nextPage(TenantsAdditionsDisregardedId, session6045)
        .apply(
          session6045
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.TenantsAdditionsDisregardedDetailsController
        .show()
    }

    "return a function that goes to PropertyUpdatesController  when tenantAdditionalDisregarded is 'no'" in {
      val answers = session6045.copy(
        aboutLeaseOrAgreementPartTwo = Some(
          prefilledAboutLeaseOrAgreementPartTwo6045.copy(
            tenantAdditionsDisregarded = Some(AnswerNo)
          )
        )
      )

      navigator
        .nextPage(TenantsAdditionsDisregardedDetailsId, answers)
        .apply(answers) shouldBe controllers.aboutYourLeaseOrTenure.routes.PropertyUpdatesController.show()
    }

    "return a function that goes to TenantsAdditionsDisregardedController when formerLeaseSurrendered is 'no'" in {
      val answers = session6045.copy(
        aboutLeaseOrAgreementPartTwo = Some(
          prefilledAboutLeaseOrAgreementPartTwo6045.copy(
            incentivesPaymentsConditionsDetails = Some(AnswerNo)
          )
        )
      )

      navigator
        .nextPage(IncentivesPaymentsConditionsId, answers)
        .apply(answers) shouldBe controllers.aboutYourLeaseOrTenure.routes.TenantsAdditionsDisregardedController.show()
    }

    "return a function that goes to SurrenderLeaseAgreementDetailsController when formerLeaseSurrendered is 'yes'" in {
      val answers = session6045.copy(
        forType = FOR6045,
        aboutLeaseOrAgreementPartTwo = Some(
          prefilledAboutLeaseOrAgreementPartTwo6045.copy(
            incentivesPaymentsConditionsDetails = Some(AnswerYes)
          )
        )
      )

      navigator
        .nextPage(IncentivesPaymentsConditionsId, answers)
        .apply(answers) shouldBe controllers.aboutYourLeaseOrTenure.routes.SurrenderLeaseAgreementDetailsController
        .show()
    }

    "return a function that goes to PropertyUpdatesController when tenantAdditionalDisregarded is not 'yes' and forType is FOR6045" in {
      val answers = session6045.copy(
        forType = FOR6045,
        aboutLeaseOrAgreementPartTwo = Some(
          prefilledAboutLeaseOrAgreementPartTwo6045.copy(
            tenantAdditionsDisregarded = Some(AnswerNo)
          )
        )
      )

      navigator
        .nextPage(TenantsAdditionsDisregardedId, answers)
        .apply(answers) shouldBe controllers.aboutYourLeaseOrTenure.routes.PropertyUpdatesController.show()
    }

  }
}
