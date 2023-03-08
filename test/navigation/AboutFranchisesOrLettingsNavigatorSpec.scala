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
import navigation.identifiers._
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec

import scala.concurrent.ExecutionContext

class AboutFranchisesOrLettingsNavigatorSpec extends TestBaseSpec {

  val audit = mock[Audit]
  doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(any[HeaderCarrier], any[ExecutionContext])

  val navigator = new AboutFranchisesOrLettingsNavigator(audit)

  val sessionAboutFranchiseOrLettingYes =
    Session(prefilledUserLoginDetails, aboutFranchisesOrLettings = Some(prefilledAboutFranchiseOrLettings))
  val sessionAboutFranchiseOrLettingNo  =
    Session(prefilledUserLoginDetails, aboutFranchisesOrLettings = Some(prefilledAboutFranchiseOrLettingsNo))

  val sessionAboutFranchiseOrLettingYes6015 =
    Session(prefilledUserLoginDetails6015, aboutFranchisesOrLettings = Some(prefilledAboutFranchiseOrLettings6015))
  val sessionAboutFranchiseOrLettingNo6015  =
    Session(prefilledUserLoginDetails6015, aboutFranchisesOrLettings = Some(prefilledAboutFranchiseOrLettingsNo6015))

  "About franchise or lettings navigator" when {

    "go to navigate from an identifier that doesn't exist in the route map" in {
      case object UnknownIdentifier extends Identifier
      navigator
        .nextPage(UnknownIdentifier)
        .apply(sessionAboutFranchiseOrLettingYes) mustBe controllers.routes.LoginController.show()
    }

    "return a function that goes to catering operation page when franchise page has been completed yes" in {
      navigator
        .nextPage(FranchiseOrLettingsTiedToPropertyId)
        .apply(
          sessionAboutFranchiseOrLettingYes
        ) mustBe controllers.aboutfranchisesorlettings.routes.CateringOperationController.show()
    }

    "return a function that goes to task list page when franchise page has been completed no" in {
      navigator
        .nextPage(FranchiseOrLettingsTiedToPropertyId)
        .apply(
          sessionAboutFranchiseOrLettingNo
        ) mustBe controllers.routes.TaskListController.show()
    }

    "return a function that goes to catering operation page when franchise page has been completed yes 6015" in {
      navigator
        .nextPage(FranchiseOrLettingsTiedToPropertyId)
        .apply(
          sessionAboutFranchiseOrLettingYes6015
        ) mustBe controllers.aboutfranchisesorlettings.routes.ConcessionOrFranchiseController.show()
    }

    "return a function that goes to task list page when franchise page has been completed no 6015" in {
      navigator
        .nextPage(FranchiseOrLettingsTiedToPropertyId)
        .apply(
          sessionAboutFranchiseOrLettingNo6015
        ) mustBe controllers.routes.TaskListController.show()
    }

    "return a function that goes to catering operation details page when catering operation page has been completed yes" in {
      navigator
        .nextPage(CateringOperationPageId)
        .apply(
          sessionAboutFranchiseOrLettingYes
        ) mustBe controllers.aboutfranchisesorlettings.routes.CateringOperationDetailsController.show()
    }

    "return a function that goes to letting page when catering operation page has been completed no" in {
      navigator
        .nextPage(CateringOperationPageId)
        .apply(
          sessionAboutFranchiseOrLettingNo
        ) mustBe controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyController.show()
    }

    "return a function that goes to concession or franchise page when franchise page has been completed yes 6015" in {
      navigator
        .nextPage(CateringOperationPageId)
        .apply(
          sessionAboutFranchiseOrLettingYes6015
        ) mustBe controllers.aboutfranchisesorlettings.routes.CateringOperationDetailsController.show()
    }

    "return a function that goes to concession or franchise page when franchise page has been completed no 6015" in {
      navigator
        .nextPage(CateringOperationPageId)
        .apply(
          sessionAboutFranchiseOrLettingNo6015
        ) mustBe controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyController.show()
    }

    "return a function that goes to catering operation rent details page when catering operation details page has been completed" in {
      navigator
        .nextPage(CateringOperationDetailsPageId)
        .apply(
          sessionAboutFranchiseOrLettingYes
        ) mustBe controllers.aboutfranchisesorlettings.routes.CateringOperationDetailsRentController.show(0)
    }

    "return a function that goes to catering operation rent includes page when catering operation rent details page has been completed" in {
      navigator
        .nextPage(CateringOperationRentDetailsPageId)
        .apply(
          sessionAboutFranchiseOrLettingYes
        ) mustBe controllers.aboutfranchisesorlettings.routes.CateringOperationRentIncludesController.show(0)
    }

    "return a function that goes to add another catering operation page when catering operation rent includes page has been completed" in {
      navigator
        .nextPage(CateringOperationRentIncludesPageId)
        .apply(
          sessionAboutFranchiseOrLettingYes
        ) mustBe controllers.aboutfranchisesorlettings.routes.AddAnotherCateringOperationController.show(0)
    }

    "return a function that goes to franchise details page when franchise rent includes has been completed yes" in {
      navigator
        .nextPage(AddAnotherCateringOperationPageId)
        .apply(
          sessionAboutFranchiseOrLettingYes
        ) mustBe controllers.aboutfranchisesorlettings.routes.CateringOperationDetailsController.show()
    }

    "return a function that goes to lettings page when letting page has been completed no" in {
      navigator
        .nextPage(AddAnotherCateringOperationPageId)
        .apply(
          sessionAboutFranchiseOrLettingNo
        ) mustBe controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyController.show()
    }

    "return a function that goes to lettings details page when letting page has been completed yes" in {
      navigator
        .nextPage(LettingAccommodationPageId)
        .apply(
          sessionAboutFranchiseOrLettingYes
        ) mustBe controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyDetailsController.show()
    }

    "return a function that goes to task list page when letting page has been completed no" in {
      navigator
        .nextPage(LettingAccommodationPageId)
        .apply(
          sessionAboutFranchiseOrLettingNo
        ) mustBe controllers.routes.TaskListController.show()
    }

    "return a function that goes to letting rent details page when lettings page has been completed" in {
      navigator
        .nextPage(LettingAccommodationDetailsPageId)
        .apply(
          sessionAboutFranchiseOrLettingYes
        ) mustBe controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyDetailsRentController.show(0)
    }

    "return a function that goes to letting rent includes page when lettings rent details page has been completed" in {
      navigator
        .nextPage(LettingAccommodationRentDetailsPageId)
        .apply(
          sessionAboutFranchiseOrLettingYes
        ) mustBe controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyRentIncludesController.show(0)
    }

    "return a function that goes to add another letting page when lettings rent includes page has been completed" in {
      navigator
        .nextPage(LettingAccommodationRentIncludesPageId)
        .apply(
          sessionAboutFranchiseOrLettingYes
        ) mustBe controllers.aboutfranchisesorlettings.routes.AddAnotherLettingOtherPartOfPropertyController.show(0)
    }

    "return a function that goes to lettings details page when lettings rent includes has been completed yes" in {
      navigator
        .nextPage(AddAnotherLettingAccommodationPageId)
        .apply(
          sessionAboutFranchiseOrLettingYes
        ) mustBe controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyDetailsController.show()
    }

    "return a function that goes to task list page when add another letting page has been completed no" in {
      navigator
        .nextPage(AddAnotherLettingAccommodationPageId)
        .apply(
          sessionAboutFranchiseOrLettingNo
        ) mustBe controllers.Form6010.routes.CheckYourAnswersController.show()
    }
  }
}
