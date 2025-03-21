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
import navigation.identifiers.{AlternativeContactDetailsId, CommercialLettingAvailabilityId, CommercialLettingAvailabilityWelshId, CommercialLettingQuestionId, CompletedCommercialLettingsId, CompletedCommercialLettingsWelshId, ContactDetailsQuestionId, OccupiersDetailsId, PartsUnavailableId}
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec

import java.time.LocalDate
import scala.concurrent.ExecutionContext

class AboutYouAndTheProperty6048NavigatorSpec extends TestBaseSpec {

  val audit = mock[Audit]
  doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(any[HeaderCarrier], any[ExecutionContext])

  val navigator = new AboutYouAndThePropertyNavigator(audit)

  "About you and the property navigator for 6048" when {

    "handling no answers" should {

      "navigate to CommercialLettingQuestionController after completing ContactDetailsQuestion with no" in {
        val answers = baseFilled6048Session.copy(
          aboutYouAndTheProperty =
            Some(AboutYouAndTheProperty(altDetailsQuestion = Some(ContactDetailsQuestion(AnswerNo))))
        )
        navigator
          .nextPage(ContactDetailsQuestionId, answers)
          .apply(answers) shouldBe
          controllers.aboutyouandtheproperty.routes.CommercialLettingQuestionController.show()
      }

      "navigate to CheckYourAnswersAboutThePropertyController after completing PartsUnavailable with no" in {
        val answers = baseFilled6048Session.copy(
          aboutYouAndThePropertyPartTwo = Some(AboutYouAndThePropertyPartTwo(partsUnavailable = Some(AnswerNo)))
        )
        navigator
          .nextPage(PartsUnavailableId, answers)
          .apply(answers) shouldBe
          controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
      }
    }

    "handling yes answers" should {

      "navigate to CommercialLettingQuestionController after completing AlternativeContactDetails" in {
        navigator
          .nextPage(AlternativeContactDetailsId, baseFilled6048Session)
          .apply(baseFilled6048Session) shouldBe
          controllers.aboutyouandtheproperty.routes.CommercialLettingQuestionController.show()
      }

      "navigate to CommercialLettingAvailabilityController after completing CommercialLettingQuestion for English property" in {
        navigator
          .nextPage(CommercialLettingQuestionId, baseFilled6048Session)
          .apply(baseFilled6048Session) shouldBe
          controllers.aboutyouandtheproperty.routes.CommercialLettingAvailabilityController.show()
      }

      "navigate to CommercialLettingAvailabilityWelshController after completing CommercialLettingQuestion for Welsh property" in {
        navigator
          .nextPage(CommercialLettingQuestionId, baseFilled6048WelshSession)
          .apply(baseFilled6048WelshSession) shouldBe
          controllers.aboutyouandtheproperty.routes.CommercialLettingAvailabilityWelshController.show()
      }

      "navigate to CompletedCommercialLettingsController after completing CommercialLettingAvailability for English property" in {
        navigator
          .nextPage(CommercialLettingAvailabilityId, baseFilled6048Session)
          .apply(baseFilled6048Session) shouldBe
          controllers.aboutyouandtheproperty.routes.CompletedCommercialLettingsController.show()
      }

      "navigate to CompletedCommercialLettingsWelshController after completing CommercialLettingAvailabilityWelsh for Welsh property" in {
        navigator
          .nextPage(CommercialLettingAvailabilityWelshId, baseFilled6048WelshSession)
          .apply(baseFilled6048WelshSession) shouldBe
          controllers.aboutyouandtheproperty.routes.CompletedCommercialLettingsWelshController.show()
      }

      "navigate to PartsUnavailableController after completing CompletedCommercialLettings for English property" in {
        val answers = baseFilled6048Session.copy(
          aboutYouAndThePropertyPartTwo = Some(
            AboutYouAndThePropertyPartTwo(
              commercialLetAvailability = Some(200),
              completedCommercialLettings = Some(200)
            )
          )
        )
        navigator
          .nextPage(CompletedCommercialLettingsId, answers)
          .apply(answers) shouldBe
          controllers.aboutyouandtheproperty.routes.PartsUnavailableController.show()
      }

      "navigate to PartsUnavailableController after completing CompletedCommercialLettingsWelsh for Welsh property" in {
        val answers = baseFilled6048Session.copy(
          aboutYouAndThePropertyPartTwo = Some(
            AboutYouAndThePropertyPartTwo(
              commercialLetAvailabilityWelsh = Some(
                Seq(
                  LettingAvailability(LocalDate.of(2024, 3, 31), 100),
                  LettingAvailability(LocalDate.of(2023, 3, 31), 200),
                  LettingAvailability(LocalDate.of(2022, 3, 31), 150)
                )
              ),
              completedCommercialLettingsWelsh = Some(
                Seq(
                  CompletedLettings(LocalDate.of(2024, 3, 31), 100),
                  CompletedLettings(LocalDate.of(2023, 3, 31), 200),
                  CompletedLettings(LocalDate.of(2022, 3, 31), 150)
                )
              )
            )
          )
        )
        navigator
          .nextPage(CompletedCommercialLettingsWelshId, answers)
          .apply(answers) shouldBe
          controllers.aboutyouandtheproperty.routes.PartsUnavailableController.show()
      }

      "navigate to OccupiersDetailsController after completing PartsUnavailable with yes" in {
        val answers = baseFilled6048Session.copy(
          aboutYouAndThePropertyPartTwo = Some(AboutYouAndThePropertyPartTwo(partsUnavailable = Some(AnswerYes)))
        )
        navigator
          .nextPage(PartsUnavailableId, answers)
          .apply(answers) shouldBe
          controllers.aboutyouandtheproperty.routes.OccupiersDetailsController.show()
      }

      "navigate to OccupiersDetailsListController after completing OccupiersDetails" in {
        navigator
          .nextPage(OccupiersDetailsId, baseFilled6048Session)
          .apply(baseFilled6048Session) shouldBe
          controllers.aboutyouandtheproperty.routes.OccupiersDetailsListController.show(0)
      }
    }
  }
}
