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
import models.submissions.common.AnswersYesNo.*
import navigation.identifiers.*
import test.{InjectedNavigation, TCTRAppSpec}

import scala.language.implicitConversions

class AboutYourLeaseOrTenure6048NavigatorSpec extends TCTRAppSpec with InjectedNavigation:

  private val session6048 = Session(
    "99996048004",
    FOR6048,
    prefilledAddress,
    "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
    isWelsh = false,
    aboutLeaseOrAgreementPartOne = prefilledAboutLeaseOrAgreement6045TextArea,
    aboutLeaseOrAgreementPartThree = prefilledAboutLeaseOrAgreementPartThree6045TextArea
  )

  "Lease or agreement navigator for 6048" should {
    "redirect to how rent is currently fixed  page when UR building insurance is completed" in {
      val answers = session6048.copy(
        aboutLeaseOrAgreementPartTwo = prefilledAboutLeaseOrAgreementPartTwo
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(HowIsCurrentRentFixedId, answers)
        .apply(answers) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.MethodToFixCurrentRentController
          .show()
    }

    "redirect to how rent is currently fixed  page when UR building insurance is completed123" in {
      val answers = session6048.copy(
        aboutLeaseOrAgreementPartTwo = prefilledAboutLeaseOrAgreementPartTwo
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(UltimatelyResponsibleBusinessInsurancePageId, answers)
        .apply(answers) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.HowIsCurrentRentFixedController
          .show()
    }

    "redirect to work carried out condition page when property updates answer is no123" in {
      val session = session6048.copy(
        aboutLeaseOrAgreementPartTwo =
          session6048.aboutLeaseOrAgreementPartTwo.getOrElse(
            AboutLeaseOrAgreementPartTwo(payACapitalSumOrPremium = AnswerYes)
          )
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(PayCapitalSumId, session)
        .apply(
          session
        ) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.PayACapitalSumAmountDetailsController
          .show()
    }

    "redirect to does rent include fixture and fittings  when is parking rent paid separately has been completed123" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(PayCapitalSumDetailsId, session6048)
        .apply(session6048) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.LegalOrPlanningRestrictionsController
          .show()
    }
  }
