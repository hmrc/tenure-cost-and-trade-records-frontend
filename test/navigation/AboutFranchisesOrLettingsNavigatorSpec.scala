/*
 * Copyright 2025 HM Revenue & Customs
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

import models.submissions.common.AnswersYesNo.*
import navigation.identifiers.*
import utils.TestBaseSpec

class AboutFranchisesOrLettingsNavigatorSpec extends TestBaseSpec {

  "About franchise or lettings navigator" when {

    "go to navigate from an identifier that doesn't exist in the route map" in {
      case object UnknownIdentifier extends Identifier
      aboutFranchisesOrLettingsNavigator
        .nextPage(UnknownIdentifier, sessionAboutFranchiseOrLetting6010YesSession)
        .apply(sessionAboutFranchiseOrLetting6010YesSession) shouldBe controllers.routes.LoginController.show
    }

    "return a function that goes to type of income page when franchise page has been completed yes" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(FranchiseOrLettingsTiedToPropertyId, sessionAboutFranchiseOrLetting6010YesSession)
        .apply(
          sessionAboutFranchiseOrLetting6010YesSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.TypeOfIncomeController.show()
    }

    "return a function that goes to CYA page when franchise page has been completed no" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(FranchiseOrLettingsTiedToPropertyId, sessionAboutFranchiseOrLetting6010NoSession)
        .apply(
          sessionAboutFranchiseOrLetting6010NoSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController
        .show()
    }

    "return a function that goes to type of income page when franchise page has been completed yes 6015" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(FranchiseOrLettingsTiedToPropertyId, sessionAboutFranchiseOrLetting6015YesSession)
        .apply(
          sessionAboutFranchiseOrLetting6015YesSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.TypeOfIncomeController.show()
    }

    "return a function that goes to task list page when franchise page has been completed no 6015" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(FranchiseOrLettingsTiedToPropertyId, sessionAboutFranchiseOrLetting6015NoSession)
        .apply(
          sessionAboutFranchiseOrLetting6015NoSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController
        .show()
    }
    "return a function that goes to rental income included page when rent received from page has been completed" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(CalculatingTheRentForPageId, sessionAboutFranchiseOrLetting6015YesSession)
        .apply(
          sessionAboutFranchiseOrLetting6015YesSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.RentalIncomeIncludedController.show(0)
    }

    "return a function that goes to CYA when max lettings current page reached" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(MaxOfLettingsReachedCurrentId, sessionAboutFranchiseOrLetting6010NoSession)
        .apply(
          sessionAboutFranchiseOrLetting6010NoSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController
        .show()
    }

    "return a function that goes to add another letting page when fee received completed" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(FeeReceivedPageId, sessionAboutFranchiseOrLetting6030YesSession)
        .apply(
          sessionAboutFranchiseOrLetting6030YesSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.RentalIncomeListController.show(1)
    }

    "return a function that goes to task list page from cya" in {
      aboutFranchisesOrLettingsNavigator
        .nextPage(CheckYourAnswersAboutFranchiseOrLettingsId, sessionAboutFranchiseOrLetting6010NoSession)
        .apply(
          sessionAboutFranchiseOrLetting6010NoSession
        ) shouldBe controllers.routes.TaskListController.show().withFragment("franchiseAndLettings")
    }

    // TEST FOR SINGLE RENTAL INCOME LOOP LOOP 6010

    "return a function that goes to rental income rent page when franchise type details page has been completed" in {

      aboutFranchisesOrLettingsNavigator
        .nextPage(FranchiseTypeDetailsId, sessionAboutFranchiseOrLetting6010YesSession)
        .apply(
          sessionAboutFranchiseOrLetting6010YesSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.RentalIncomeRentController.show(0)
    }

    "return a function that goes to rental income rent page when letting type details page has been completed" in {

      aboutFranchisesOrLettingsNavigator
        .nextPage(LettingTypeDetailsId, sessionAboutFranchiseOrLetting6010YesSession)
        .apply(
          sessionAboutFranchiseOrLetting6010YesSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.RentalIncomeRentController.show(0)
    }

    "return a function that goes to rental income included page when rental income rent page has been completed" in {

      aboutFranchisesOrLettingsNavigator
        .nextPage(RentalIncomeRentId, sessionAboutFranchiseOrLetting6010YesSession)
        .apply(
          sessionAboutFranchiseOrLetting6010YesSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.RentalIncomeIncludedController.show(0)
    }

    "return a function that goes to rental income list page when rental income rent page has been completed" in {

      aboutFranchisesOrLettingsNavigator
        .nextPage(RentalIncomeIncludedId, sessionAboutFranchiseOrLetting6010YesSession)
        .apply(
          sessionAboutFranchiseOrLetting6010YesSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.RentalIncomeListController.show(0)
    }

    // TEST FOR SINGLE RENTAL INCOME LOOP LOOP 6015 AND 6015

    "return a function that goes to rent received from page when concession type details page has been completed" in {

      aboutFranchisesOrLettingsNavigator
        .nextPage(FranchiseTypeDetailsId, sessionAboutFranchiseOrLetting6015YesSession)
        .apply(
          sessionAboutFranchiseOrLetting6015YesSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.RentReceivedFromController.show(0)
    }

    "return a function that goes to calculating rent for when rent received from page has been completed" in {

      aboutFranchisesOrLettingsNavigator
        .nextPage(RentReceivedFromPageId, sessionAboutFranchiseOrLetting6015YesSession)
        .apply(
          sessionAboutFranchiseOrLetting6015YesSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.CalculatingTheRentForController.show(0)
    }

    "return a function that goes to rental income included when rent calculating the rent for  page has been completed" in {

      aboutFranchisesOrLettingsNavigator
        .nextPage(CalculatingTheRentForPageId, sessionAboutFranchiseOrLetting6015YesSession)
        .apply(
          sessionAboutFranchiseOrLetting6015YesSession
        ) shouldBe controllers.aboutfranchisesorlettings.routes.RentalIncomeIncludedController.show(0)
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
      "return a function that goes to rental  income rent page rent when  letting type details finished" in {

        aboutFranchisesOrLettingsNavigator
          .nextPage(LettingTypeDetailsId, sessionAboutFranchiseOrLetting6045)
          .apply(
            sessionAboutFranchiseOrLetting6045
          ) shouldBe controllers.aboutfranchisesorlettings.routes.RentalIncomeRentController.show(0)

      }

      "return a function that goes to rental income  included page rent when  rental  income rent page finished" in {

        aboutFranchisesOrLettingsNavigator
          .nextPage(RentalIncomeRentId, sessionAboutFranchiseOrLetting6045)
          .apply(
            sessionAboutFranchiseOrLetting6045
          ) shouldBe controllers.aboutfranchisesorlettings.routes.RentalIncomeIncludedController.show(0)

      }

      "return a function that goes to add another income page when  letting type included finished" in {

        aboutFranchisesOrLettingsNavigator
          .nextPage(RentalIncomeIncludedId, sessionAboutFranchiseOrLetting6045)
          .apply(
            sessionAboutFranchiseOrLetting6045
          ) shouldBe controllers.aboutfranchisesorlettings.routes.RentalIncomeListController.show(0)
      }
      "return a function that goes to TypeOfLettingController for form 6020" in {
        val session = sessionAboutFranchiseOrLetting6020Session.copy(
          aboutFranchisesOrLettings = Some(prefilledAboutFranchiseOrLettingsWith6020LettingsAll)
        )
        aboutFranchisesOrLettingsNavigator
          .nextPage(FranchiseOrLettingsTiedToPropertyId, session)
          .apply(session) shouldBe
          controllers.aboutfranchisesorlettings.routes.TypeOfLettingController.show(Some(4))
      }
    }
  }
}
