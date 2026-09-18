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

import models.submissions.aboutyouandtheproperty.*
import models.submissions.common.AnswersYesNo.*
import navigation.identifiers.*
import test.{InjectedNavigation, TCTRAppSpec}

import scala.language.implicitConversions

class AboutYouAndTheProperty6045NavigatorSpec extends TCTRAppSpec with InjectedNavigation:

  "About you and the property navigator for 6045" should {
    "redirect to PropertyCurrentlyUsedController after completing ContactDetailsQuestion with no" in {
      val answers = baseFilled6045Session.copy(
        aboutYouAndTheProperty = AboutYouAndTheProperty(altDetailsQuestion = AnswerNo)
      )
      aboutYouAndThePropertyNavigator
        .nextPage(ContactDetailsQuestionId, answers)
        .apply(answers) shouldBe
        controllers.aboutyouandtheproperty.routes.PropertyCurrentlyUsedController.show()
    }

    "redirect to WebsiteForPropertyController after completing PropertyCurrentlyUsed" in {
      val answers = baseFilled6045Session.copy(
        aboutYouAndThePropertyPartTwo =
          AboutYouAndThePropertyPartTwo(propertyCurrentlyUsed =
            PropertyCurrentlyUsed(List("test"), "test")
          )
      )
      aboutYouAndThePropertyNavigator
        .nextPage(PropertyCurrentlyUsedPageId, answers)
        .apply(answers) shouldBe
        controllers.aboutyouandtheproperty.routes.WebsiteForPropertyController.show()
    }

    "redirect to CheckYourAnswersAboutThePropertyController after completing WebsiteForProperty" in {
      val answers = baseFilled6045Session.copy(
        aboutYouAndTheProperty =
          AboutYouAndTheProperty(websiteForPropertyDetails = WebsiteForPropertyDetails(AnswerNo, "test"))
      )
      aboutYouAndThePropertyNavigator
        .nextPage(WebsiteForPropertyPageId, answers)
        .apply(answers) shouldBe
        controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
    }
  }
