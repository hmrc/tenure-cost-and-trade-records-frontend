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

class AdditionalInformationNavigatorSpec extends TCTRAppSpec with InjectedNavigation:

  "Additional information navigator" should {
    "redirect to default page for identifier that doesn't exist in the route map" in {
      additionalInformationNavigator
        .nextPage(UnknownIdentifier, additionalInformationSession)
        .apply(additionalInformationSession) shouldBe controllers.routes.LoginController.show
    }

    "redirect to  CYA page when further information has been completed" in {
      additionalInformationNavigator
        .nextPage(FurtherInformationId, additionalInformationSession)
        .apply(
          additionalInformationSession
        ) shouldBe
        controllers.additionalinformation.routes.CheckYourAnswersAdditionalInformationController
          .show()
    }

    "redirect to task list page when CYA has been completed" in {
      additionalInformationNavigator
        .nextPage(CheckYourAnswersAdditionalInformationId, additionalInformationSession)
        .apply(
          additionalInformationSession
        ) shouldBe controllers.routes.TaskListController.show
    }

    "redirect to the CYA" in {
      val call = additionalInformationNavigator.cyaPage.get
      call shouldBe controllers.additionalinformation.routes.CheckYourAnswersAdditionalInformationController.show()
    }
  }
