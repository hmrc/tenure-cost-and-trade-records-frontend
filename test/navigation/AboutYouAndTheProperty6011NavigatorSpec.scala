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

class AboutYouAndTheProperty6011NavigatorSpec extends TestBaseSpec {

  "About you and the property navigator for no answers for 6011" when {

    // See AboutYouAndThePropertyNavigatorSpec for generic parts of the journey

    "return a function that goes to licence activity page when about the property website page has been completed" in {
      aboutYouAndThePropertyNavigator
        .nextPage(WebsiteForPropertyPageId, aboutYouAndTheProperty6011NoSession)
        .apply(
          aboutYouAndTheProperty6011NoSession
        ) mustBe controllers.aboutyouandtheproperty.routes.LicensableActivitiesController
        .show()
    }

    "return a function that goes to property licence conditions page when licence activity page has been completed no" in {
      aboutYouAndThePropertyNavigator
        .nextPage(LicensableActivityPageId, aboutYouAndTheProperty6011NoSession)
        .apply(
          aboutYouAndTheProperty6011NoSession
        ) mustBe controllers.aboutyouandtheproperty.routes.PremisesLicenseConditionsController
        .show()
    }

    "return a function that goes to enforcement action taken page when property licence conditions page has been completed no" in {
      aboutYouAndThePropertyNavigator
        .nextPage(PremisesLicenceConditionsPageId, aboutYouAndTheProperty6011NoSession)
        .apply(
          aboutYouAndTheProperty6011NoSession
        ) mustBe controllers.aboutyouandtheproperty.routes.EnforcementActionBeenTakenController
        .show()
    }

    "return a function that goes to CYA page when enforcement action taken page has been completed with no" in {
      aboutYouAndThePropertyNavigator
        .nextPage(EnforcementActionBeenTakenPageId, aboutYouAndTheProperty6011NoSession)
        .apply(
          aboutYouAndTheProperty6011NoSession
        ) mustBe controllers.aboutyouandtheproperty.routes.TiedForGoodsController
        .show()
    }
  }

  "About you and the property navigator for yes answers for 6010" when {

    "return a function that goes to licence activity page when about the property website page has been completed" in {
      aboutYouAndThePropertyNavigator
        .nextPage(WebsiteForPropertyPageId, aboutYouAndTheProperty6011YesSession)
        .apply(
          aboutYouAndTheProperty6011YesSession
        ) mustBe controllers.aboutyouandtheproperty.routes.LicensableActivitiesController
        .show()
    }

    "return a function that goes to licence activity details page when licence activity page has been completed yes" in {
      aboutYouAndThePropertyNavigator
        .nextPage(LicensableActivityPageId, aboutYouAndTheProperty6011YesSession)
        .apply(
          aboutYouAndTheProperty6011YesSession
        ) mustBe controllers.aboutyouandtheproperty.routes.LicensableActivitiesDetailsController
        .show()
    }

    "return a function that goes to premises license conditions page when licence activity details page has been completed" in {
      aboutYouAndThePropertyNavigator
        .nextPage(LicensableActivityDetailsPageId, aboutYouAndTheProperty6011YesSession)
        .apply(
          aboutYouAndTheProperty6011YesSession
        ) mustBe controllers.aboutyouandtheproperty.routes.PremisesLicenseConditionsController
        .show()
    }

    "return a function that goes to premises license conditions details page when property licence conditions page has been completed yes" in {
      aboutYouAndThePropertyNavigator
        .nextPage(PremisesLicenceConditionsPageId, aboutYouAndTheProperty6011YesSession)
        .apply(
          aboutYouAndTheProperty6011YesSession
        ) mustBe controllers.aboutyouandtheproperty.routes.PremisesLicenseConditionsDetailsController.show()
    }

    "return a function that goes to enforcement action taken page when premises license conditions details page has been completed" in {
      aboutYouAndThePropertyNavigator
        .nextPage(PremisesLicenceConditionsDetailsPageId, aboutYouAndTheProperty6011YesSession)
        .apply(
          aboutYouAndTheProperty6011YesSession
        ) mustBe controllers.aboutyouandtheproperty.routes.EnforcementActionBeenTakenController
        .show()
    }

    "return a function that goes to enforcement action taken details page when enforcement action taken page has been completed with yes" in {
      aboutYouAndThePropertyNavigator
        .nextPage(EnforcementActionBeenTakenPageId, aboutYouAndTheProperty6011YesSession)
        .apply(
          aboutYouAndTheProperty6011YesSession
        ) mustBe controllers.aboutyouandtheproperty.routes.EnforcementActionBeenTakenDetailsController.show()
    }

    "return a function that goes to CYA page when enforcement action taken details page has been completed" in {
      aboutYouAndThePropertyNavigator
        .nextPage(EnforcementActionBeenTakenDetailsPageId, aboutYouAndTheProperty6011YesSession)
        .apply(
          aboutYouAndTheProperty6011YesSession
        ) mustBe controllers.aboutyouandtheproperty.routes.TiedForGoodsController.show()
    }
  }
}
