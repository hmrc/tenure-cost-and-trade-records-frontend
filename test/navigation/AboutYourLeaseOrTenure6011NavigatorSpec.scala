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

import connectors.Audit
import models.Session
import models.submissions.aboutYourLeaseOrTenure.{AboutLeaseOrAgreementPartOne, AboutLeaseOrAgreementPartTwo, CurrentRentFirstPaid, TenancyLeaseAgreementExpire}
import models.submissions.common.{AnswerNo, AnswerYes}
import navigation.identifiers._
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec

import java.time.LocalDate
import scala.concurrent.ExecutionContext

class AboutYourLeaseOrTenure6011NavigatorSpec extends TestBaseSpec {

  val audit = mock[Audit]
  doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(any[HeaderCarrier], any[ExecutionContext])

  val navigator = new AboutYourLeaseOrTenureNavigator(audit)

  val session6011 = Session("99996010004", "FOR6011", prefilledAddress, "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=")

  implicit override val hc: HeaderCarrier = HeaderCarrier()

  "About your lease or tenure navigator" when {

    "go to sign in from an identifier that doesn't exist in the route map" in {
      case object UnknownIdentifier extends Identifier
      navigator
        .nextPage(UnknownIdentifier, session6011)
        .apply(session6011) shouldBe controllers.routes.LoginController.show
    }

    "return a function that goes to connected to landlord page when about your landlord has been completed" in {
      navigator
        .nextPage(AboutTheLandlordPageId, session6011)
        .apply(session6011) shouldBe controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordController.show()
    }

    "return a function that goes to current annual rent page when about your landlord has been completed" in {
      navigator
        .nextPage(ConnectedToLandlordDetailsPageId, session6011)
        .apply(session6011) shouldBe controllers.aboutYourLeaseOrTenure.routes.CurrentAnnualRentController.show()
    }

    "return a function that goes to connected to landlord details page when connected to landlord and answer is 'yes'" in {
      val answers = session6011.copy(
        aboutLeaseOrAgreementPartOne = Some(AboutLeaseOrAgreementPartOne(connectedToLandlord = Some(AnswerYes)))
      )
      val result  = navigator.connectedToLandlordRouting(answers)
      result shouldBe controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordDetailsController.show()

    }

    "return a function that goes to current annual page when connected to landlord has been completed and the answer is 'no'" in {
      val answers = session6011.copy(
        aboutLeaseOrAgreementPartOne = Some(AboutLeaseOrAgreementPartOne(connectedToLandlord = Some(AnswerNo)))
      )
      val result  = navigator.connectedToLandlordRouting(answers)
      result shouldBe controllers.aboutYourLeaseOrTenure.routes.CurrentAnnualRentController.show()
    }

    "return a function that goes to  rent includes VAT page when current annual rent has been completed" in {
      navigator
        .nextPage(CurrentAnnualRentPageId, session6011)
        .apply(session6011) shouldBe controllers.aboutYourLeaseOrTenure.routes.RentIncludesVatController.show()
    }

    "return a function that goes to current rent first paid page when  rent includes VAT has been completed" in {
      navigator
        .nextPage(RentIncludesVatPageId, session6011)
        .apply(session6011) shouldBe controllers.aboutYourLeaseOrTenure.routes.CurrentRentFirstPaidController.show()
    }

    "return a function that goes to tenancy lease agreement expire page when current rent first paid has been completed" in {
      navigator
        .nextPage(CurrentRentFirstPaidPageId, session6011)
        .apply(session6011) shouldBe controllers.aboutYourLeaseOrTenure.routes.TenancyLeaseAgreementExpireController
        .show()
    }

    "return a function that goes to tenancy lease expire page when current rent first paid has been completed" in {

      val session = session6011.copy(
        aboutLeaseOrAgreementPartOne = Some(
          session6011.aboutLeaseOrAgreementPartOne.getOrElse(
            AboutLeaseOrAgreementPartOne(currentRentFirstPaid = Some(CurrentRentFirstPaid(LocalDate.of(2000, 2, 1))))
          )
        )
      )
      navigator
        .nextPage(CurrentRentFirstPaidPageId, session)
        .apply(
          session
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.TenancyLeaseAgreementExpireController
        .show()
    }

    "return a function that goes to Tenancy lease agreement page when What is your current rent has been completed" in {

      val session = session6011.copy(
        aboutLeaseOrAgreementPartTwo = Some(
          session6011.aboutLeaseOrAgreementPartTwo.getOrElse(
            AboutLeaseOrAgreementPartTwo(tenancyLeaseAgreementExpire =
              Some(TenancyLeaseAgreementExpire(LocalDate.of(2000, 2, 1)))
            )
          )
        )
      )
      navigator
        .nextPage(CurrentRentFirstPaidPageId, session)
        .apply(
          session
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.TenancyLeaseAgreementExpireController
        .show()
    }

    "return a function that goes to further information page when tenancy lease agreement expire has been completed" in {
      navigator
        .nextPage(TenancyLeaseAgreementExpirePageId, session6011)
        .apply(
          session6011
        ) shouldBe controllers.aboutYourLeaseOrTenure.routes.CheckYourAnswersAboutYourLeaseOrTenureController.show()
    }
  }
}
