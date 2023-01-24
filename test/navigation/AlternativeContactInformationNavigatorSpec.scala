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
import models.submissions.abouttheproperty.AboutTheProperty
import models.submissions.aboutyou.{AboutYou, CustomerDetails}
import models.submissions.additionalinformation.{AdditionalInformation, FurtherInformationOrRemarksDetails}
import models.submissions.common.ContactDetails
import models.submissions.connectiontoproperty.{AddressConnectionTypeYes, StillConnectedDetails}
import models.submissions.notconnected.{RemoveConnectionDetails, RemoveConnectionsDetails}
import navigation.identifiers.{AdditionalInformationId, Identifier}
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec

import scala.concurrent.ExecutionContext

class AlternativeContactInformationNavigatorSpec extends TestBaseSpec {

  val audit = mock[Audit]
  doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(any[HeaderCarrier], any[ExecutionContext])

  val navigator = new AdditionalInformationNavigator(audit)

  val aboutYou                     = Some(AboutYou(Some(CustomerDetails("Tobermory", ContactDetails("12345678909", "test@email.com")))))
  val aboutTheProperty             = Some(AboutTheProperty(None))
  val removeConnection             = Some(
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
  val additionalInformation        = Some(AdditionalInformation(Some(FurtherInformationOrRemarksDetails("test"))))
  val stillConnectedDetailsYes     = Some(StillConnectedDetails(Some(AddressConnectionTypeYes)))
  val sessionAdditionalInformation =
    Session(
      testUserLoginDetails,
      stillConnectedDetailsYes,
      removeConnection,
      aboutYou,
      aboutTheProperty,
      additionalInformation
    )

  "Connection to property navigator" when {

    "go to sign in from an identifier that doesn't exist in the route map" in {
      case object UnknownIdentifier extends Identifier
      navigator
        .nextPage(UnknownIdentifier)
        .apply(sessionAdditionalInformation) mustBe controllers.routes.LoginController.show()
    }

    "return a function that goes to alternative contact details page when about you has been completed" in {
      navigator
        .nextPage(AdditionalInformationId)
        .apply(
          sessionAdditionalInformation
        ) mustBe controllers.additionalinformation.routes.AlternativeContactDetailsController
        .show()
    }
  }
}
