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

class AboutYourLeaseOrTenure6010NavigatorSpec extends TestBaseSpec {

  val audit = mock[Audit]
  doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(any[HeaderCarrier], any[ExecutionContext])

  val navigator = new AboutYourLeaseOrTenureNavigator(audit)

  val session6010 = Session(testUserLoginDetails)

  implicit override val hc: HeaderCarrier = HeaderCarrier()

  "About your lease or tenure navigator" when {

    "go to sign in from an identifier that doesn't exist in the route map" in {
      case object UnknownIdentifier extends Identifier
      navigator.nextPage(UnknownIdentifier).apply(session6010) mustBe controllers.routes.LoginController.show()
    }

    "return a function that goes to lease or agreement years page when about your landlord has been completed" in {
      navigator
        .nextPage(AboutTheLandlordPageId)
        .apply(session6010) mustBe controllers.Form6010.routes.LeaseOrAgreementYearsController.show()
    }

    "return a function that goes to current rent first paid page when current annual rent has been completed" in {
      navigator
        .nextPage(CurrentAnnualRentPageId)
        .apply(session6010) mustBe controllers.aboutYourLeaseOrTenure.routes.CurrentRentFirstPaidController.show()
    }

    "return a function that goes to current lease or agreement begin page when current rent first paid has been completed" in {
      navigator
        .nextPage(CurrentRentFirstPaidPageId)
        .apply(session6010) mustBe controllers.Form6010.routes.CurrentLeaseOrAgreementBeginController.show()
    }
  }
}
