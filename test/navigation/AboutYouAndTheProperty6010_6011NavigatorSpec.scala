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

class AboutYouAndTheProperty6010_6011NavigatorSpec extends TCTRAppSpec with InjectedNavigation:

  "About you and the property navigator for 6010/6011" should {
    "redirect to default page for identifier that doesn't exist in the route map" in {
      aboutYouAndThePropertyNavigator
        .nextPage(UnknownIdentifier, aboutYouAndTheProperty6010YesSession)
        .apply(aboutYouAndTheProperty6010YesSession) shouldBe controllers.routes.LoginController.show
    }

    "redirect to ContactDetailsQuestionController after completing AboutYouPage" in {
      aboutYouAndThePropertyNavigator
        .nextPage(AboutYouPageId, aboutYouAndTheProperty6010YesSession)
        .apply(aboutYouAndTheProperty6010YesSession) shouldBe
        controllers.aboutyouandtheproperty.routes.ContactDetailsQuestionController.show()
    }

    "redirect to AlternativeContactDetailsController after completing ContactDetailsQuestion" in {
      aboutYouAndThePropertyNavigator
        .nextPage(ContactDetailsQuestionId, aboutYouAndTheProperty6010YesSession)
        .apply(aboutYouAndTheProperty6010YesSession) shouldBe
        controllers.aboutyouandtheproperty.routes.AboutThePropertyController.show()
    }

    "redirect to WebsiteForPropertyController after completing AboutTheProperty" in {
      aboutYouAndThePropertyNavigator
        .nextPage(AboutThePropertyPageId, aboutYouAndTheProperty6010YesSession)
        .apply(aboutYouAndTheProperty6010YesSession) shouldBe
        controllers.aboutyouandtheproperty.routes.WebsiteForPropertyController.show()
    }

    "redirect to LicensableActivitiesController after completing WebsiteForProperty" in {
      aboutYouAndThePropertyNavigator
        .nextPage(WebsiteForPropertyPageId, aboutYouAndTheProperty6010YesSession)
        .apply(aboutYouAndTheProperty6010YesSession) shouldBe
        controllers.aboutyouandtheproperty.routes.LicensableActivitiesController.show()
    }

    "redirect to LicensableActivitiesDetailsController after completing LicensableActivity" in {
      aboutYouAndThePropertyNavigator
        .nextPage(LicensableActivityPageId, aboutYouAndTheProperty6010YesSession)
        .apply(aboutYouAndTheProperty6010YesSession) shouldBe
        controllers.aboutyouandtheproperty.routes.LicensableActivitiesDetailsController.show()
    }

    "redirect to PremisesLicenseConditionsController after completing LicensableActivityDetails" in {
      aboutYouAndThePropertyNavigator
        .nextPage(LicensableActivityDetailsPageId, aboutYouAndTheProperty6010YesSession)
        .apply(aboutYouAndTheProperty6010YesSession) shouldBe
        controllers.aboutyouandtheproperty.routes.PremisesLicenseConditionsController.show()
    }

    "redirect to TaskListController after completing CheckYourAnswersAboutTheProperty" in {
      aboutYouAndThePropertyNavigator
        .nextPage(CheckYourAnswersAboutThePropertyPageId, aboutYouAndTheProperty6010YesSession)
        .apply(aboutYouAndTheProperty6010YesSession) shouldBe
        controllers.routes.TaskListController.show
    }

    "redirect to AboutThePropertyController after completing ContactDetailsQuestion with no" in {
      aboutYouAndThePropertyNavigator
        .nextPage(ContactDetailsQuestionId, aboutYouAndTheProperty6010NoSession)
        .apply(aboutYouAndTheProperty6010NoSession) shouldBe
        controllers.aboutyouandtheproperty.routes.AboutThePropertyController.show()
    }

    "redirect to LicensableActivitiesController after completing WebsiteForProperty with no" in {
      aboutYouAndThePropertyNavigator
        .nextPage(WebsiteForPropertyPageId, aboutYouAndTheProperty6010NoSession)
        .apply(aboutYouAndTheProperty6010NoSession) shouldBe
        controllers.aboutyouandtheproperty.routes.LicensableActivitiesController.show()
    }

    "redirect to PremisesLicenseConditionsController after completing LicensableActivity with no" in {
      aboutYouAndThePropertyNavigator
        .nextPage(LicensableActivityPageId, aboutYouAndTheProperty6010NoSession)
        .apply(aboutYouAndTheProperty6010NoSession) shouldBe
        controllers.aboutyouandtheproperty.routes.PremisesLicenseConditionsController.show()
    }

    "redirect to EnforcementActionBeenTakenController after completing PremisesLicenceConditions with no" in {
      aboutYouAndThePropertyNavigator
        .nextPage(PremisesLicenceConditionsPageId, aboutYouAndTheProperty6010NoSession)
        .apply(aboutYouAndTheProperty6010NoSession) shouldBe
        controllers.aboutyouandtheproperty.routes.EnforcementActionBeenTakenController.show()
    }

    "redirect to TiedForGoodsController after completing EnforcementActionBeenTaken with no" in {
      aboutYouAndThePropertyNavigator
        .nextPage(EnforcementActionBeenTakenPageId, aboutYouAndTheProperty6010NoSession)
        .apply(aboutYouAndTheProperty6010NoSession) shouldBe
        controllers.aboutyouandtheproperty.routes.TiedForGoodsController.show()
    }

    "redirect to CheckYourAnswersAboutThePropertyController after completing TiedForGoods with no" in {
      aboutYouAndThePropertyNavigator
        .nextPage(TiedForGoodsPageId, aboutYouAndTheProperty6010NoSession)
        .apply(aboutYouAndTheProperty6010NoSession) shouldBe
        controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
    }

    "redirect to PremisesLicenseConditionsDetailsController after completing PremisesLicenceConditions with yes" in {
      aboutYouAndThePropertyNavigator
        .nextPage(PremisesLicenceConditionsPageId, aboutYouAndTheProperty6010YesSession)
        .apply(aboutYouAndTheProperty6010YesSession) shouldBe
        controllers.aboutyouandtheproperty.routes.PremisesLicenseConditionsDetailsController.show()
    }

    "redirect to EnforcementActionBeenTakenController after completing PremisesLicenceConditionsDetails" in {
      aboutYouAndThePropertyNavigator
        .nextPage(PremisesLicenceConditionsDetailsPageId, aboutYouAndTheProperty6010YesSession)
        .apply(aboutYouAndTheProperty6010YesSession) shouldBe
        controllers.aboutyouandtheproperty.routes.EnforcementActionBeenTakenController.show()
    }

    "redirect to EnforcementActionBeenTakenDetailsController after completing EnforcementActionBeenTaken with yes" in {
      aboutYouAndThePropertyNavigator
        .nextPage(EnforcementActionBeenTakenPageId, aboutYouAndTheProperty6010YesSession)
        .apply(aboutYouAndTheProperty6010YesSession) shouldBe
        controllers.aboutyouandtheproperty.routes.EnforcementActionBeenTakenDetailsController.show()
    }

    "redirect to TiedForGoodsController after completing EnforcementActionBeenTakenDetails" in {
      aboutYouAndThePropertyNavigator
        .nextPage(EnforcementActionBeenTakenDetailsPageId, aboutYouAndTheProperty6010YesSession)
        .apply(aboutYouAndTheProperty6010YesSession) shouldBe
        controllers.aboutyouandtheproperty.routes.TiedForGoodsController.show()
    }

    "redirect to LicensableActivitiesController after completing PremisesLicenseGrantedDetails" in {
      aboutYouAndThePropertyNavigator
        .nextPage(PremisesLicenseGrantedDetailsId, aboutYouAndTheProperty6010YesSession)
        .apply(aboutYouAndTheProperty6010YesSession) shouldBe
        controllers.aboutyouandtheproperty.routes.LicensableActivitiesController.show()
    }

    "redirect to TiedForGoodsDetailsController after completing TiedForGoods with yes" in {
      aboutYouAndThePropertyNavigator
        .nextPage(TiedForGoodsPageId, aboutYouAndTheProperty6010YesSession)
        .apply(aboutYouAndTheProperty6010YesSession) shouldBe
        controllers.aboutyouandtheproperty.routes.TiedForGoodsDetailsController.show()
    }

    "redirect to CheckYourAnswersAboutThePropertyController after completing TiedForGoodsDetails" in {
      aboutYouAndThePropertyNavigator
        .nextPage(TiedForGoodsDetailsPageId, aboutYouAndTheProperty6010YesSession)
        .apply(aboutYouAndTheProperty6010YesSession) shouldBe
        controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
    }
  }
