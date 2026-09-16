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

import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty
import models.submissions.common.AnswersYesNo.*
import navigation.identifiers.*
import test.{InjectedNavigation, TCTRAppSpec}

import scala.language.implicitConversions

class AboutYouAndTheProperty6030NavigatorSpec extends TCTRAppSpec with InjectedNavigation:

  "About you and the property navigator for 6030" should {
    "redirect to AboutThePropertyStringController after completing ContactDetailsQuestion with no" in {
      aboutYouAndThePropertyNavigator
        .nextPage(ContactDetailsQuestionId, aboutYouAndTheProperty6030NoSession)
        .apply(aboutYouAndTheProperty6030NoSession) shouldBe
        controllers.aboutyouandtheproperty.routes.AboutThePropertyStringController.show()
    }

    "redirect to WebsiteForPropertyController after completing AboutTheProperty with no" in {
      aboutYouAndThePropertyNavigator
        .nextPage(AboutThePropertyPageId, aboutYouAndTheProperty6030NoSession)
        .apply(aboutYouAndTheProperty6030NoSession) shouldBe
        controllers.aboutyouandtheproperty.routes.WebsiteForPropertyController.show()
    }

    "redirect to CharityQuestionController after completing WebsiteForProperty with no" in {
      aboutYouAndThePropertyNavigator
        .nextPage(WebsiteForPropertyPageId, aboutYouAndTheProperty6030NoSession)
        .apply(aboutYouAndTheProperty6030NoSession) shouldBe
        controllers.aboutyouandtheproperty.routes.CharityQuestionController.show()
    }

    "redirect to AlternativeContactDetailsController after completing ContactDetailsQuestion with yes" in {
      aboutYouAndThePropertyNavigator
        .nextPage(ContactDetailsQuestionId, aboutYouAndTheProperty6030YesSession)
        .apply(aboutYouAndTheProperty6030YesSession) shouldBe
        controllers.aboutyouandtheproperty.routes.AboutThePropertyStringController.show()
    }

    "redirect to WebsiteForPropertyController after completing AboutTheProperty with yes" in {
      aboutYouAndThePropertyNavigator
        .nextPage(AboutThePropertyPageId, aboutYouAndTheProperty6030YesSession)
        .apply(aboutYouAndTheProperty6030YesSession) shouldBe
        controllers.aboutyouandtheproperty.routes.WebsiteForPropertyController.show()
    }

    "redirect to TradingActivityController after completing CharityQuestion with yes" in {
      val answers = aboutYouAndTheProperty6030YesSession.copy(
        aboutYouAndTheProperty = AboutYouAndTheProperty(charityQuestion = AnswerYes)
      )
      aboutYouAndThePropertyNavigator
        .nextPage(CharityQuestionPageId, answers)
        .apply(answers) shouldBe
        controllers.aboutyouandtheproperty.routes.TradingActivityController.show()
    }

    "redirect to CheckYourAnswersAboutThePropertyController after completing CharityQuestion with no" in {
      val answers = aboutYouAndTheProperty6030YesSession.copy(
        aboutYouAndTheProperty = AboutYouAndTheProperty(charityQuestion = AnswerNo)
      )
      aboutYouAndThePropertyNavigator
        .nextPage(CharityQuestionPageId, answers)
        .apply(answers) shouldBe
        controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
    }
  }
