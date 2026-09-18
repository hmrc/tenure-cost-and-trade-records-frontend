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
import models.submissions.aboutyouandtheproperty.{AboutYouAndTheProperty, AboutYouAndThePropertyPartTwo}
import models.submissions.additionalinformation.AdditionalInformation
import models.submissions.common.ContactDetails
import models.submissions.connectiontoproperty.AddressConnectionType.*
import models.submissions.connectiontoproperty.StillConnectedDetails
import models.submissions.notconnected.{RemoveConnectionDetails, RemoveConnectionsDetails}
import navigation.identifiers.{PastConnectionId, RemoveConnectionId}
import test.{InjectedNavigation, TCTRAppSpec}

import scala.language.implicitConversions

class RemoveConnectionNavigatorSpec extends TCTRAppSpec with InjectedNavigation:

  private val aboutTheProperty: Option[AboutYouAndTheProperty]               = AboutYouAndTheProperty()
  private val aboutThePropertyPartTwo: Option[AboutYouAndThePropertyPartTwo] = AboutYouAndThePropertyPartTwo()

  private val removeConnection: Option[RemoveConnectionDetails] =
    RemoveConnectionDetails(
      RemoveConnectionsDetails(
        "John Smith",
        ContactDetails("12345678909", "test@email.com"),
        "Additional Information is here"
      )
    )

  private val additionalInformation: Option[AdditionalInformation] = AdditionalInformation("test")

  private val stillConnectedDetailsYes: Option[StillConnectedDetails] = StillConnectedDetails(AddressConnectionTypeYes)

  private val sessionAdditionalInformation: Session =
    Session(
      "99996010004",
      FOR6010,
      prefilledAddress,
      "Basic OTk5OTYwMTAwMDQ6U2Vuc2l0aXZlKC4uLik=",
      isWelsh = false,
      stillConnectedDetailsYes,
      removeConnection,
      aboutTheProperty,
      aboutThePropertyPartTwo,
      additionalInformation
    )

  "Remove connection navigator" should {
    "redirect to default page for identifier that doesn't exist in the route map" in {
      removeConnectionNavigator
        .nextPage(UnknownIdentifier, sessionAdditionalInformation)
        .apply(sessionAdditionalInformation) shouldBe controllers.routes.LoginController.show
    }

    "redirect to remove connection page when past connection has been completed" in {
      removeConnectionNavigator
        .nextPage(PastConnectionId, sessionAdditionalInformation)
        .apply(
          sessionAdditionalInformation
        ) shouldBe
        controllers.notconnected.routes.RemoveConnectionController
          .show()
    }

    "redirect to CYA page when remove connection has been completed" in {
      removeConnectionNavigator
        .nextPage(RemoveConnectionId, sessionAdditionalInformation)
        .apply(
          sessionAdditionalInformation
        ) shouldBe
        controllers.notconnected.routes.CheckYourAnswersNotConnectedController
          .show()
    }

    "redirect to the CYA" in {
      val call = removeConnectionNavigator.cyaPage.get
      call shouldBe controllers.notconnected.routes.CheckYourAnswersNotConnectedController.show()
    }
  }
