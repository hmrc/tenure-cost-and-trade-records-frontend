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

class AboutYouAndTheProperty6020NavigatorSpec extends TCTRAppSpec with InjectedNavigation:

  "About you and the property navigator for 6020" should {
    "redirect to AlternativeContactDetailsController after completing ContactDetailsQuestion" in {
      aboutYouAndThePropertyNavigator
        .nextPage(ContactDetailsQuestionId, aboutYouAndTheProperty6020YesSession)
        .apply(aboutYouAndTheProperty6020YesSession) shouldBe
        controllers.aboutyouandtheproperty.routes.AboutThePropertyStringController.show()
    }

    "redirect to TradingActivityController after completing CharityQuestion with yes" in {
      val answers = aboutYouAndTheProperty6020YesSession.copy(
        aboutYouAndTheProperty = AboutYouAndTheProperty(charityQuestion = AnswerYes)
      )
      aboutYouAndThePropertyNavigator
        .nextPage(CharityQuestionPageId, answers)
        .apply(answers) shouldBe
        controllers.aboutyouandtheproperty.routes.TradingActivityController.show()
    }

    "redirect to ContactDetailsQuestionController after completing AboutYou" in {
      aboutYouAndThePropertyNavigator
        .nextPage(AboutYouPageId, aboutYouAndTheProperty6020YesSession)
        .apply(aboutYouAndTheProperty6020YesSession) shouldBe
        controllers.aboutyouandtheproperty.routes.ContactDetailsQuestionController.show()
    }

    "redirect to CheckYourAnswersAboutThePropertyController after completing CharityQuestion with no" in {
      val answers = aboutYouAndTheProperty6020YesSession.copy(
        aboutYouAndTheProperty = AboutYouAndTheProperty(charityQuestion = AnswerNo)
      )
      aboutYouAndThePropertyNavigator
        .nextPage(CharityQuestionPageId, answers)
        .apply(answers) shouldBe
        controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
    }

    "redirect to CheckYourAnswersAboutThePropertyController after completing AboutTheProperty" in {
      aboutYouAndThePropertyNavigator
        .nextPage(AboutThePropertyPageId, aboutYouAndTheProperty6020YesSession)
        .apply(aboutYouAndTheProperty6020YesSession) shouldBe
        controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
    }
  }
