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
import navigation.identifiers._
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec

import scala.concurrent.ExecutionContext

class RequestReferenceNumberNavigatorSpec extends TestBaseSpec {

  val audit = mock[Audit]
  doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(any[HeaderCarrier], any[ExecutionContext])

  val navigator = new RequestReferenceNumberNavigator(audit)

  implicit override val hc: HeaderCarrier = HeaderCarrier()

  "RequestReferenceNumber navigator" when {

    "go to sign in from an identifier that doesn't exist in the route map" in {
      case object UnknownIdentifier extends Identifier
      navigator
        .nextPage(UnknownIdentifier, stillConnectedDetailsYesSession)
        .apply(stillConnectedDetailsYesSession) mustBe controllers.routes.LoginController.show()
    }

    "return a function that goes to RequestReferenceNumberContactDetailsController from NoReferenceNumberPageId" in {
      navigator
        .nextPage(NoReferenceNumberPageId, stillConnectedDetailsYesSession)
        .apply(
          stillConnectedDetailsYesSession
        ) mustBe controllers.requestReferenceNumber.routes.RequestReferenceNumberContactDetailsController
        .show()
    }

    "return a function that goes to CheckYourAnswersRequestReferenceNumberController from NoReferenceNumberContactDetailsPageId" in {
      navigator
        .nextPage(NoReferenceNumberContactDetailsPageId, stillConnectedDetailsYesSession)
        .apply(
          stillConnectedDetailsYesSession
        ) mustBe controllers.requestReferenceNumber.routes.CheckYourAnswersRequestReferenceNumberController
        .show()
    }

    "return a function that goes to request reference number confirmation from CheckYourAnswersRequestReferenceNumberPageId" in {
      navigator
        .nextPage(CheckYourAnswersRequestReferenceNumberPageId, stillConnectedDetailsYesSession)
        .apply(
          stillConnectedDetailsYesSession
        ) mustBe controllers.requestReferenceNumber.routes.CheckYourAnswersRequestReferenceNumberController
        .confirmation()
    }
  }

}
