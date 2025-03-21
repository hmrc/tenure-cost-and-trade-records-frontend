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

import models.submissions.aboutyouandtheproperty.AboutYouAndTheProperty
import models.submissions.common.{AnswerNo, AnswerYes}
import navigation.identifiers._
import utils.TestBaseSpec

class AboutYouAndTheProperty6030NavigatorSpec extends TestBaseSpec {

  val navigator = aboutYouAndThePropertyNavigator

  "About you and the property navigator for 6030" when {

    "navigate to AboutThePropertyStringController after completing ContactDetailsQuestion with no" in {
      navigator
        .nextPage(ContactDetailsQuestionId, aboutYouAndTheProperty6030NoSession)
        .apply(aboutYouAndTheProperty6030NoSession) shouldBe
        controllers.aboutyouandtheproperty.routes.AboutThePropertyStringController.show()
    }

    "navigate to WebsiteForPropertyController after completing AboutTheProperty with no" in {
      navigator
        .nextPage(AboutThePropertyPageId, aboutYouAndTheProperty6030NoSession)
        .apply(aboutYouAndTheProperty6030NoSession) shouldBe
        controllers.aboutyouandtheproperty.routes.WebsiteForPropertyController.show()
    }

    "navigate to CharityQuestionController after completing WebsiteForProperty with no" in {
      navigator
        .nextPage(WebsiteForPropertyPageId, aboutYouAndTheProperty6030NoSession)
        .apply(aboutYouAndTheProperty6030NoSession) shouldBe
        controllers.aboutyouandtheproperty.routes.CharityQuestionController.show()
    }

    "navigate to AlternativeContactDetailsController after completing ContactDetailsQuestion with yes" in {
      navigator
        .nextPage(ContactDetailsQuestionId, aboutYouAndTheProperty6030YesSession)
        .apply(aboutYouAndTheProperty6030YesSession) shouldBe
        controllers.aboutyouandtheproperty.routes.AlternativeContactDetailsController.show()
    }

    "navigate to AboutThePropertyStringController after completing AlternativeContactDetails with yes" in {
      navigator
        .nextPage(AlternativeContactDetailsId, aboutYouAndTheProperty6030YesSession)
        .apply(aboutYouAndTheProperty6030YesSession) shouldBe
        controllers.aboutyouandtheproperty.routes.AboutThePropertyStringController.show()
    }

    "navigate to WebsiteForPropertyController after completing AboutTheProperty with yes" in {
      navigator
        .nextPage(AboutThePropertyPageId, aboutYouAndTheProperty6030YesSession)
        .apply(aboutYouAndTheProperty6030YesSession) shouldBe
        controllers.aboutyouandtheproperty.routes.WebsiteForPropertyController.show()
    }

    "navigate to TradingActivityController after completing CharityQuestion with yes" in {
      val answers = aboutYouAndTheProperty6030YesSession.copy(
        aboutYouAndTheProperty = Some(AboutYouAndTheProperty(charityQuestion = Some(AnswerYes)))
      )
      navigator
        .nextPage(CharityQuestionPageId, answers)
        .apply(answers) shouldBe
        controllers.aboutyouandtheproperty.routes.TradingActivityController.show()
    }

    "navigate to CheckYourAnswersAboutThePropertyController after completing CharityQuestion with no" in {
      val answers = aboutYouAndTheProperty6030YesSession.copy(
        aboutYouAndTheProperty = Some(AboutYouAndTheProperty(charityQuestion = Some(AnswerNo)))
      )
      navigator
        .nextPage(CharityQuestionPageId, answers)
        .apply(answers) shouldBe
        controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
    }
  }
}
