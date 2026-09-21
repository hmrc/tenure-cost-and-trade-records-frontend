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
import models.submissions.aboutYourLeaseOrTenure.MethodToFixCurrentRent.*
import models.submissions.common.AnswersYesNo.*
import models.submissions.common.ResponsibilityParty.BuildingInsurance.*
import models.submissions.common.ResponsibilityParty.InsideRepairs.*
import models.submissions.common.ResponsibilityParty.OutsideRepairs.*
import navigation.identifiers.*
import test.{InjectedNavigation, TCTRAppSpec}

import scala.language.implicitConversions

class AboutYourLeaseOrTenure6076NavigatorSpec extends TCTRAppSpec with InjectedNavigation:

  private val session6076Full = Session(
    "99996076004",
    FOR6076,
    prefilledAddress,
    "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
    isWelsh = false,
    aboutLeaseOrAgreementPartOne = prefilledAboutLeaseOrAgreementPartOne,
    aboutLeaseOrAgreementPartTwo = prefilledAboutLeaseOrAgreementPartTwoNo,
    aboutLeaseOrAgreementPartThree = prefilledAboutLeaseOrAgreementPartThree
  )

  private val session6076NoLeaseback = Session(
    "99996076004",
    FOR6076,
    prefilledAddress,
    "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
    isWelsh = false,
    aboutLeaseOrAgreementPartOne = prefilledAboutLeaseOrAgreementPartOne,
    aboutLeaseOrAgreementPartTwo = prefilledAboutLeaseOrAgreementPartTwoNo,
    aboutLeaseOrAgreementPartThree = prefilledAboutLeaseOrAgreementPartThree
  )

  "Lease or agreement navigator for 6076" should {
    "redirect to current annual rent page when lease or agreement details with yes has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(LeaseOrAgreementDetailsPageId, session6076Full)
        .apply(
          session6076Full
        ) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.PropertyUseLeasebackArrangementController
          .show()
    }

    "redirect to connected to landlord details when connection to landlord yes has been completed1" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(AboutTheLandlordPageId, session6076Full)
        .apply(
          session6076Full
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordController.show()
    }

    "redirect to connected to landlord details when connection to landlord yes has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(ConnectedToLandlordPageId, session6076Full)
        .apply(
          session6076Full
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordDetailsController.show()
    }

    "redirect to property use leaseback arrangement when connection to landlord details has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(ConnectedToLandlordDetailsPageId, session6076Full)
        .apply(
          session6076Full
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.PropertyUseLeasebackArrangementController.show()
    }

    "redirect to property use leaseback arrangement when provide details of your lease has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(PropertyUseLeasebackAgreementId, session6076Full)
        .apply(
          session6076Full
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.ProvideDetailsOfYourLeaseController.show()
    }

    "redirect to property use leaseback arrangement no when provide details of your lease has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(PropertyUseLeasebackAgreementId, session6076NoLeaseback)
        .apply(
          session6076NoLeaseback
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.ProvideDetailsOfYourLeaseController.show()
    }

    "redirect to cya when property use leaseback arrangement has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(ProvideDetailsOfYourLeasePageId, session6076Full)
        .apply(
          session6076Full
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.CheckYourAnswersAboutYourLeaseOrTenureController.show()
    }

    "redirect to Ultimately responsible BI page when Ultimately Responsible OR has been completed" in {
      val session = session6076Full.copy(
        aboutLeaseOrAgreementPartTwo =
          session6076Full.aboutLeaseOrAgreementPartTwo.getOrElse(
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

    "redirect to Ultimately responsible IR page when Ultimately Responsible OR has been completed" in {
      val session = session6076Full.copy(
        aboutLeaseOrAgreementPartTwo =
          session6076Full.aboutLeaseOrAgreementPartTwo.getOrElse(
            AboutLeaseOrAgreementPartTwo(ultimatelyResponsibleInsideRepairs =
              UltimatelyResponsibleInsideRepairs(InsideRepairsLandlord, "test")
            )
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
      val session = session6076Full.copy(
        aboutLeaseOrAgreementPartTwo =
          session6076Full.aboutLeaseOrAgreementPartTwo.getOrElse(
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

    "redirect to intervals of rent page when method to fix current rent has been completed" in {
      val session = session6076Full.copy(
        aboutLeaseOrAgreementPartTwo =
          session6076Full.aboutLeaseOrAgreementPartTwo.getOrElse(
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

    "redirect to ProvideDetailsOfYourLeaseController when forType is FOR6076 and propertyUseLeasebackAgreement is NO" in {
      val sessionWithNoLeaseback = session6076Full.copy(
        aboutLeaseOrAgreementPartOne =
          prefilledAboutLeaseOrAgreementPartOne.copy(
            propertyUseLeasebackAgreement = AnswerNo
          )
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(PropertyUseLeasebackAgreementId, sessionWithNoLeaseback)
        .apply(sessionWithNoLeaseback) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.ProvideDetailsOfYourLeaseController.show()
    }
  }
