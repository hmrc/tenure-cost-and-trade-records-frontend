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
import navigation.identifiers._
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec

import scala.concurrent.ExecutionContext

class AboutYouAndTheProperty6010NavigatorSpec extends TestBaseSpec {

  val audit = mock[Audit]
  doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(any[HeaderCarrier], any[ExecutionContext])

  val navigator = new AboutYouAndThePropertyNavigator(audit)

  "About you and the property navigator for no answers for 6010" when {

    "go to sign in from an identifier that doesn't exist in the route map" in {
      case object UnknownIdentifier extends Identifier
      navigator
        .nextPage(UnknownIdentifier, aboutYouAndTheProperty6010YesSession)
        .apply(aboutYouAndTheProperty6010YesSession) shouldBe controllers.routes.LoginController.show
    }

    // See AboutYouAndThePropertyNavigatorSpec for generic parts of the journey

    "return a function that goes to about the property contact details page when no is answered for contact details and has been completed" in {
      aboutYouAndThePropertyNavigator
        .nextPage(ContactDetailsQuestionId, aboutYouAndTheProperty6010NoSession)
        .apply(
          aboutYouAndTheProperty6010NoSession
        ) shouldBe controllers.aboutyouandtheproperty.routes.AboutThePropertyController
        .show()
    }

    "return a function that goes to licence activity page when about the property website page has been completed" in {
      aboutYouAndThePropertyNavigator
        .nextPage(WebsiteForPropertyPageId, aboutYouAndTheProperty6010NoSession)
        .apply(
          aboutYouAndTheProperty6010NoSession
        ) shouldBe controllers.aboutyouandtheproperty.routes.LicensableActivitiesController
        .show()
    }

    "return a function that goes to property licence conditions page when licence activity page has been completed no" in {
      aboutYouAndThePropertyNavigator
        .nextPage(LicensableActivityPageId, aboutYouAndTheProperty6010NoSession)
        .apply(
          aboutYouAndTheProperty6010NoSession
        ) shouldBe controllers.aboutyouandtheproperty.routes.PremisesLicenseConditionsController
        .show()
    }

    "return a function that goes to enforcement action taken page when property licence conditions page has been completed no" in {
      aboutYouAndThePropertyNavigator
        .nextPage(PremisesLicenceConditionsPageId, aboutYouAndTheProperty6010NoSession)
        .apply(
          aboutYouAndTheProperty6010NoSession
        ) shouldBe controllers.aboutyouandtheproperty.routes.EnforcementActionBeenTakenController
        .show()
    }

    "return a function that goes to tied for goods page when enforcement action taken page has been completed with no" in {
      aboutYouAndThePropertyNavigator
        .nextPage(EnforcementActionBeenTakenPageId, aboutYouAndTheProperty6010NoSession)
        .apply(
          aboutYouAndTheProperty6010NoSession
        ) shouldBe controllers.aboutyouandtheproperty.routes.TiedForGoodsController
        .show()
    }

    "return a function that goes to about the check answers page when tied for goods page has been completed with no" in {
      aboutYouAndThePropertyNavigator
        .nextPage(TiedForGoodsPageId, aboutYouAndTheProperty6010NoSession)
        .apply(
          aboutYouAndTheProperty6010NoSession
        ) shouldBe controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController
        .show()
    }
  }

  "About you and the property navigator for yes answers for 6010" when {

    "return a function that goes to licence activity page when about the property website page has been completed" in {
      aboutYouAndThePropertyNavigator
        .nextPage(WebsiteForPropertyPageId, aboutYouAndTheProperty6010YesSession)
        .apply(
          aboutYouAndTheProperty6010YesSession
        ) shouldBe controllers.aboutyouandtheproperty.routes.LicensableActivitiesController
        .show()
    }

    "return a function that goes to licence activity details page when licence activity page has been completed yes" in {
      aboutYouAndThePropertyNavigator
        .nextPage(LicensableActivityPageId, aboutYouAndTheProperty6010YesSession)
        .apply(
          aboutYouAndTheProperty6010YesSession
        ) shouldBe controllers.aboutyouandtheproperty.routes.LicensableActivitiesDetailsController
        .show()
    }

    "return a function that goes to premises license conditions conditions page when licence activity details page has been completed" in {
      aboutYouAndThePropertyNavigator
        .nextPage(LicensableActivityDetailsPageId, aboutYouAndTheProperty6010YesSession)
        .apply(
          aboutYouAndTheProperty6010YesSession
        ) shouldBe controllers.aboutyouandtheproperty.routes.PremisesLicenseConditionsController
        .show()
    }

    "return a function that goes to premises license conditions details page when property licence conditions page has been completed yes" in {
      aboutYouAndThePropertyNavigator
        .nextPage(PremisesLicenceConditionsPageId, aboutYouAndTheProperty6010YesSession)
        .apply(
          aboutYouAndTheProperty6010YesSession
        ) shouldBe controllers.aboutyouandtheproperty.routes.PremisesLicenseConditionsDetailsController
        .show()
    }

    "return a function that goes to enforcement action taken page when premises license conditions details page has been completed" in {
      aboutYouAndThePropertyNavigator
        .nextPage(PremisesLicenceConditionsDetailsPageId, aboutYouAndTheProperty6010YesSession)
        .apply(
          aboutYouAndTheProperty6010YesSession
        ) shouldBe controllers.aboutyouandtheproperty.routes.EnforcementActionBeenTakenController
        .show()
    }

    "return a function that goes to enforcement action taken details page when enforcement action taken page has been completed with yes" in {
      aboutYouAndThePropertyNavigator
        .nextPage(EnforcementActionBeenTakenPageId, aboutYouAndTheProperty6010YesSession)
        .apply(
          aboutYouAndTheProperty6010YesSession
        ) shouldBe controllers.aboutyouandtheproperty.routes.EnforcementActionBeenTakenDetailsController.show()
    }

    "return a function that goes to tied for goods page when enforcement action taken details page has been completed" in {
      aboutYouAndThePropertyNavigator
        .nextPage(EnforcementActionBeenTakenDetailsPageId, aboutYouAndTheProperty6010YesSession)
        .apply(
          aboutYouAndTheProperty6010YesSession
        ) shouldBe controllers.aboutyouandtheproperty.routes.TiedForGoodsController
        .show()
    }

    "return a function that goes to CYA page when licence granted details page has been completed" in {
      aboutYouAndThePropertyNavigator
        .nextPage(PremisesLicenseGrantedDetailsId, aboutYouAndTheProperty6010YesSession)
        .apply(
          aboutYouAndTheProperty6010YesSession
        ) shouldBe controllers.aboutyouandtheproperty.routes.LicensableActivitiesController
        .show()
    }

    "return a function that goes to tied for goods details page when tied for goods page has been completed with yes" in {
      aboutYouAndThePropertyNavigator
        .nextPage(TiedForGoodsPageId, aboutYouAndTheProperty6010YesSession)
        .apply(
          aboutYouAndTheProperty6010YesSession
        ) shouldBe controllers.aboutyouandtheproperty.routes.TiedForGoodsDetailsController
        .show()
    }

    "return a function that goes to CYA page when tied for goods details page has been completed" in {
      aboutYouAndThePropertyNavigator
        .nextPage(TiedForGoodsDetailsPageId, aboutYouAndTheProperty6010YesSession)
        .apply(
          aboutYouAndTheProperty6010YesSession
        ) shouldBe controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
    }
  }

}
