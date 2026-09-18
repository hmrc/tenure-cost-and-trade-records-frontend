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
import models.submissions.aboutYourLeaseOrTenure.{AboutLeaseOrAgreementPartOne, AboutLeaseOrAgreementPartTwo}
import models.submissions.common.AnswersYesNo.*
import navigation.identifiers.*
import test.{InjectedNavigation, TCTRAppSpec}

import java.time.LocalDate
import scala.language.implicitConversions

class AboutYourLeaseOrTenure6011NavigatorSpec extends TCTRAppSpec with InjectedNavigation:

  private val session6011 = Session("99996011004", FOR6011, prefilledAddress, "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=", isWelsh = false)

  "Lease or agreement navigator for 6011" should {
    "redirect to default page for identifier that doesn't exist in the route map" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(UnknownIdentifier, session6011)
        .apply(session6011) shouldBe controllers.routes.LoginController.show
    }

    "redirect to current annual rent page when about your landlord has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(ConnectedToLandlordDetailsPageId, session6011)
        .apply(session6011) shouldBe controllers.aboutYourLeaseOrTenure.routes.CurrentAnnualRentController.show()
    }

    "redirect to connected to landlord details page when connected to landlord and answer is 'yes'" in {
      val answers = session6011.copy(
        aboutLeaseOrAgreementPartOne = AboutLeaseOrAgreementPartOne(connectedToLandlord = AnswerYes)
      )
      val result  = aboutYourLeaseOrTenureNavigator.connectedToLandlordRouting(answers)
      result shouldBe controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordDetailsController.show()

    }

    "redirect to current annual page when connected to landlord has been completed and the answer is 'no'" in {
      val answers = session6011.copy(
        aboutLeaseOrAgreementPartOne = AboutLeaseOrAgreementPartOne(connectedToLandlord = AnswerNo)
      )
      val result  = aboutYourLeaseOrTenureNavigator.connectedToLandlordRouting(answers)
      result shouldBe controllers.aboutYourLeaseOrTenure.routes.CurrentAnnualRentController.show()
    }

    "redirect to  rent includes VAT page when current annual rent has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(CurrentAnnualRentPageId, session6011)
        .apply(session6011) shouldBe controllers.aboutYourLeaseOrTenure.routes.RentIncludesVatController.show()
    }

    "redirect to current rent first paid page when  rent includes VAT has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(RentIncludesVatPageId, session6011)
        .apply(session6011) shouldBe controllers.aboutYourLeaseOrTenure.routes.CurrentRentFirstPaidController.show()
    }

    "redirect to tenancy lease agreement expire page when current rent first paid has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(CurrentRentFirstPaidPageId, session6011)
        .apply(session6011) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.TenancyLeaseAgreementExpireController
          .show()
    }

    "redirect to tenancy lease expire page when current rent first paid has been completed" in {

      val session = session6011.copy(
        aboutLeaseOrAgreementPartOne =
          session6011.aboutLeaseOrAgreementPartOne.getOrElse(
            AboutLeaseOrAgreementPartOne(currentRentFirstPaid = LocalDate.of(2000, 2, 1))
          )
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(CurrentRentFirstPaidPageId, session)
        .apply(
          session
        ) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.TenancyLeaseAgreementExpireController
          .show()
    }

    "redirect to Tenancy lease agreement page when What is your current rent has been completed" in {
      val session = session6011.copy(
        aboutLeaseOrAgreementPartTwo =
          session6011.aboutLeaseOrAgreementPartTwo.getOrElse(
            AboutLeaseOrAgreementPartTwo(tenancyLeaseAgreementExpire = LocalDate.of(2000, 2, 1))
          )
      )
      aboutYourLeaseOrTenureNavigator
        .nextPage(CurrentRentFirstPaidPageId, session)
        .apply(
          session
        ) shouldBe
        controllers.aboutYourLeaseOrTenure.routes.TenancyLeaseAgreementExpireController
          .show()
    }

    "redirect to further information page when tenancy lease agreement expire has been completed" in {
      aboutYourLeaseOrTenureNavigator
        .nextPage(TenancyLeaseAgreementExpirePageId, session6011)
        .apply(
          session6011
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.CheckYourAnswersAboutYourLeaseOrTenureController.show()
    }
  }
