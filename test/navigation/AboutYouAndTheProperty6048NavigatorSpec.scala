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

import models.submissions.aboutyouandtheproperty.{AboutYouAndTheProperty, AboutYouAndThePropertyPartTwo, CompletedLettings, LettingAvailability}
import models.submissions.common.AnswersYesNo.*
import navigation.identifiers.*
import test.{InjectedNavigation, TCTRAppSpec}

import java.time.LocalDate
import scala.language.implicitConversions

class AboutYouAndTheProperty6048NavigatorSpec extends TCTRAppSpec with InjectedNavigation:

  "About you and the property navigator for 6048" when {
    "handling NO answers" should {
      "redirect to CommercialLettingQuestionController after completing Contact Details Question with no" in {
        val answers = baseFilled6048Session.copy(
          aboutYouAndTheProperty = AboutYouAndTheProperty(altDetailsQuestion = AnswerNo)
        )
        aboutYouAndThePropertyNavigator
          .nextPage(ContactDetailsQuestionId, answers)
          .apply(answers) shouldBe
          controllers.aboutyouandtheproperty.routes.CommercialLettingQuestionController.show()
      }

      "redirect to CheckYourAnswersAboutThePropertyController after completing PartsUnavailable with no" in {
        val answers = baseFilled6048Session.copy(
          aboutYouAndThePropertyPartTwo = AboutYouAndThePropertyPartTwo(partsUnavailable = AnswerNo)
        )
        aboutYouAndThePropertyNavigator
          .nextPage(PartsUnavailableId, answers)
          .apply(answers) shouldBe
          controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
      }
    }

    "handling YES answers" should {
      "redirect to CommercialLettingAvailabilityController after completing CommercialLettingQuestion for English property" in {
        aboutYouAndThePropertyNavigator
          .nextPage(CommercialLettingQuestionId, baseFilled6048Session)
          .apply(baseFilled6048Session) shouldBe
          controllers.aboutyouandtheproperty.routes.CommercialLettingAvailabilityController.show()
      }

      "redirect to CommercialLettingAvailabilityWelshController after completing CommercialLettingQuestion for Welsh property" in {
        aboutYouAndThePropertyNavigator
          .nextPage(CommercialLettingQuestionId, baseFilled6048WelshSession)
          .apply(baseFilled6048WelshSession) shouldBe
          controllers.aboutyouandtheproperty.routes.CommercialLettingAvailabilityWelshController.show()
      }

      "redirect to CompletedCommercialLettingsController after completing CommercialLettingAvailability for English property" in {
        aboutYouAndThePropertyNavigator
          .nextPage(CommercialLettingAvailabilityId, baseFilled6048Session)
          .apply(baseFilled6048Session) shouldBe
          controllers.aboutyouandtheproperty.routes.CompletedCommercialLettingsController.show()
      }

      "redirect to CompletedCommercialLettingsWelshController after completing CommercialLettingAvailabilityWelsh for Welsh property" in {
        aboutYouAndThePropertyNavigator
          .nextPage(CommercialLettingAvailabilityWelshId, baseFilled6048WelshSession)
          .apply(baseFilled6048WelshSession) shouldBe
          controllers.aboutyouandtheproperty.routes.CompletedCommercialLettingsWelshController.show()
      }

      "redirect to PartsUnavailableController after completing CompletedCommercialLettings for English property" in {
        val answers = baseFilled6048Session.copy(
          aboutYouAndThePropertyPartTwo =
            AboutYouAndThePropertyPartTwo(
              commercialLetAvailability = 200,
              completedCommercialLettings = 200
            )
        )
        aboutYouAndThePropertyNavigator
          .nextPage(CompletedCommercialLettingsId, answers)
          .apply(answers) shouldBe
          controllers.aboutyouandtheproperty.routes.PartsUnavailableController.show()
      }

      "redirect to PartsUnavailableController after completing CompletedCommercialLettingsWelsh for Welsh property" in {
        val answers = baseFilled6048Session.copy(
          aboutYouAndThePropertyPartTwo =
            AboutYouAndThePropertyPartTwo(
              commercialLetAvailabilityWelsh =
                Seq(
                  LettingAvailability(LocalDate.of(2024, 3, 31), 100),
                  LettingAvailability(LocalDate.of(2023, 3, 31), 200),
                  LettingAvailability(LocalDate.of(2022, 3, 31), 150)
                ),
              completedCommercialLettingsWelsh =
                Seq(
                  CompletedLettings(LocalDate.of(2024, 3, 31), 100),
                  CompletedLettings(LocalDate.of(2023, 3, 31), 200),
                  CompletedLettings(LocalDate.of(2022, 3, 31), 150)
                )
            )
        )
        aboutYouAndThePropertyNavigator
          .nextPage(CompletedCommercialLettingsWelshId, answers)
          .apply(answers) shouldBe
          controllers.aboutyouandtheproperty.routes.PartsUnavailableController.show()
      }

      "redirect to OccupiersDetailsController after completing PartsUnavailable with yes" in {
        val answers = baseFilled6048Session.copy(
          aboutYouAndThePropertyPartTwo = AboutYouAndThePropertyPartTwo(partsUnavailable = AnswerYes)
        )
        aboutYouAndThePropertyNavigator
          .nextPage(PartsUnavailableId, answers)
          .apply(answers) shouldBe
          controllers.aboutyouandtheproperty.routes.OccupiersDetailsController.show()
      }

      "redirect to OccupiersDetailsListController after completing OccupiersDetails" in {
        aboutYouAndThePropertyNavigator
          .nextPage(OccupiersDetailsId, baseFilled6048Session)
          .apply(baseFilled6048Session) shouldBe
          controllers.aboutyouandtheproperty.routes.OccupiersDetailsListController.show(0)
      }
    }
  }
