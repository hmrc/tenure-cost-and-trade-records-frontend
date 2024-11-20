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
import models.submissions.aboutyouandtheproperty.{AboutYouAndTheProperty, AboutYouAndThePropertyPartTwo, CompletedLettings, ContactDetailsQuestion, LettingAvailability}
import models.submissions.common.{AnswerNo, AnswerYes}
import navigation.identifiers.{AlternativeContactDetailsId, CommercialLettingAvailabilityId, CommercialLettingAvailabilityWelshId, CommercialLettingQuestionId, CompletedCommercialLettingsId, CompletedCommercialLettingsWelshId, ContactDetailsQuestionId, PartsUnavailableId}
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec

import java.time.LocalDate
import scala.concurrent.ExecutionContext

class AboutYouAndTheProperty6048NavigatorSpec extends TestBaseSpec {

  val audit = mock[Audit]
  doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(any[HeaderCarrier], any[ExecutionContext])

  val navigator = new AdditionalInformationNavigator(audit)

  "About you and the property navigator" when {

    "return a function that goes to commercial letting, when alternative correspondence question have been completed with no" in {

      val answers = baseFilled6048Session.copy(
        aboutYouAndTheProperty =
          Some(AboutYouAndTheProperty(altDetailsQuestion = Some(ContactDetailsQuestion(AnswerNo))))
      )
      aboutYouAndThePropertyNavigator
        .nextPage(ContactDetailsQuestionId, answers)
        .apply(
          answers
        ) shouldBe controllers.aboutyouandtheproperty.routes.CommercialLettingQuestionController.show()
    }

    "return a function that goes to Availability for commercial letting for England when alternative contact details filled" in {
      aboutYouAndThePropertyNavigator
        .nextPage(AlternativeContactDetailsId, baseFilled6048Session)
        .apply(
          baseFilled6048Session
        ) shouldBe controllers.aboutyouandtheproperty.routes.CommercialLettingQuestionController.show()
    }

    "return a function that goes to Availability for commercial letting for England when commercial letting question completed for english property" in {
      aboutYouAndThePropertyNavigator
        .nextPage(CommercialLettingQuestionId, baseFilled6048Session)
        .apply(
          baseFilled6048Session
        ) shouldBe controllers.aboutyouandtheproperty.routes.CommercialLettingAvailabilityController
        .show()
    }

    "return a function that goes to Availability for commercial letting for Wales when commercial letting question completed for welsh property" in {
      aboutYouAndThePropertyNavigator
        .nextPage(CommercialLettingQuestionId, baseFilled6048WelshSession)
        .apply(
          baseFilled6048WelshSession
        ) shouldBe controllers.aboutyouandtheproperty.routes.CommercialLettingAvailabilityWelshController
        .show()
    }
    "return a function that goes to completed commercial letting for England" in {

      aboutYouAndThePropertyNavigator
        .nextPage(CommercialLettingAvailabilityId, baseFilled6048Session)
        .apply(
          baseFilled6048Session
        ) shouldBe controllers.aboutyouandtheproperty.routes.CompletedCommercialLettingsController
        .show()
    }

    "return a function that goes to completed commercial letting for Wales" in {

      aboutYouAndThePropertyNavigator
        .nextPage(CommercialLettingAvailabilityWelshId, baseFilled6048WelshSession)
        .apply(
          baseFilled6048WelshSession
        ) shouldBe controllers.aboutyouandtheproperty.routes.CompletedCommercialLettingsWelshController
        .show()
    }

    "return a function that goes to CYA page when letting conditions not fulfilled for England " in {

      val answers = baseFilled6048Session.copy(
        aboutYouAndThePropertyPartTwo = Option(
          AboutYouAndThePropertyPartTwo(
            commercialLetAvailability = Option(12),
            completedCommercialLettings = Option(12)
          )
        )
      )
      aboutYouAndThePropertyNavigator
        .nextPage(CompletedCommercialLettingsId, answers)
        .apply(
          answers
        ) shouldBe controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
    }

    "return a function that goes to CYA page when letting conditions not fulfilled for Wales " in {

      val answers = baseFilled6048Session.copy(
        aboutYouAndThePropertyPartTwo = Option(
          AboutYouAndThePropertyPartTwo(
            commercialLetAvailabilityWelsh = Option(
              Seq(
                LettingAvailability(LocalDate.of(2024, 3, 31), 10),
                LettingAvailability(LocalDate.of(2023, 3, 31), 20),
                LettingAvailability(LocalDate.of(2022, 3, 31), 15)
              )
            ),
            completedCommercialLettingsWelsh = Option(
              Seq(
                CompletedLettings(LocalDate.of(2024, 3, 31), 10),
                CompletedLettings(LocalDate.of(2023, 3, 31), 20),
                CompletedLettings(LocalDate.of(2022, 3, 31), 15)
              )
            )
          )
        )
      )
      aboutYouAndThePropertyNavigator
        .nextPage(CompletedCommercialLettingsWelshId, answers)
        .apply(
          answers
        ) shouldBe controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
    }

    "return a function that goes to unavailable parts  page when letting conditions fulfilled for England " in {

      val answers = baseFilled6048Session.copy(
        aboutYouAndThePropertyPartTwo = Option(
          AboutYouAndThePropertyPartTwo(
            commercialLetAvailability = Option(200),
            completedCommercialLettings = Option(200)
          )
        )
      )
      aboutYouAndThePropertyNavigator
        .nextPage(CompletedCommercialLettingsId, answers)
        .apply(
          answers
        ) shouldBe controllers.aboutyouandtheproperty.routes.PartsUnavailableController.show()
    }

    "return a function that goes to unavailable parts  page when letting conditions fulfilled for Wales " in {

      val answers = baseFilled6048Session.copy(
        aboutYouAndThePropertyPartTwo = Option(
          AboutYouAndThePropertyPartTwo(
            commercialLetAvailabilityWelsh = Option(
              Seq(
                LettingAvailability(LocalDate.of(2024, 3, 31), 100),
                LettingAvailability(LocalDate.of(2023, 3, 31), 200),
                LettingAvailability(LocalDate.of(2022, 3, 31), 150)
              )
            ),
            completedCommercialLettingsWelsh = Option(
              Seq(
                CompletedLettings(LocalDate.of(2024, 3, 31), 100),
                CompletedLettings(LocalDate.of(2023, 3, 31), 200),
                CompletedLettings(LocalDate.of(2022, 3, 31), 150)
              )
            )
          )
        )
      )
      aboutYouAndThePropertyNavigator
        .nextPage(CompletedCommercialLettingsWelshId, answers)
        .apply(
          answers
        ) shouldBe controllers.aboutyouandtheproperty.routes.PartsUnavailableController.show()
    }

    "return a function that goes to CYA when parts unavailable completed with no" in {

      val answers = baseFilled6048Session.copy(
        aboutYouAndThePropertyPartTwo = Option(AboutYouAndThePropertyPartTwo(partsUnavailable = Option(AnswerNo)))
      )
      aboutYouAndThePropertyNavigator
        .nextPage(PartsUnavailableId, answers)
        .apply(
          answers
        ) shouldBe controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
    }

    "return a function that goes to occupiers details when parts unavailable completed with yes" in {

      val answers = baseFilled6048Session.copy(
        aboutYouAndThePropertyPartTwo = Option(AboutYouAndThePropertyPartTwo(partsUnavailable = Option(AnswerYes)))
      )
      aboutYouAndThePropertyNavigator
        .nextPage(PartsUnavailableId, answers)
        .apply(
          answers
        ) shouldBe controllers.routes.TaskListController.show() // TODO !!!
    }
  }
}
