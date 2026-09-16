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

import models.submissions.aboutyouandtheproperty.{AboutYouAndTheProperty, AboutYouAndThePropertyPartTwo}
import models.submissions.common.AnswersYesNo.*
import navigation.identifiers.*
import test.{InjectedNavigation, TCTRAppSpec}

import scala.language.implicitConversions

class AboutYouAndTheProperty6076NavigatorSpec extends TCTRAppSpec with InjectedNavigation:

  "About you and the property navigator for form 6076" should {
    "redirect to RenewablesPlantController after completing Contact Details Question with no" in {
      val answers = baseFilled6076Session.copy(
        aboutYouAndTheProperty = AboutYouAndTheProperty(altDetailsQuestion = AnswerNo)
      )
      aboutYouAndThePropertyNavigator
        .nextPage(ContactDetailsQuestionId, answers)
        .apply(answers) shouldBe
        controllers.aboutyouandtheproperty.routes.RenewablesPlantController.show()
    }

    "redirect to PlantAndTechnologyController after completing ThreeYearsConstructed with no" in {
      val answers = baseFilled6076Session.copy(
        aboutYouAndTheProperty = AboutYouAndTheProperty(threeYearsConstructed = AnswerNo)
      )
      aboutYouAndThePropertyNavigator
        .nextPage(ThreeYearsConstructedPageId, answers)
        .apply(answers) shouldBe
        controllers.aboutyouandtheproperty.routes.PlantAndTechnologyController.show()
    }

    "redirect to ThreeYearsConstructedController after completing RenewablesPlant" in {
      aboutYouAndThePropertyNavigator
        .nextPage(RenewablesPlantPageId, baseFilled6076Session)
        .apply(baseFilled6076Session) shouldBe
        controllers.aboutyouandtheproperty.routes.ThreeYearsConstructedController.show()
    }

    "redirect to CostsBreakdownController after completing ThreeYearsConstructed with yes" in {
      val answers = baseFilled6076Session.copy(
        aboutYouAndTheProperty = AboutYouAndTheProperty(threeYearsConstructed = AnswerYes)
      )
      aboutYouAndThePropertyNavigator
        .nextPage(ThreeYearsConstructedPageId, answers)
        .apply(answers) shouldBe
        controllers.aboutyouandtheproperty.routes.CostsBreakdownController.show()
    }

    "redirect to PlantAndTechnologyController after completing CostsBreakdown" in {
      aboutYouAndThePropertyNavigator
        .nextPage(CostsBreakdownId, baseFilled6076Session)
        .apply(baseFilled6076Session) shouldBe
        controllers.aboutyouandtheproperty.routes.PlantAndTechnologyController.show()
    }

    "redirect to GeneratorCapacityController after completing PlantAndTechnology" in {
      aboutYouAndThePropertyNavigator
        .nextPage(PlantAndTechnologyId, baseFilled6076Session)
        .apply(baseFilled6076Session) shouldBe
        controllers.aboutyouandtheproperty.routes.GeneratorCapacityController.show()
    }

    "redirect to BatteriesCapacityController after completing GeneratorCapacity" in {
      aboutYouAndThePropertyNavigator
        .nextPage(GeneratorCapacityId, baseFilled6076Session)
        .apply(baseFilled6076Session) shouldBe
        controllers.aboutyouandtheproperty.routes.BatteriesCapacityController.show()
    }

    "redirect to CheckYourAnswersAboutThePropertyController after completing BatteriesCapacity" in {
      aboutYouAndThePropertyNavigator
        .nextPage(BatteriesCapacityId, baseFilled6076Session)
        .apply(baseFilled6076Session) shouldBe
        controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
    }

    "redirect to OccupiersDetailsController after completing OccupiersDetailsList with yes" in {
      val answers = baseFilled6076Session.copy(
        aboutYouAndThePropertyPartTwo = AboutYouAndThePropertyPartTwo(addAnotherPaidService = AnswerYes)
      )
      aboutYouAndThePropertyNavigator
        .nextPage(OccupiersDetailsListId, answers)
        .apply(answers) shouldBe
        controllers.aboutyouandtheproperty.routes.OccupiersDetailsController.show()
    }
  }
