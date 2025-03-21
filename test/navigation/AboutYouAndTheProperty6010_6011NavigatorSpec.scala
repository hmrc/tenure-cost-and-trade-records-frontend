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

class AboutYouAndTheProperty6010_6011NavigatorSpec extends TestBaseSpec {

  // This suite tests all generic paths based on form type 6010

  val audit = mock[Audit]
  doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(any[HeaderCarrier], any[ExecutionContext])

  val navigator = new AboutYouAndThePropertyNavigator(audit)

  "About you and the property navigator" when {

    "go to sign in from an identifier that doesn't exist in the route map" in {
      case object UnknownIdentifier extends Identifier
      navigator
        .nextPage(UnknownIdentifier, aboutYouAndTheProperty6010YesSession)
        .apply(aboutYouAndTheProperty6010YesSession) shouldBe controllers.routes.LoginController.show
    }

    "navigate correctly based on the page and session" should {

      "navigate to ContactDetailsQuestionController after completing AboutYouPage" in {
        navigator
          .nextPage(AboutYouPageId, aboutYouAndTheProperty6010YesSession)
          .apply(aboutYouAndTheProperty6010YesSession) shouldBe
          controllers.aboutyouandtheproperty.routes.ContactDetailsQuestionController.show()
      }

      "navigate to AlternativeContactDetailsController after completing ContactDetailsQuestion" in {
        navigator
          .nextPage(ContactDetailsQuestionId, aboutYouAndTheProperty6010YesSession)
          .apply(aboutYouAndTheProperty6010YesSession) shouldBe
          controllers.aboutyouandtheproperty.routes.AlternativeContactDetailsController.show()
      }

      "navigate to AboutThePropertyController after completing AlternativeContactDetails" in {
        navigator
          .nextPage(AlternativeContactDetailsId, aboutYouAndTheProperty6010YesSession)
          .apply(aboutYouAndTheProperty6010YesSession) shouldBe
          controllers.aboutyouandtheproperty.routes.AboutThePropertyController.show()
      }

      "navigate to WebsiteForPropertyController after completing AboutTheProperty" in {
        navigator
          .nextPage(AboutThePropertyPageId, aboutYouAndTheProperty6010YesSession)
          .apply(aboutYouAndTheProperty6010YesSession) shouldBe
          controllers.aboutyouandtheproperty.routes.WebsiteForPropertyController.show()
      }

      "navigate to LicensableActivitiesController after completing WebsiteForProperty" in {
        navigator
          .nextPage(WebsiteForPropertyPageId, aboutYouAndTheProperty6010YesSession)
          .apply(aboutYouAndTheProperty6010YesSession) shouldBe
          controllers.aboutyouandtheproperty.routes.LicensableActivitiesController.show()
      }

      "navigate to LicensableActivitiesDetailsController after completing LicensableActivity" in {
        navigator
          .nextPage(LicensableActivityPageId, aboutYouAndTheProperty6010YesSession)
          .apply(aboutYouAndTheProperty6010YesSession) shouldBe
          controllers.aboutyouandtheproperty.routes.LicensableActivitiesDetailsController.show()
      }

      "navigate to PremisesLicenseConditionsController after completing LicensableActivityDetails" in {
        navigator
          .nextPage(LicensableActivityDetailsPageId, aboutYouAndTheProperty6010YesSession)
          .apply(aboutYouAndTheProperty6010YesSession) shouldBe
          controllers.aboutyouandtheproperty.routes.PremisesLicenseConditionsController.show()
      }

      "navigate to TaskListController after completing CheckYourAnswersAboutTheProperty" in {
        navigator
          .nextPage(CheckYourAnswersAboutThePropertyPageId, aboutYouAndTheProperty6010YesSession)
          .apply(aboutYouAndTheProperty6010YesSession) shouldBe
          controllers.routes.TaskListController.show()
      }

      "navigate to AboutThePropertyController after completing ContactDetailsQuestion with no" in {
        navigator
          .nextPage(ContactDetailsQuestionId, aboutYouAndTheProperty6010NoSession)
          .apply(aboutYouAndTheProperty6010NoSession) shouldBe
          controllers.aboutyouandtheproperty.routes.AboutThePropertyController.show()
      }

      "navigate to LicensableActivitiesController after completing WebsiteForProperty with no" in {
        navigator
          .nextPage(WebsiteForPropertyPageId, aboutYouAndTheProperty6010NoSession)
          .apply(aboutYouAndTheProperty6010NoSession) shouldBe
          controllers.aboutyouandtheproperty.routes.LicensableActivitiesController.show()
      }

      "navigate to PremisesLicenseConditionsController after completing LicensableActivity with no" in {
        navigator
          .nextPage(LicensableActivityPageId, aboutYouAndTheProperty6010NoSession)
          .apply(aboutYouAndTheProperty6010NoSession) shouldBe
          controllers.aboutyouandtheproperty.routes.PremisesLicenseConditionsController.show()
      }

      "navigate to EnforcementActionBeenTakenController after completing PremisesLicenceConditions with no" in {
        navigator
          .nextPage(PremisesLicenceConditionsPageId, aboutYouAndTheProperty6010NoSession)
          .apply(aboutYouAndTheProperty6010NoSession) shouldBe
          controllers.aboutyouandtheproperty.routes.EnforcementActionBeenTakenController.show()
      }

      "navigate to TiedForGoodsController after completing EnforcementActionBeenTaken with no" in {
        navigator
          .nextPage(EnforcementActionBeenTakenPageId, aboutYouAndTheProperty6010NoSession)
          .apply(aboutYouAndTheProperty6010NoSession) shouldBe
          controllers.aboutyouandtheproperty.routes.TiedForGoodsController.show()
      }

      "navigate to CheckYourAnswersAboutThePropertyController after completing TiedForGoods with no" in {
        navigator
          .nextPage(TiedForGoodsPageId, aboutYouAndTheProperty6010NoSession)
          .apply(aboutYouAndTheProperty6010NoSession) shouldBe
          controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
      }

      "navigate to PremisesLicenseConditionsDetailsController after completing PremisesLicenceConditions with yes" in {
        navigator
          .nextPage(PremisesLicenceConditionsPageId, aboutYouAndTheProperty6010YesSession)
          .apply(aboutYouAndTheProperty6010YesSession) shouldBe
          controllers.aboutyouandtheproperty.routes.PremisesLicenseConditionsDetailsController.show()
      }

      "navigate to EnforcementActionBeenTakenController after completing PremisesLicenceConditionsDetails" in {
        navigator
          .nextPage(PremisesLicenceConditionsDetailsPageId, aboutYouAndTheProperty6010YesSession)
          .apply(aboutYouAndTheProperty6010YesSession) shouldBe
          controllers.aboutyouandtheproperty.routes.EnforcementActionBeenTakenController.show()
      }

      "navigate to EnforcementActionBeenTakenDetailsController after completing EnforcementActionBeenTaken with yes" in {
        navigator
          .nextPage(EnforcementActionBeenTakenPageId, aboutYouAndTheProperty6010YesSession)
          .apply(aboutYouAndTheProperty6010YesSession) shouldBe
          controllers.aboutyouandtheproperty.routes.EnforcementActionBeenTakenDetailsController.show()
      }

      "navigate to TiedForGoodsController after completing EnforcementActionBeenTakenDetails" in {
        navigator
          .nextPage(EnforcementActionBeenTakenDetailsPageId, aboutYouAndTheProperty6010YesSession)
          .apply(aboutYouAndTheProperty6010YesSession) shouldBe
          controllers.aboutyouandtheproperty.routes.TiedForGoodsController.show()
      }

      "navigate to LicensableActivitiesController after completing PremisesLicenseGrantedDetails" in {
        navigator
          .nextPage(PremisesLicenseGrantedDetailsId, aboutYouAndTheProperty6010YesSession)
          .apply(aboutYouAndTheProperty6010YesSession) shouldBe
          controllers.aboutyouandtheproperty.routes.LicensableActivitiesController.show()
      }

      "navigate to TiedForGoodsDetailsController after completing TiedForGoods with yes" in {
        navigator
          .nextPage(TiedForGoodsPageId, aboutYouAndTheProperty6010YesSession)
          .apply(aboutYouAndTheProperty6010YesSession) shouldBe
          controllers.aboutyouandtheproperty.routes.TiedForGoodsDetailsController.show()
      }

      "navigate to CheckYourAnswersAboutThePropertyController after completing TiedForGoodsDetails" in {
        navigator
          .nextPage(TiedForGoodsDetailsPageId, aboutYouAndTheProperty6010YesSession)
          .apply(aboutYouAndTheProperty6010YesSession) shouldBe
          controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
      }
    }
  }
}
