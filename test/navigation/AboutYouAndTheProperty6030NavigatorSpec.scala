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
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import utils.TestBaseSpec

class AboutYouAndTheProperty6030NavigatorSpec extends TestBaseSpec {

  "About you and the property navigator for no answers for 6030" when {

    // See AboutYouAndThePropertyNavigatorSpec for generic parts of the journey

    "return a function that goes to how is the property used page when no is answered for contact details and has been completed" in {
      aboutYouAndThePropertyNavigator
        .nextPage(ContactDetailsQuestionId, aboutYouAndTheProperty6030NoSession)
        .apply(
          aboutYouAndTheProperty6030NoSession
        ) mustBe controllers.aboutyouandtheproperty.routes.AboutThePropertyStringController
        .show()
    }

    "return a function that goes to website page when no is answered for about the property and has been completed" in {
      aboutYouAndThePropertyNavigator
        .nextPage(AboutThePropertyPageId, aboutYouAndTheProperty6030NoSession)
        .apply(
          aboutYouAndTheProperty6030NoSession
        ) mustBe controllers.aboutyouandtheproperty.routes.WebsiteForPropertyController
        .show()
    }
  }

  "return a function that goes to charity page when about the property website page has been completed" in {
    aboutYouAndThePropertyNavigator
      .nextPage(WebsiteForPropertyPageId, aboutYouAndTheProperty6030NoSession)
      .apply(
        aboutYouAndTheProperty6030NoSession
      ) mustBe controllers.aboutyouandtheproperty.routes.CharityQuestionController
      .show()
  }

  "return a function that goes to about the property alternative contact details page when yes is answered for contact details and has been completed" in {
    aboutYouAndThePropertyNavigator
      .nextPage(ContactDetailsQuestionId, aboutYouAndTheProperty6030YesSession)
      .apply(
        aboutYouAndTheProperty6030YesSession
      ) mustBe controllers.aboutyouandtheproperty.routes.AlternativeContactDetailsController
      .show()
  }

  "return a function that goes to about the property contact details page when yes is answered for alternative contact details and has been completed" in {
    aboutYouAndThePropertyNavigator
      .nextPage(AlternativeContactDetailsId, aboutYouAndTheProperty6030YesSession)
      .apply(
        aboutYouAndTheProperty6030YesSession
      ) mustBe controllers.aboutyouandtheproperty.routes.AboutThePropertyStringController
      .show()
  }

  "return a function that goes to about the property contact details page when data is input for how the property is used and has been completed" in {
    aboutYouAndThePropertyNavigator
      .nextPage(AboutThePropertyPageId, aboutYouAndTheProperty6030YesSession)
      .apply(
        aboutYouAndTheProperty6030YesSession
      ) mustBe controllers.aboutyouandtheproperty.routes.WebsiteForPropertyController
      .show()
  }
  "return a function that goes to trading activity page when charity question is  completed with Yes" in {

    val answers = aboutYouAndTheProperty6030YesSession.copy(
      aboutYouAndTheProperty = Some(AboutYouAndTheProperty(charityQuestion = Some(AnswerYes)))
    )
    aboutYouAndThePropertyNavigator
      .nextPage(CharityQuestionPageId, answers)
      .apply(
        answers
      ) mustBe controllers.aboutyouandtheproperty.routes.TradingActivityController.show()
  }
  "return a function that goes to CYA page when charity question is  completed with No" in {

    val answers = aboutYouAndTheProperty6030YesSession.copy(
      aboutYouAndTheProperty = Some(AboutYouAndTheProperty(charityQuestion = Some(AnswerNo)))
    )
    aboutYouAndThePropertyNavigator
      .nextPage(CharityQuestionPageId, answers)
      .apply(
        answers
      ) mustBe controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
  }
}
