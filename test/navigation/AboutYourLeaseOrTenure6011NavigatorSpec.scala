/*
 * Copyright 2023 HM Revenue & Customs
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
import navigation.identifiers._
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec

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
      navigator.nextPage(UnknownIdentifier).apply(session6011) mustBe controllers.routes.LoginController.show()
    }

    "return a function that goes to current annual rent page when about your landlord has been completed" in {
      navigator
        .nextPage(AboutTheLandlordPageId)
        .apply(session6011) mustBe controllers.aboutYourLeaseOrTenure.routes.CurrentAnnualRentController.show()
    }

    "return a function that goes to current rent first paid page when current annual rent has been completed" in {
      navigator
        .nextPage(CurrentAnnualRentPageId)
        .apply(session6011) mustBe controllers.aboutYourLeaseOrTenure.routes.CurrentRentFirstPaidController.show()
    }

    "return a function that goes to tenancy lease agreement expire page when current rent first paid has been completed" in {
      navigator
        .nextPage(CurrentRentFirstPaidPageId)
        .apply(session6011) mustBe controllers.aboutYourLeaseOrTenure.routes.TenancyLeaseAgreementExpireController
        .show()
    }

    "return a function that goes to further information page when tenancy lease agreement expire has been completed" in {
      navigator
        .nextPage(TenancyLeaseAgreementExpirePageId)
        .apply(
          session6011
        ) mustBe controllers.aboutYourLeaseOrTenure.routes.CheckYourAnswersAboutYourLeaseOrTenureController.show()
    }
  }
}
