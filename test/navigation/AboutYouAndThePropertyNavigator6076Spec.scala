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
import models.submissions.aboutyouandtheproperty.{AboutYouAndTheProperty, AboutYouAndThePropertyPartTwo, ContactDetailsQuestion}
import models.submissions.common.{AnswerNo, AnswerYes}
import navigation.identifiers.{AlternativeContactDetailsId, BatteriesCapacityId, ContactDetailsQuestionId, CostsBreakdownId, GeneratorCapacityId, OccupiersDetailsId, OccupiersDetailsListId, PlantAndTechnologyId, RenewablesPlantPageId, ThreeYearsConstructedPageId}
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec

import scala.concurrent.ExecutionContext

class AboutYouAndThePropertyNavigator6076Spec extends TestBaseSpec {

  val audit = mock[Audit]
  doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(any[HeaderCarrier], any[ExecutionContext])

  val navigator = new AdditionalInformationNavigator(audit)

  "About you and the property navigator for generic path" when {

    "return a function that goes to renewable plant page when contact details question have been completed with no" in {

      val answers = baseFilled6076Session.copy(
        aboutYouAndTheProperty =
          Some(AboutYouAndTheProperty(altDetailsQuestion = Some(ContactDetailsQuestion(AnswerNo))))
      )
      aboutYouAndThePropertyNavigator
        .nextPage(ContactDetailsQuestionId, answers)
        .apply(
          answers
        ) shouldBe controllers.aboutyouandtheproperty.routes.RenewablesPlantController.show()
    }

    "return a function that goes to property currently used when alternative contact details filled" in {
      aboutYouAndThePropertyNavigator
        .nextPage(AlternativeContactDetailsId, baseFilled6076Session)
        .apply(
          baseFilled6076Session
        ) shouldBe controllers.aboutyouandtheproperty.routes.RenewablesPlantController.show()
    }

    "return a function that goes to 3 years constructed page when renewables plants have been completed" in {
      aboutYouAndThePropertyNavigator
        .nextPage(RenewablesPlantPageId, baseFilled6076Session)
        .apply(
          baseFilled6076Session
        ) shouldBe controllers.aboutyouandtheproperty.routes.ThreeYearsConstructedController
        .show()
    }

    "return a function that goes to costs breakdown page when 3 years constructed page completed with Yes" in {

      val answers = baseFilled6076Session.copy(
        aboutYouAndTheProperty = Some(AboutYouAndTheProperty(threeYearsConstructed = Some(AnswerYes)))
      )
      aboutYouAndThePropertyNavigator
        .nextPage(ThreeYearsConstructedPageId, answers)
        .apply(
          answers
        ) shouldBe controllers.aboutyouandtheproperty.routes.CostsBreakdownController.show()
    }
    "return a function that goes to plant and technology page when 3 years constructed page completed with No" in {

      val answers = baseFilled6076Session.copy(
        aboutYouAndTheProperty = Some(AboutYouAndTheProperty(threeYearsConstructed = Some(AnswerNo)))
      )
      aboutYouAndThePropertyNavigator
        .nextPage(ThreeYearsConstructedPageId, answers)
        .apply(
          answers
        ) shouldBe controllers.aboutyouandtheproperty.routes.PlantAndTechnologyController.show()
    }

    "return a function that goes to plant and technology page when costs breakdown have been completed" in {
      aboutYouAndThePropertyNavigator
        .nextPage(CostsBreakdownId, aboutYouAndTheProperty6076Session)
        .apply(
          aboutYouAndTheProperty6076Session
        ) shouldBe controllers.aboutyouandtheproperty.routes.PlantAndTechnologyController
        .show()
    }

    "return a function that goes to about the generator capacity page when plant and technology have been completed" in {
      aboutYouAndThePropertyNavigator
        .nextPage(PlantAndTechnologyId, aboutYouAndTheProperty6076Session)
        .apply(
          aboutYouAndTheProperty6076Session
        ) shouldBe controllers.aboutyouandtheproperty.routes.GeneratorCapacityController
        .show()
    }

    "return a function that goes to batteries capacity page when generator capacity page has been completed" in {
      aboutYouAndThePropertyNavigator
        .nextPage(GeneratorCapacityId, aboutYouAndTheProperty6076Session)
        .apply(
          aboutYouAndTheProperty6076Session
        ) shouldBe controllers.aboutyouandtheproperty.routes.BatteriesCapacityController
        .show()
    }

    "return a function that goes to CYA  page when battery capacity page has been completed" in {
      aboutYouAndThePropertyNavigator
        .nextPage(BatteriesCapacityId, aboutYouAndTheProperty6076Session)
        .apply(
          aboutYouAndTheProperty6076Session
        ) shouldBe controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController
        .show()
    }

    "return a function that goes to occupier details page when occupier details list have been completed with yes" in {

      val answers = baseFilled6076Session.copy(
        aboutYouAndThePropertyPartTwo = Some(AboutYouAndThePropertyPartTwo(addAnotherPaidService = Some(AnswerYes)))
      )
      aboutYouAndThePropertyNavigator
        .nextPage(OccupiersDetailsListId, answers)
        .apply(
          answers
        ) shouldBe controllers.aboutyouandtheproperty.routes.OccupiersDetailsController.show()
    }
  }
}
