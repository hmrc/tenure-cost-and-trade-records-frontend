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

class AboutYourLeaseOrTenure6030NavigatorSpec extends TCTRAppSpec with InjectedNavigation:

  private val session6030 = Session(
    "99996030004",
    FOR6030,
    prefilledAddress,
    "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
    isWelsh = false,
    aboutLeaseOrAgreementPartOne = prefilledAboutLeaseOrAgreement6030Route,
    aboutLeaseOrAgreementPartTwo = prefilledAboutLeaseOrAgreementPartTwo6030
  )

  private val session6030Full = Session(
    "99996030004",
    FOR6030,
    prefilledAddress,
    "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
    isWelsh = false,
    aboutLeaseOrAgreementPartOne = prefilledAboutLeaseOrAgreement6030Route,
    aboutLeaseOrAgreementPartTwo = prefilledAboutLeaseOrAgreementPartTwoNo,
    aboutLeaseOrAgreementPartThree = prefilledAboutLeaseOrAgreementPartThree
  )

  "Lease or agreement navigator for 6030" should {
    "redirect to 'does the rent payable (...)', when 'what is the rent based on (...)' has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(WhatRentBasedOnPageId, session6030)
        .apply(
          session6030
        ) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryAccordingToGrossOrNetController
          .show()
    }

    "redirect to services paid separately when payment for trade services has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(PaymentForTradeServicesId, session6030Full)
        .apply(
          session6030Full
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.ServicePaidSeparatelyController.show()
    }

    "redirect to Ultimately responsible BI page when Ultimately Responsible OR has been completed" in {
      val session = session6030.copy(
        aboutLeaseOrAgreementPartTwo =
          session6030.aboutLeaseOrAgreementPartTwo.getOrElse(
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
      val session = session6030.copy(
        aboutLeaseOrAgreementPartTwo =
          session6030.aboutLeaseOrAgreementPartTwo.getOrElse(
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
      val session = session6030.copy(
        aboutLeaseOrAgreementPartTwo =
          session6030.aboutLeaseOrAgreementPartTwo.getOrElse(
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

    "redirect to method to fix current rent page when how is current rent fixed has been completed" in {
      val session = session6030.copy(
        aboutLeaseOrAgreementPartTwo =
          session6030.aboutLeaseOrAgreementPartTwo.getOrElse(
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
      val session = session6030.copy(
        aboutLeaseOrAgreementPartTwo =
          session6030.aboutLeaseOrAgreementPartTwo.getOrElse(
            AboutLeaseOrAgreementPartTwo(methodToFixCurrentRentDetails = MethodToFixCurrentRentAgreement)
          )
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(MethodToFixCurrentRentsId, session)
        .apply(
          session
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.IsRentReviewPlannedController.show()
    }
  }
