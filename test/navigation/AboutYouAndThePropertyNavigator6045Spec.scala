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
import models.submissions.aboutyouandtheproperty._
import models.submissions.common.AnswerNo
import navigation.identifiers._
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec

import scala.concurrent.ExecutionContext

class AboutYouAndThePropertyNavigator6045Spec extends TestBaseSpec {

  val audit = mock[Audit]
  doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(any[HeaderCarrier], any[ExecutionContext])

  val navigator = new AdditionalInformationNavigator(audit)

  "About you and the property navigator for generic path" when {

    "return a function that goes to property currently used when contact details question have been completed with no" in {

      val answers = baseFilled6045Session.copy(
        aboutYouAndTheProperty =
          Some(AboutYouAndTheProperty(altDetailsQuestion = Some(ContactDetailsQuestion(AnswerNo))))
      )
      aboutYouAndThePropertyNavigator
        .nextPage(ContactDetailsQuestionId, answers)
        .apply(
          answers
        ) shouldBe controllers.aboutyouandtheproperty.routes.PropertyCurrentlyUsedController.show()
    }

    "return a function that goes to website when property currently used has been completed" in {

      val answers = baseFilled6045Session.copy(
        aboutYouAndThePropertyPartTwo = Some(
          AboutYouAndThePropertyPartTwo(propertyCurrentlyUsed = Some(PropertyCurrentlyUsed(List("test"), Some("test"))))
        )
      )
      aboutYouAndThePropertyNavigator
        .nextPage(PropertyCurrentlyUsedPageId, answers)
        .apply(
          answers
        ) shouldBe controllers.aboutyouandtheproperty.routes.WebsiteForPropertyController.show()
    }

    "return a function that goes to CYA when website question have been completed" in {

      val answers = baseFilled6045Session.copy(
        aboutYouAndTheProperty = Some(
          AboutYouAndTheProperty(websiteForPropertyDetails =
            Some(WebsiteForPropertyDetails(BuildingOperationHaveAWebsiteNo, Some("test")))
          )
        )
      )
      aboutYouAndThePropertyNavigator
        .nextPage(WebsiteForPropertyPageId, answers)
        .apply(
          answers
        ) shouldBe controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
    }
  }
}
