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
import navigation.identifiers._
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec

import scala.concurrent.ExecutionContext

class AdditionalInformationNavigatorSpec extends TestBaseSpec {

  val audit = mock[Audit]
  doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(using any[HeaderCarrier], any[ExecutionContext])

  val navigator = new AdditionalInformationNavigator(audit)

  "Additional information navigator" when {

    "go to sign in from an identifier that doesn't exist in the route map" in {
      case object UnknownIdentifier extends Identifier
      navigator
        .nextPage(UnknownIdentifier, additionalInformationSession)
        .apply(additionalInformationSession) shouldBe controllers.routes.LoginController.show
    }

    "return a function that goes to  CYA page when further information has been completed" in {
      navigator
        .nextPage(FurtherInformationId, additionalInformationSession)
        .apply(
          additionalInformationSession
        ) shouldBe controllers.additionalinformation.routes.CheckYourAnswersAdditionalInformationController
        .show()
    }

    "return a function that goes to task list page when CYA has been completed" in {
      navigator
        .nextPage(CheckYourAnswersAdditionalInformationId, additionalInformationSession)
        .apply(
          additionalInformationSession
        ) shouldBe controllers.routes.TaskListController
        .show()
    }

    "return the CYA url" in {
      val result = navigator.cyaPage
      result.map(
        _.url shouldBe controllers.additionalinformation.routes.CheckYourAnswersAdditionalInformationController
          .show()
          .url
      )
    }
  }
}
