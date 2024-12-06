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

import models.submissions.common.AnswerNo
import navigation.identifiers._
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import utils.TestBaseSpec

class AboutFranchisesOrLettingsNavigatorSpec extends TestBaseSpec {

  "About franchise or lettings navigator" when {

    "go to navigate from an identifier that doesn't exist in the route map" in {
      case object UnknownIdentifier extends Identifier
      aboutFranchisesOrLettingsNavigator
        .nextPage(UnknownIdentifier, sessionAboutFranchiseOrLetting6010YesSession)
        .apply(sessionAboutFranchiseOrLetting6010YesSession) shouldBe controllers.routes.LoginController.show
    }

    "return a function that goes to catering operation page when concession page has been completed yes" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(ConcessionOrFranchiseFeePageId, sessionAboutFranchiseOrLetting6010YesSession)
        .apply(
          sessionAboutFranchiseOrLetting6010YesSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.CateringOperationBusinessDetailsController.show()
    }

    "return a function that goes to catering operation page when lettings page has been completed yes" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(ConcessionOrFranchiseFeePageId, sessionAboutFranchiseOrLetting6010NoSession)
        .apply(
          sessionAboutFranchiseOrLetting6010NoSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyController.show()
    }

    "return a function that goes to catering operation page when franchise page has been completed yes" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(FranchiseOrLettingsTiedToPropertyId, sessionAboutFranchiseOrLetting6010YesSession)
        .apply(
          sessionAboutFranchiseOrLetting6010YesSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.CateringOperationController.show()
    }

    "return a function that goes to task list page when franchise page has been completed no" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(FranchiseOrLettingsTiedToPropertyId, sessionAboutFranchiseOrLetting6010NoSession)
        .apply(
          sessionAboutFranchiseOrLetting6010NoSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController
        .show()
    }

    "return a function that goes to letting other part of property page when franchise page has been completed no" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(ConcessionOrFranchiseFeePageId, sessionAboutFranchiseOrLetting6010NoSession)
        .apply(
          sessionAboutFranchiseOrLetting6010NoSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyController.show()
    }

    "return a function that goes to catering operation page when franchise page has been completed yes 6015" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(FranchiseOrLettingsTiedToPropertyId, sessionAboutFranchiseOrLetting6015YesSession)
        .apply(
          sessionAboutFranchiseOrLetting6015YesSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.ConcessionOrFranchiseController.show()
    }

    "return a function that goes to catering operation page when franchise page has been completed yes 6015123" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(FranchiseOrLettingsTiedToPropertyId, sessionAboutFranchiseOrLetting6020Session)
        .apply(
          sessionAboutFranchiseOrLetting6020Session
        ) shouldBe controllers.aboutfranchisesorlettings.routes.TypeOfLettingController.show(Some(4))
    }

    "return a function that goes to rent received from page when catering operation page has been completed yes 6015" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(CateringOperationDetailsPageId, sessionAboutFranchiseOrLetting6015YesSession)
        .apply(
          sessionAboutFranchiseOrLetting6015YesSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.RentReceivedFromController.show(0)
    }

    "return a function that goes to catering operation details page when rent from concession is completed yes 6015" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(RentFromConcessionId, sessionAboutFranchiseOrLetting6015YesSession)
        .apply(
          sessionAboutFranchiseOrLetting6015YesSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.CateringOperationDetailsController.show()
    }

    "return a function that goes to catering operation details page when rent from concession is completed no 6015" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(RentFromConcessionId, sessionAboutFranchiseOrLetting6015NoSession)
        .apply(
          sessionAboutFranchiseOrLetting6015NoSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyController.show()
    }

    "return a function that goes to task list page when franchise page has been completed no 6015" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(FranchiseOrLettingsTiedToPropertyId, sessionAboutFranchiseOrLetting6015NoSession)
        .apply(
          sessionAboutFranchiseOrLetting6015NoSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController
        .show()
    }

    "return a function that goes to catering operation details page when catering operation page has been completed yes" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(CateringOperationPageId, sessionAboutFranchiseOrLetting6010YesSession)
        .apply(
          sessionAboutFranchiseOrLetting6010YesSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.CateringOperationDetailsController.show(Some(0))
    }

    "return a function that goes to letting page when catering operation page has been completed no" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(CateringOperationPageId, sessionAboutFranchiseOrLetting6010NoSession)
        .apply(
          sessionAboutFranchiseOrLetting6010NoSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyController.show()
    }

    "return a function that goes to concession or franchise page when franchise page has been completed yes 6015" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(CateringOperationPageId, sessionAboutFranchiseOrLetting6015YesSession)
        .apply(
          sessionAboutFranchiseOrLetting6015YesSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.CateringOperationDetailsController.show(Some(0))
    }

    "return a function that continue with incomplete section when franchise page has been incomplete in" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(CateringOperationPageId, sessionAboutFranchiseOrLetting6015SIncompleteCatering)
        .apply(
          sessionAboutFranchiseOrLetting6015SIncompleteCatering
        ) shouldBe controllers.aboutfranchisesorlettings.routes.CateringOperationRentIncludesController.show(0)
    }

    "return a function that continue with incomplete section when catering detail page has been incomplete in 6015" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(CateringOperationPageId, sessionAboutFranchiseOrLetting6015SIncompleteCateringDetail)
        .apply(
          sessionAboutFranchiseOrLetting6015SIncompleteCateringDetail
        ) shouldBe controllers.aboutfranchisesorlettings.routes.RentReceivedFromController.show(0)
    }

    "return a function that continue with incomplete section when catering detail page has been incomplete in 6010" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(CateringOperationPageId, sessionAboutFranchiseOrLetting6010Incomplete)
        .apply(
          sessionAboutFranchiseOrLetting6010Incomplete
        ) shouldBe controllers.aboutfranchisesorlettings.routes.CateringOperationDetailsRentController.show(0)
    }

    "return a function that goes to concession or franchise page when franchise page has been completed no 6015" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(CateringOperationPageId, sessionAboutFranchiseOrLetting6015NoSession)
        .apply(
          sessionAboutFranchiseOrLetting6015NoSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyController.show()
    }

    "return a function that goes to catering operation rent details page when catering operation details page has been completed" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(CateringOperationDetailsPageId, sessionAboutFranchiseOrLetting6010YesSession)
        .apply(
          sessionAboutFranchiseOrLetting6010YesSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.CateringOperationDetailsRentController.show(0)
    }

    "return a function that goes to catering operation rent calculated from page when rent received from page has been completed" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(CalculatingTheRentForPageId, sessionAboutFranchiseOrLetting6015YesSession)
        .apply(
          sessionAboutFranchiseOrLetting6015YesSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.CateringOperationRentIncludesController.show(0)
    }

    "return a function that goes to catering operation rent included page when rent received calculation page has been completed" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(RentReceivedFromPageId, sessionAboutFranchiseOrLetting6015YesSession)
        .apply(
          sessionAboutFranchiseOrLetting6015YesSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.CalculatingTheRentForController.show(0)
    }

    "return a function that goes to catering operation rent includes page when catering operation rent details page has been completed" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(CateringOperationRentDetailsPageId, sessionAboutFranchiseOrLetting6010YesSession)
        .apply(
          sessionAboutFranchiseOrLetting6010YesSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.CateringOperationRentIncludesController.show(0)
    }

    "return a function that goes to add another catering operation page when catering operation rent includes page has been completed" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(CateringOperationRentIncludesPageId, sessionAboutFranchiseOrLetting6010YesSession)
        .apply(
          sessionAboutFranchiseOrLetting6010YesSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.AddAnotherCateringOperationController.show(0)
    }

    "return a function that goes to franchise details page when franchise rent includes has been completed yes" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(AddAnotherCateringOperationPageId, sessionAboutFranchiseOrLetting6010YesSession)
        .apply(
          sessionAboutFranchiseOrLetting6010YesSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.CateringOperationDetailsController.show(Some(1))
    }

    "return a function that goes to Catering details page when adding another catering business yes" in {
      val requestFromCYA: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/?from=CYA")
      aboutFranchisesOrLettingsNavigator
        .nextPage(AddAnotherCateringOperationPageId, sessionAboutFranchiseOrLetting6010YesSession)(hc, requestFromCYA)
        .apply(
          sessionAboutFranchiseOrLetting6010YesSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.CateringOperationDetailsController.show(Some(1))
    }

    "return a function that goes to lettings page when letting page has been completed no" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(AddAnotherCateringOperationPageId, sessionAboutFranchiseOrLetting6010NoSession)
        .apply(
          sessionAboutFranchiseOrLetting6010NoSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyController.show()
    }

    "return a function that goes to lettings details page when letting page has been completed yes" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(LettingAccommodationPageId, sessionAboutFranchiseOrLetting6010YesSession)
        .apply(
          sessionAboutFranchiseOrLetting6010YesSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyDetailsController
        .show(Some(0))
    }

    "return a function that goes to task list page when letting page has been completed no" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(LettingAccommodationPageId, sessionAboutFranchiseOrLetting6010NoSession)
        .apply(
          sessionAboutFranchiseOrLetting6010NoSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController
        .show()
    }

    "return a function that goes to letting rent details page when lettings page has been completed" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(LettingAccommodationDetailsPageId, sessionAboutFranchiseOrLetting6010YesSession)
        .apply(
          sessionAboutFranchiseOrLetting6010YesSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyDetailsRentController.show(0)
    }

    "return a function that goes to letting rent includes page when lettings rent details page has been completed" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(LettingAccommodationRentDetailsPageId, sessionAboutFranchiseOrLetting6010YesSession)
        .apply(
          sessionAboutFranchiseOrLetting6010YesSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyRentIncludesController.show(0)
    }

    "return a function that goes to add another letting page when lettings rent includes page has been completed" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(LettingAccommodationRentIncludesPageId, sessionAboutFranchiseOrLetting6010YesSession)
        .apply(
          sessionAboutFranchiseOrLetting6010YesSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.AddAnotherLettingOtherPartOfPropertyController.show(0)
    }

    "return a function that goes to lettings details page when lettings rent includes has been completed yes" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(AddAnotherLettingAccommodationPageId, sessionAboutFranchiseOrLetting6010YesSession)
        .apply(
          sessionAboutFranchiseOrLetting6010YesSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyDetailsController
        .show(Some(1))
    }

    "return a function that goes to rent includes if the section has not been completed" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(AddAnotherLettingAccommodationPageId, sessionAboutFranchiseOrLetting6015SIncompleteCatering)
        .apply(
          sessionAboutFranchiseOrLetting6015SIncompleteCatering
        ) shouldBe controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyRentIncludesController.show(0)
    }

    "return a function that goes to task list page when add another letting page has been completed no" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(AddAnotherLettingAccommodationPageId, sessionAboutFranchiseOrLetting6010NoSession)
        .apply(
          sessionAboutFranchiseOrLetting6010NoSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController
        .show()
    }
    "return a function that goes to lettings other part when max lettings catering page reached" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(MaxOfLettingsReachedCateringId, sessionAboutFranchiseOrLetting6010NoSession)
        .apply(
          sessionAboutFranchiseOrLetting6010NoSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyController.show()
    }
    "return a function that goes to CYA when max lettings current page reached" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(MaxOfLettingsReachedCurrentId, sessionAboutFranchiseOrLetting6010NoSession)
        .apply(
          sessionAboutFranchiseOrLetting6010NoSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController
        .show()
    }

    "return a function that goes to add another letting page when fee received comleted" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(FeeReceivedPageId, sessionAboutFranchiseOrLetting6030YesSession)
        .apply(
          sessionAboutFranchiseOrLetting6030YesSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.AddAnotherCateringOperationController.show(0)
    }

    "return a function that goes to task list page from cya" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(CheckYourAnswersAboutFranchiseOrLettingsId, sessionAboutFranchiseOrLetting6010NoSession)
        .apply(
          sessionAboutFranchiseOrLetting6010NoSession
        ) shouldBe controllers.routes.TaskListController.show().withFragment("franchiseAndLettings")
    }

    // TESTS FOR FORMS 6045 AND 6046

    "About franchise or lettings navigator for forms 6045/6046" when {

      "return a function that goes to source of income page when concession page has been completed yes" in {

        aboutFranchisesOrLettingsNavigator
          .nextPage(FranchiseOrLettingsTiedToPropertyId, sessionAboutFranchiseOrLetting6045)
          .apply(
            sessionAboutFranchiseOrLetting6045
          ) shouldBe controllers.aboutfranchisesorlettings.routes.TypeOfIncomeController.show()

      }
      "return a function that goes to CYA page when concession page has been completed no" in {

        val updatedSession = sessionAboutFranchiseOrLetting6045.copy(
          aboutFranchisesOrLettings = sessionAboutFranchiseOrLetting6045.aboutFranchisesOrLettings.map(
            _.copy(
              franchisesOrLettingsTiedToProperty = Some(AnswerNo)
            )
          )
        )

        aboutFranchisesOrLettingsNavigator
          .nextPage(FranchiseOrLettingsTiedToPropertyId, updatedSession)
          .apply(
            updatedSession
          ) shouldBe controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController
          .show()
      }

      "return a function that goes to concession type fees page when  concession type details finished" in {

        aboutFranchisesOrLettingsNavigator
          .nextPage(ConcessionTypeDetailsId, sessionAboutFranchiseOrLetting6045)
          .apply(
            sessionAboutFranchiseOrLetting6045
          ) shouldBe controllers.aboutfranchisesorlettings.routes.ConcessionTypeFeesController.show(0)

      }

      "return a function that goes to add another income page when  concession type fees finished" in {

        aboutFranchisesOrLettingsNavigator
          .nextPage(ConcessionTypeFeesId, sessionAboutFranchiseOrLetting6045)
          .apply(
            sessionAboutFranchiseOrLetting6045
          ) shouldBe controllers.aboutfranchisesorlettings.routes.RentalIncomeListController.show(0)

      }

      "return a function that goes to add another income page when  letting type included finished" in {

        aboutFranchisesOrLettingsNavigator
          .nextPage(LettingTypeIncludedId, sessionAboutFranchiseOrLetting6045)
          .apply(
            sessionAboutFranchiseOrLetting6045
          ) shouldBe controllers.aboutfranchisesorlettings.routes.RentalIncomeListController.show(0)
      }
    }
  }
}
