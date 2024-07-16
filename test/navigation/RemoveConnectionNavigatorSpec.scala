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
import models.submissions.aboutyouandtheproperty.{AboutYouAndTheProperty, AboutYouAndThePropertyPartTwo}
import models.submissions.additionalinformation.{AdditionalInformation, FurtherInformationOrRemarksDetails}
import models.submissions.common.ContactDetails
import models.submissions.connectiontoproperty.{AddressConnectionTypeYes, StillConnectedDetails}
import models.submissions.notconnected.{RemoveConnectionDetails, RemoveConnectionsDetails}
import navigation.identifiers.{Identifier, PastConnectionId, RemoveConnectionId}
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec

import scala.concurrent.ExecutionContext

class RemoveConnectionNavigatorSpec extends TestBaseSpec {

  val audit: Audit = mock[Audit]
  doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(any[HeaderCarrier], any[ExecutionContext])

  val navigator = new RemoveConnectionNavigator(audit)

  val aboutTheProperty: Option[AboutYouAndTheProperty]               = Some(AboutYouAndTheProperty(None))
  val aboutThePropertyPartTwo: Option[AboutYouAndThePropertyPartTwo] = Some(AboutYouAndThePropertyPartTwo(None))

  val removeConnection: Option[RemoveConnectionDetails]       = Some(
    RemoveConnectionDetails(
      Some(
        RemoveConnectionsDetails(
          "John Smith",
          ContactDetails("12345678909", "test@email.com"),
          Some("Additional Information is here")
        )
      )
    )
  )
  val additionalInformation: Option[AdditionalInformation]    = Some(
    AdditionalInformation(Some(FurtherInformationOrRemarksDetails("test")))
  )
  val stillConnectedDetailsYes: Option[StillConnectedDetails] = Some(
    StillConnectedDetails(Some(AddressConnectionTypeYes))
  )
  val sessionAdditionalInformation: Session                   =
    Session(
      "99996010004",
      "FOR6010",
      prefilledAddress,
      "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
      stillConnectedDetailsYes,
      removeConnection,
      aboutTheProperty,
      aboutThePropertyPartTwo,
      additionalInformation
    )

  "Remove connection navigator" when {

    "go to sign in from an identifier that doesn't exist in the route map" in {
      case object UnknownIdentifier extends Identifier
      navigator
        .nextPage(UnknownIdentifier, sessionAdditionalInformation)
        .apply(sessionAdditionalInformation) shouldBe controllers.routes.LoginController.show
    }

    "return a function that goes to remove connection page when past connection has been completed" in {
      navigator
        .nextPage(PastConnectionId, sessionAdditionalInformation)
        .apply(
          sessionAdditionalInformation
        ) shouldBe controllers.notconnected.routes.RemoveConnectionController
        .show()
    }

    "return a function that goes to CYA page when remove connection has been completed" in {
      navigator
        .nextPage(RemoveConnectionId, sessionAdditionalInformation)
        .apply(
          sessionAdditionalInformation
        ) shouldBe controllers.notconnected.routes.CheckYourAnswersNotConnectedController
        .show()
    }

    "return the CYA url" in {
      val result = navigator.cyaPage
      result.map(_.url shouldBe controllers.notconnected.routes.CheckYourAnswersNotConnectedController.show().url)
    }
  }
}
