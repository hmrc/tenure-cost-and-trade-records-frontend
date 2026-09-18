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

import navigation.identifiers.*
import test.{InjectedNavigation, TCTRAppSpec}

class RequestReferenceNumberNavigatorSpec extends TCTRAppSpec with InjectedNavigation:

  "RequestReferenceNumber navigator" should {
    "redirect to default page for identifier that doesn't exist in the route map" in {
      requestReferenceNumberNavigator
        .nextPage(UnknownIdentifier, stillConnectedDetailsYesSession)
        .apply(stillConnectedDetailsYesSession) shouldBe controllers.routes.LoginController.show
    }

    "redirect to RequestReferenceNumberContactDetailsController from NoReferenceNumberPageId" in {
      requestReferenceNumberNavigator
        .nextPage(NoReferenceNumberPageId, stillConnectedDetailsYesSession)
        .apply(
          stillConnectedDetailsYesSession
        ) shouldBe
        controllers.requestReferenceNumber.routes.RequestReferenceNumberContactDetailsController
          .show()
    }

    "redirect to RequestReferenceNumberCheckYourAnswersController from NoReferenceNumberContactDetailsPageId" in {
      requestReferenceNumberNavigator
        .nextPage(NoReferenceNumberContactDetailsPageId, stillConnectedDetailsYesSession)
        .apply(
          stillConnectedDetailsYesSession
        ) shouldBe
        controllers.requestReferenceNumber.routes.RequestReferenceNumberCheckYourAnswersController
          .show()
    }

    "redirect to request reference number confirmation from CheckYourAnswersRequestReferenceNumberPageId" in {
      requestReferenceNumberNavigator
        .nextPage(CheckYourAnswersRequestReferenceNumberPageId, stillConnectedDetailsYesSession)
        .apply(
          stillConnectedDetailsYesSession
        ) shouldBe
        controllers.requestReferenceNumber.routes.RequestReferenceNumberCheckYourAnswersController
          .confirmation()
    }

    "redirect to the CYA" in {
      val call = requestReferenceNumberNavigator.cyaPage.get
      call shouldBe controllers.requestReferenceNumber.routes.RequestReferenceNumberCheckYourAnswersController.show()
    }
  }
