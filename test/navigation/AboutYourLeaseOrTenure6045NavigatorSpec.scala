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
import models.submissions.aboutYourLeaseOrTenure.*
import models.submissions.common.{BuildingInsuranceLandlord, InsideRepairsLandlord, OutsideRepairsLandlord}
import navigation.identifiers.*
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec

import java.time.LocalDate
import scala.concurrent.ExecutionContext

class AboutYourLeaseOrTenure6045NavigatorSpec extends TestBaseSpec {

  val audit = mock[Audit]
  doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(any[HeaderCarrier], any[ExecutionContext])

  val navigator = new AboutYourLeaseOrTenureNavigator(audit)

  val session6045 = Session(
    "99996045004",
    "FOR6045 ",
    prefilledAddress,
    "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
    aboutLeaseOrAgreementPartOne = Some(prefilledAboutLeaseOrAgreement6045TextArea),
    aboutLeaseOrAgreementPartThree = Some(prefilledAboutLeaseOrAgreementPartThree6045TextArea)
  )

  implicit override val hc: HeaderCarrier = HeaderCarrier()

  "About your lease or tenure navigator" when {

    "go to sign in from an identifier that doesn't exist in the route map" in {
      case object UnknownIdentifier extends Identifier
      navigator
        .nextPage(UnknownIdentifier, session6045)
        .apply(session6045) shouldBe controllers.routes.LoginController.show
    }

    "return a function that goes to connected to landlors page for 6045 when about your landlord has been completed" in {
      navigator
        .nextPage(ConnectedToLandlordPageId, session6045)
        .apply(session6045) shouldBe controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordDetailsController
        .show()
    }

    "return a function that goes to lease or agreement years page when about your landlord has been completed" in {
      navigator
        .nextPage(ConnectedToLandlordDetailsPageId, session6045)
        .apply(session6045) shouldBe controllers.aboutYourLeaseOrTenure.routes.LeaseOrAgreementYearsController.show()
    }

  }
}
