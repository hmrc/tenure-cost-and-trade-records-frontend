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
import utils.TestBaseSpec

class AboutYouAndTheProperty6015_6016NavigatorSpec extends TestBaseSpec {

  val navigator = aboutYouAndThePropertyNavigator

  "About you and the property navigator for 6015" should {

    "navigate to PremisesLicenseGrantedDetailsController after completing PremisesLicenseGranted with yes" in {
      navigator
        .nextPage(PremisesLicenseGrantedId, aboutYouAndTheProperty6015YesSession)
        .apply(aboutYouAndTheProperty6015YesSession) shouldBe
        controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedDetailsController.show()
    }

    "navigate to CheckYourAnswersAboutThePropertyController after completing PremisesLicenseGrantedDetails with yes" in {
      navigator
        .nextPage(PremisesLicenseGrantedDetailsId, aboutYouAndTheProperty6015YesSession)
        .apply(aboutYouAndTheProperty6015YesSession) shouldBe
        controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
    }

    "navigate to PremisesLicenseGrantedController after completing WebsiteForProperty with no" in {
      navigator
        .nextPage(WebsiteForPropertyPageId, aboutYouAndTheProperty6016NoSession)
        .apply(aboutYouAndTheProperty6016NoSession) shouldBe
        controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedController.show()
    }

    "navigate to CheckYourAnswersAboutThePropertyController after completing PremisesLicenseGranted with no" in {
      navigator
        .nextPage(PremisesLicenseGrantedId, aboutYouAndTheProperty6016NoSession)
        .apply(aboutYouAndTheProperty6016NoSession) shouldBe
        controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
    }

    "navigate to PremisesLicenseGrantedController after completing WebsiteForProperty" in {
      navigator
        .nextPage(WebsiteForPropertyPageId, aboutYouAndTheProperty6016YesSession)
        .apply(aboutYouAndTheProperty6016YesSession) shouldBe
        controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedController.show()
    }

    "navigate to PremisesLicenseGrantedDetailsController after completing PremisesLicenseGranted" in {
      navigator
        .nextPage(PremisesLicenseGrantedId, aboutYouAndTheProperty6016YesSession)
        .apply(aboutYouAndTheProperty6016YesSession) shouldBe
        controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedDetailsController.show()
    }
  }
}
