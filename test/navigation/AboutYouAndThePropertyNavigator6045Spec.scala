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

import connectors.Audit
import models.submissions.aboutyouandtheproperty._
import models.submissions.common.AnswersYesNo.*
import navigation.identifiers.*
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec

import scala.concurrent.ExecutionContext

class AboutYouAndThePropertyNavigator6045Spec extends TestBaseSpec {

  val audit = mock[Audit]
  doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(using any[HeaderCarrier], any[ExecutionContext])

  val navigator = new AboutYouAndThePropertyNavigator(audit)

  "About you and the property navigator for form 6045" when {

    "handling no answers" should {

      "navigate to PropertyCurrentlyUsedController after completing ContactDetailsQuestion with no" in {
        val answers = baseFilled6045Session.copy(
          aboutYouAndTheProperty =
            Some(AboutYouAndTheProperty(altDetailsQuestion = Some(ContactDetailsQuestion(AnswerNo))))
        )
        navigator
          .nextPage(ContactDetailsQuestionId, answers)
          .apply(answers) shouldBe
          controllers.aboutyouandtheproperty.routes.PropertyCurrentlyUsedController.show()
      }

      "navigate to PropertyCurrentlyUsedController after completing AlternativeContactDetails" in {
        navigator
          .nextPage(AlternativeContactDetailsId, baseFilled6045Session)
          .apply(baseFilled6045Session) shouldBe
          controllers.aboutyouandtheproperty.routes.PropertyCurrentlyUsedController.show()
      }
    }

    "handling yes answers" should {

      "navigate to WebsiteForPropertyController after completing PropertyCurrentlyUsed" in {
        val answers = baseFilled6045Session.copy(
          aboutYouAndThePropertyPartTwo = Some(
            AboutYouAndThePropertyPartTwo(propertyCurrentlyUsed =
              Some(PropertyCurrentlyUsed(List("test"), Some("test")))
            )
          )
        )
        navigator
          .nextPage(PropertyCurrentlyUsedPageId, answers)
          .apply(answers) shouldBe
          controllers.aboutyouandtheproperty.routes.WebsiteForPropertyController.show()
      }

      "navigate to CheckYourAnswersAboutThePropertyController after completing WebsiteForProperty" in {
        val answers = baseFilled6045Session.copy(
          aboutYouAndTheProperty = Some(
            AboutYouAndTheProperty(websiteForPropertyDetails =
              Some(WebsiteForPropertyDetails(BuildingOperationHaveAWebsiteNo, Some("test")))
            )
          )
        )
        navigator
          .nextPage(WebsiteForPropertyPageId, answers)
          .apply(answers) shouldBe
          controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
      }
    }
  }
}
