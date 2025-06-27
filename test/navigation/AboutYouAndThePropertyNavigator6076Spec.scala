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
import models.submissions.aboutyouandtheproperty.{AboutYouAndTheProperty, AboutYouAndThePropertyPartTwo}
import models.submissions.common.AnswersYesNo.*
import navigation.identifiers.*
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.HeaderCarrier
import utils.TestBaseSpec

import scala.concurrent.ExecutionContext

class AboutYouAndThePropertyNavigator6076Spec extends TestBaseSpec {

  val audit = mock[Audit]
  doNothing.when(audit).sendExplicitAudit(any[String], any[JsObject])(using any[HeaderCarrier], any[ExecutionContext])

  val navigator = new AboutYouAndThePropertyNavigator(audit)

  "About you and the property navigator for form 6076" when {

    "navigate to RenewablesPlantController after completing Contact Details Question with no" in {
      val answers = baseFilled6076Session.copy(
        aboutYouAndTheProperty = Some(AboutYouAndTheProperty(altDetailsQuestion = Some(AnswerNo)))
      )
      navigator
        .nextPage(ContactDetailsQuestionId, answers)
        .apply(answers) shouldBe
        controllers.aboutyouandtheproperty.routes.RenewablesPlantController.show()
    }

    "navigate to PlantAndTechnologyController after completing ThreeYearsConstructed with no" in {
      val answers = baseFilled6076Session.copy(
        aboutYouAndTheProperty = Some(AboutYouAndTheProperty(threeYearsConstructed = Some(AnswerNo)))
      )
      navigator
        .nextPage(ThreeYearsConstructedPageId, answers)
        .apply(answers) shouldBe
        controllers.aboutyouandtheproperty.routes.PlantAndTechnologyController.show()
    }
    "navigate to RenewablesPlantController after completing AlternativeContactDetails" in {
      navigator
        .nextPage(AlternativeContactDetailsId, baseFilled6076Session)
        .apply(baseFilled6076Session) shouldBe
        controllers.aboutyouandtheproperty.routes.RenewablesPlantController.show()
    }

    "navigate to ThreeYearsConstructedController after completing RenewablesPlant" in {
      navigator
        .nextPage(RenewablesPlantPageId, baseFilled6076Session)
        .apply(baseFilled6076Session) shouldBe
        controllers.aboutyouandtheproperty.routes.ThreeYearsConstructedController.show()
    }

    "navigate to CostsBreakdownController after completing ThreeYearsConstructed with yes" in {
      val answers = baseFilled6076Session.copy(
        aboutYouAndTheProperty = Some(AboutYouAndTheProperty(threeYearsConstructed = Some(AnswerYes)))
      )
      navigator
        .nextPage(ThreeYearsConstructedPageId, answers)
        .apply(answers) shouldBe
        controllers.aboutyouandtheproperty.routes.CostsBreakdownController.show()
    }

    "navigate to PlantAndTechnologyController after completing CostsBreakdown" in {
      navigator
        .nextPage(CostsBreakdownId, baseFilled6076Session)
        .apply(baseFilled6076Session) shouldBe
        controllers.aboutyouandtheproperty.routes.PlantAndTechnologyController.show()
    }

    "navigate to GeneratorCapacityController after completing PlantAndTechnology" in {
      navigator
        .nextPage(PlantAndTechnologyId, baseFilled6076Session)
        .apply(baseFilled6076Session) shouldBe
        controllers.aboutyouandtheproperty.routes.GeneratorCapacityController.show()
    }

    "navigate to BatteriesCapacityController after completing GeneratorCapacity" in {
      navigator
        .nextPage(GeneratorCapacityId, baseFilled6076Session)
        .apply(baseFilled6076Session) shouldBe
        controllers.aboutyouandtheproperty.routes.BatteriesCapacityController.show()
    }

    "navigate to CheckYourAnswersAboutThePropertyController after completing BatteriesCapacity" in {
      navigator
        .nextPage(BatteriesCapacityId, baseFilled6076Session)
        .apply(baseFilled6076Session) shouldBe
        controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
    }

    "navigate to OccupiersDetailsController after completing OccupiersDetailsList with yes" in {
      val answers = baseFilled6076Session.copy(
        aboutYouAndThePropertyPartTwo = Some(AboutYouAndThePropertyPartTwo(addAnotherPaidService = Some(AnswerYes)))
      )
      navigator
        .nextPage(OccupiersDetailsListId, answers)
        .apply(answers) shouldBe
        controllers.aboutyouandtheproperty.routes.OccupiersDetailsController.show()
    }
  }
}
