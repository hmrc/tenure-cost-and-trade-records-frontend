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

class AboutYouAndTheProperty6015_6016NavigatorSpec extends TestBaseSpec {

  "About you and the property navigator for no answers for 6015" when {

    // See AboutYouAndThePropertyNavigatorSpec for generic parts of the journey

    "return a function that goes to licence granted page when about the property website page has been completed" in {
      aboutYouAndThePropertyNavigator
        .nextPage(WebsiteForPropertyPageId, aboutYouAndTheProperty6015NoSession)
        .apply(
          aboutYouAndTheProperty6015NoSession
        ) mustBe controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedController
        .show()
    }

    "return a function that goes to CYA page when licence granted page has been completed no" in {
      aboutYouAndThePropertyNavigator
        .nextPage(PremisesLicenseGrantedId, aboutYouAndTheProperty6015NoSession)
        .apply(
          aboutYouAndTheProperty6015NoSession
        ) mustBe controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController
        .show()
    }
  }

  "About you and the property navigator for yes answers for 6015" when {

    "return a function that goes to licence granted page when about the property website page has been completed" in {
      aboutYouAndThePropertyNavigator
        .nextPage(WebsiteForPropertyPageId, aboutYouAndTheProperty6015YesSession)
        .apply(
          aboutYouAndTheProperty6015YesSession
        ) mustBe controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedController
        .show()
    }

    "return a function that goes to licence granted details page when licence granted page has been completed yes" in {
      aboutYouAndThePropertyNavigator
        .nextPage(PremisesLicenseGrantedId, aboutYouAndTheProperty6015YesSession)
        .apply(
          aboutYouAndTheProperty6015YesSession
        ) mustBe controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedDetailsController
        .show()
    }

    "return a function that goes to CYA page when licence granted details page has been completed" in {
      aboutYouAndThePropertyNavigator
        .nextPage(PremisesLicenseGrantedDetailsId, aboutYouAndTheProperty6015YesSession)
        .apply(
          aboutYouAndTheProperty6015YesSession
        ) mustBe controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController
        .show()
    }
  }

  "About you and the property navigator for no answers for 6016" when {

    // See AboutYouAndThePropertyNavigatorSpec for generic parts of the journey

    "return a function that goes to licence granted page when about the property website page has been completed" in {
      aboutYouAndThePropertyNavigator
        .nextPage(WebsiteForPropertyPageId, aboutYouAndTheProperty6016NoSession)
        .apply(
          aboutYouAndTheProperty6016NoSession
        ) mustBe controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedController
        .show()
    }

    "return a function that goes to CYA page when licence granted page has been completed no" in {
      aboutYouAndThePropertyNavigator
        .nextPage(PremisesLicenseGrantedId, aboutYouAndTheProperty6016NoSession)
        .apply(
          aboutYouAndTheProperty6016NoSession
        ) mustBe controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController
        .show()
    }
  }

  "About you and the property navigator for yes answers for 6016" when {

    "return a function that goes to licence granted page when about the property website page has been completed" in {
      aboutYouAndThePropertyNavigator
        .nextPage(WebsiteForPropertyPageId, aboutYouAndTheProperty6016NoSession)
        .apply(
          aboutYouAndTheProperty6016YesSession
        ) mustBe controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedController
        .show()
    }

    "return a function that goes to licence granted details page when licence granted page has been completed yes" in {
      aboutYouAndThePropertyNavigator
        .nextPage(PremisesLicenseGrantedId, aboutYouAndTheProperty6016NoSession)
        .apply(
          aboutYouAndTheProperty6016YesSession
        ) mustBe controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedDetailsController
        .show()
    }

    "return a function that goes to CYA page when licence granted details page has been completed" in {
      aboutYouAndThePropertyNavigator
        .nextPage(PremisesLicenseGrantedDetailsId, aboutYouAndTheProperty6016NoSession)
        .apply(
          aboutYouAndTheProperty6016YesSession
        ) mustBe controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController
        .show()
    }
  }
}
