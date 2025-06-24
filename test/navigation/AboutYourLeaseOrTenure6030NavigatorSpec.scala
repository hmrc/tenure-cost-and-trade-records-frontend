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
import models.submissions.aboutYourLeaseOrTenure.*
import models.submissions.aboutYourLeaseOrTenure.CurrentRentFixed.*
import models.submissions.aboutYourLeaseOrTenure.MethodToFixCurrentRent.*
import models.submissions.common.ResponsibilityParty.BuildingInsurance.*
import models.submissions.common.ResponsibilityParty.InsideRepairs.*
import models.submissions.common.ResponsibilityParty.OutsideRepairs.*
import navigation.identifiers._
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec

import java.time.LocalDate
import scala.concurrent.ExecutionContext

class AboutYourLeaseOrTenure6030NavigatorSpec extends TestBaseSpec {

  val audit = mock[Audit]
  doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(using any[HeaderCarrier], any[ExecutionContext])

  val navigator = new AboutYourLeaseOrTenureNavigator(audit)

  val session6030 = Session(
    "99996030004",
    FOR6030,
    prefilledAddress,
    "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
    isWelsh = false,
    aboutLeaseOrAgreementPartOne = Some(prefilledAboutLeaseOrAgreement6030Route),
    aboutLeaseOrAgreementPartTwo = Some(prefilledAboutLeaseOrAgreementPartTwo6030)
  )

  val session6030Full = Session(
    "99996030004",
    FOR6030,
    prefilledAddress,
    "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
    isWelsh = false,
    aboutLeaseOrAgreementPartOne = Some(prefilledAboutLeaseOrAgreement6030Route),
    aboutLeaseOrAgreementPartTwo = Some(prefilledAboutLeaseOrAgreementPartTwoNo),
    aboutLeaseOrAgreementPartThree = Some(prefilledAboutLeaseOrAgreementPartThree)
  )

  val session6030No = Session(
    "99996030004",
    FOR6030,
    prefilledAddress,
    "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
    isWelsh = false,
    aboutLeaseOrAgreementPartOne = Some(prefilledAboutLeaseOrAgreementPartOneNo),
    aboutLeaseOrAgreementPartTwo = Some(prefilledAboutLeaseOrAgreementPartTwoNo)
  )

  implicit override val hc: HeaderCarrier = HeaderCarrier()

  "About your lease or tenure navigator" when {

    "return a function that goes to 'does the rent payable (...)', when 'what is the rent based on (...)' has been completed" in {
      navigator
        .nextPage(WhatRentBasedOnPageId, session6030)
        .apply(
          session6030
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryAccordingToGrossOrNetController
        .show()
    }

    "return a function that goes to services paid separately when payment for trade services has been completed" in {
      navigator
        .nextPage(PaymentForTradeServicesId, session6030Full)
        .apply(
          session6030Full
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.ServicePaidSeparatelyController.show()
    }

    "return a function that goes to Ultimately responsible BI page when Ultimately Responsible OR has been completed" in {

      val session = session6030.copy(
        aboutLeaseOrAgreementPartTwo = Some(
          session6030.aboutLeaseOrAgreementPartTwo.getOrElse(
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

    "return a function that goes to Ultimately responsible IR page when Ultimately Responsible OR has been completed" in {

      val session = session6030.copy(
        aboutLeaseOrAgreementPartTwo = Some(
          session6030.aboutLeaseOrAgreementPartTwo.getOrElse(
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
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.UltimatelyResponsibleBuildingInsuranceController
        .show()
    }

    "return a function that goes to rent include trade services page when Ultimately Responsible BI has been completed" in {

      val session = session6030.copy(
        aboutLeaseOrAgreementPartTwo = Some(
          session6030.aboutLeaseOrAgreementPartTwo.getOrElse(
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

    "return a function that goes to method to fix current rent page when how is current rent fixed has been completed" in {

      val session = session6030.copy(
        aboutLeaseOrAgreementPartTwo = Some(
          session6030.aboutLeaseOrAgreementPartTwo.getOrElse(
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

      val session = session6030.copy(
        aboutLeaseOrAgreementPartTwo = Some(
          session6030.aboutLeaseOrAgreementPartTwo.getOrElse(
            AboutLeaseOrAgreementPartTwo(methodToFixCurrentRentDetails =
              Some(MethodToFixCurrentRentDetails(MethodToFixCurrentRentAgreement))
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
  }
}
