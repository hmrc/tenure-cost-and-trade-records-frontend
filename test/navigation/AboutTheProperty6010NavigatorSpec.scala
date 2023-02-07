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

import connectors.Audit
import models.Session
import models.submissions.abouttheproperty._
import navigation.identifiers._
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec

import scala.concurrent.ExecutionContext

class AboutTheProperty6010NavigatorSpec extends TestBaseSpec {

  val audit = mock[Audit]
  doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(any[HeaderCarrier], any[ExecutionContext])

  val navigator = new AboutThePropertyNavigator(audit)

  val sessionAboutYou6010No =
    Session(
      prefilledUserLoginDetails,
      Some(prefilledStillConnectedDetailsYes),
      Some(prefilledRemoveConnection),
      Some(prefilledAboutYou),
      Some(prefilledAboutThePropertyNo)
    )

  "About to property navigator for no answers for 6010" when {

    "return a function that goes to about the property website page when about the property page has been completed" in {
      navigator
        .nextPage(AboutThePropertyPageId)
        .apply(sessionAboutYou6010No) mustBe controllers.abouttheproperty.routes.WebsiteForPropertyController.show()
    }

    "return a function that goes to licence activity page when about the property website page has been completed" in {
      navigator
        .nextPage(WebsiteForPropertyPageId)
        .apply(sessionAboutYou6010No) mustBe controllers.abouttheproperty.routes.LicensableActivitiesController.show()
    }

    "return a function that goes to property licence conditions page when licence activity page has been completed no" in {
      navigator
        .nextPage(LicensableActivityPageId)
        .apply(sessionAboutYou6010No) mustBe controllers.abouttheproperty.routes.PremisesLicenseConditionsController
        .show()
    }

    "return a function that goes to enforcement action taken page when property licence conditions page has been completed no" in {
      navigator
        .nextPage(PremisesLicenceConditionsPageId)
        .apply(sessionAboutYou6010No) mustBe controllers.abouttheproperty.routes.EnforcementActionBeenTakenController
        .show()
    }

    "return a function that goes to tied for goods page when enforcement action taken page has been completed with no" in {
      navigator
        .nextPage(EnforcementActionBeenTakenPageId)
        .apply(sessionAboutYou6010No) mustBe controllers.abouttheproperty.routes.TiedForGoodsController.show()
    }

    "return a function that goes to about the trading history page when tied for goods page has been completed with no" in {
      navigator
        .nextPage(TiedForGoodsPageId)
        .apply(sessionAboutYou6010No) mustBe controllers.aboutthetradinghistory.routes.AboutYourTradingHistoryController
        .show()
    }
  }

  val aboutThePropertyYes    = Some(
    AboutTheProperty(
      Some(PropertyDetails("OccupierName", CurrentPropertyHotel, None)),
      Some(WebsiteForPropertyDetails(BuildingOperationHaveAWebsiteYes, Some("webAddress"))),
      Some(LicensableActivitiesYes),
      Some(LicensableActivitiesInformationDetails("Licensable Activities Details")),
      Some(PremisesLicensesConditionsYes),
      Some(PremisesLicenseConditionsDetails("Premises license conditions Details")),
      Some(EnforcementActionsYes),
      Some(EnforcementActionHasBeenTakenInformationDetails("Enforcement action taken  Details")),
      Some(TiedGoodsYes),
      Some(TiedForGoodsInformationDetails(TiedForGoodsInformationDetailsFullTie))
    )
  )
  val sessionAboutYou6010Yes =
    Session(
      prefilledUserLoginDetails,
      Some(prefilledStillConnectedDetailsYes),
      Some(prefilledRemoveConnection),
      Some(prefilledAboutYou),
      aboutThePropertyYes
    )

  "About to property navigator for yes answers for 6010" when {

    "return a function that goes to about the property website page when about the property page has been completed" in {
      navigator
        .nextPage(AboutThePropertyPageId)
        .apply(sessionAboutYou6010Yes) mustBe controllers.abouttheproperty.routes.WebsiteForPropertyController.show()
    }

    "return a function that goes to licence activity page when about the property website page has been completed" in {
      navigator
        .nextPage(WebsiteForPropertyPageId)
        .apply(sessionAboutYou6010Yes) mustBe controllers.abouttheproperty.routes.LicensableActivitiesController.show()
    }

    "return a function that goes to licence activity details page when licence activity page has been completed yes" in {
      navigator
        .nextPage(LicensableActivityPageId)
        .apply(sessionAboutYou6010Yes) mustBe controllers.abouttheproperty.routes.LicensableActivitiesDetailsController
        .show()
    }

    "return a function that goes to premises license conditions conditions page when licence activity details page has been completed" in {
      navigator
        .nextPage(LicensableActivityDetailsPageId)
        .apply(sessionAboutYou6010Yes) mustBe controllers.abouttheproperty.routes.PremisesLicenseConditionsController
        .show()
    }

    "return a function that goes to premises license conditions details page when property licence conditions page has been completed yes" in {
      navigator
        .nextPage(PremisesLicenceConditionsPageId)
        .apply(
          sessionAboutYou6010Yes
        ) mustBe controllers.abouttheproperty.routes.PremisesLicenseConditionsDetailsController
        .show()
    }

    "return a function that goes to enforcement action taken details page when premises license conditions details page has been completed" in {
      navigator
        .nextPage(PremisesLicenceConditionsDetailsPageId)
        .apply(sessionAboutYou6010Yes) mustBe controllers.abouttheproperty.routes.EnforcementActionBeenTakenController
        .show()
    }

    "return a function that goes to enforcement action taken details page when enforcement action taken page has been completed with yes" in {
      navigator
        .nextPage(EnforcementActionBeenTakenPageId)
        .apply(
          sessionAboutYou6010Yes
        ) mustBe controllers.abouttheproperty.routes.EnforcementActionBeenTakenDetailsController.show()
    }

    "return a function that goes to tied for goods page when enforcement action taken details page has been completed" in {
      navigator
        .nextPage(EnforcementActionBeenTakenDetailsPageId)
        .apply(sessionAboutYou6010Yes) mustBe controllers.abouttheproperty.routes.TiedForGoodsController.show()
    }

    "return a function that goes to tied for goods details page when tied for goods page has been completed with yes" in {
      navigator
        .nextPage(TiedForGoodsPageId)
        .apply(sessionAboutYou6010Yes) mustBe controllers.abouttheproperty.routes.TiedForGoodsDetailsController.show()
    }

    "return a function that goes to about the trading history when tied for goods details page has been completed" in {
      navigator
        .nextPage(TiedForGoodsDetailsPageId)
        .apply(
          sessionAboutYou6010Yes
        ) mustBe controllers.aboutthetradinghistory.routes.AboutYourTradingHistoryController.show()
    }
  }

}
