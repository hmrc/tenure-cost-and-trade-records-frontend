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
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec

import scala.concurrent.ExecutionContext

class AboutYouAndThePropertyNavigatorSpec extends TestBaseSpec {

  val audit = mock[Audit]
  doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(any[HeaderCarrier], any[ExecutionContext])

  val navigator = new AdditionalInformationNavigator(audit)

  "About you and the property navigator for generic path" when {

    "go to sign in from an identifier that doesn't exist in the route map" in {
      case object UnknownIdentifier extends Identifier
      navigator
        .nextPage(UnknownIdentifier, aboutYouAndTheProperty6010YesSession)
        .apply(aboutYouAndTheProperty6010YesSession) mustBe controllers.routes.LoginController.show
    }

    "return a function that goes to about the property when about you page has been completed" in {
      aboutYouAndThePropertyNavigator
        .nextPage(AboutYouPageId, aboutYouAndTheProperty6010YesSession)
        .apply(
          aboutYouAndTheProperty6010YesSession
        ) mustBe controllers.aboutyouandtheproperty.routes.ContactDetailsQuestionController
        .show()
    }

    "return a function that goes to alternative contact page when contact details questions have been completed" in {
      aboutYouAndThePropertyNavigator
        .nextPage(ContactDetailsQuestionId, aboutYouAndTheProperty6010YesSession)
        .apply(
          aboutYouAndTheProperty6010YesSession
        ) mustBe controllers.aboutyouandtheproperty.routes.AlternativeContactDetailsController
        .show()
    }

    "return a function that goes to about the property page when alternative contact details have been completed" in {
      aboutYouAndThePropertyNavigator
        .nextPage(AlternativeContactDetailsId, aboutYouAndTheProperty6010YesSession)
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

    "return a function that goes to about the property licensable activities page when about the property website has been completed" in {
      aboutYouAndThePropertyNavigator
        .nextPage(WebsiteForPropertyPageId, aboutYouAndTheProperty6010YesSession)
        .apply(
          aboutYouAndTheProperty6010YesSession
        ) mustBe controllers.aboutyouandtheproperty.routes.LicensableActivitiesController
        .show()
    }

    "return a function that goes to about the property licensable activities details page when licensable activities has been completed" in {
      aboutYouAndThePropertyNavigator
        .nextPage(LicensableActivityPageId, aboutYouAndTheProperty6010YesSession)
        .apply(
          aboutYouAndTheProperty6010YesSession
        ) mustBe controllers.aboutyouandtheproperty.routes.LicensableActivitiesDetailsController
        .show()
    }

    "return a function that goes to about the property premises license page when about the property licensable activities details has been completed" in {
      aboutYouAndThePropertyNavigator
        .nextPage(LicensableActivityDetailsPageId, aboutYouAndTheProperty6010YesSession)
        .apply(
          aboutYouAndTheProperty6010YesSession
        ) mustBe controllers.aboutyouandtheproperty.routes.PremisesLicenseConditionsController
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
