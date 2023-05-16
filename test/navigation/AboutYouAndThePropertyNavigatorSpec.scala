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

import navigation.identifiers._
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import utils.TestBaseSpec

class AboutYouAndThePropertyNavigatorSpec extends TestBaseSpec {

  "About you and the property navigator for generic path" when {

    "return a function that goes to about the property when about you page has been completed" in {
      aboutYouAndThePropertyNavigator
        .nextPage(AboutYouPageId, aboutYouAndTheProperty6010YesSession)
        .apply(
          aboutYouAndTheProperty6010YesSession
        ) mustBe controllers.aboutyouandtheproperty.routes.AboutThePropertyController
        .show()
    }

    "return a function that goes to about the property website page when about the property page has been completed" in {
      aboutYouAndThePropertyNavigator
        .nextPage(AboutThePropertyPageId, aboutYouAndTheProperty6010YesSession)
        .apply(
          aboutYouAndTheProperty6010YesSession
        ) mustBe controllers.aboutyouandtheproperty.routes.WebsiteForPropertyController
        .show()
    }

    "return a function that goes to task list page when CYA page has been completed" in {
      aboutYouAndThePropertyNavigator
        .nextPage(CheckYourAnswersAboutThePropertyPageId, aboutYouAndTheProperty6010YesSession)
        .apply(
          aboutYouAndTheProperty6010YesSession
        ) mustBe controllers.routes.TaskListController
        .show()
    }
  }
}
