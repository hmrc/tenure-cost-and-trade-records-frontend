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
import models.submissions.common.{AnswerNo, AnswerYes}
import models.{ForTypes, Session}
import navigation.identifiers._
import play.api.Logging
import play.api.mvc.Call

import javax.inject.Inject

class AboutYouAndThePropertyNavigator @Inject() (audit: Audit) extends Navigator(audit) with Logging {

  override def cyaPage: Option[Call] =
    Some(controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show())

  override val postponeCYARedirectPages: Set[String] = Set(
    controllers.aboutyouandtheproperty.routes.AlternativeContactDetailsController.show(),
    controllers.aboutyouandtheproperty.routes.CostsBreakdownController.show(),
    controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedDetailsController.show(),
    controllers.aboutyouandtheproperty.routes.LicensableActivitiesDetailsController.show(),
    controllers.aboutyouandtheproperty.routes.PremisesLicenseConditionsDetailsController.show(),
    controllers.aboutyouandtheproperty.routes.EnforcementActionBeenTakenDetailsController.show(),
    controllers.aboutyouandtheproperty.routes.TiedForGoodsDetailsController.show()
  ).map(_.url)

  private def alternativeContactDetailsRouting: Session => Call = answers => {
    answers.forType match {
      case ForTypes.for6020 | ForTypes.for6030 =>
        controllers.aboutyouandtheproperty.routes.AboutThePropertyStringController.show()
      case ForTypes.for6076                    => controllers.aboutyouandtheproperty.routes.RenewablesPlantController.show()
      case ForTypes.for6045 | ForTypes.for6046 =>
        controllers.aboutyouandtheproperty.routes.PropertyCurrentlyUsedController.show()
      case _                                   => controllers.aboutyouandtheproperty.routes.AboutThePropertyController.show()
    }
  }

  private def aboutThePropertyDescriptionRouting: Session => Call = answers => {
    val answersForType = answers.forType
    if (answersForType.equals(ForTypes.for6020)) {
      controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
    } else
      controllers.aboutyouandtheproperty.routes.WebsiteForPropertyController.show()
  }

  private def websiteForPropertyRouting: Session => Call     = answers => {
    answers.forType match {
      case ForTypes.for6015 | ForTypes.for6016 =>
        controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedController.show()
      case ForTypes.for6030                    => controllers.aboutyouandtheproperty.routes.CharityQuestionController.show()
      case ForTypes.for6045 | ForTypes.for6046 =>
        controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
      case _                                   => controllers.aboutyouandtheproperty.routes.LicensableActivitiesController.show()

    }
  }
  private def charityQuestionRouting: Session => Call        = answers => {
    answers.aboutYouAndTheProperty.flatMap(_.charityQuestion) match {
      case Some(AnswerYes) => controllers.aboutyouandtheproperty.routes.TradingActivityController.show()
      case Some(AnswerNo)  => controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
      case _               =>
        logger.warn(s"Navigation for about the property reached without correct option by controller")
        throw new RuntimeException("Invalid option exception for charity question routing")
    }
  }
  private def premisesLicenseGrantedRouting: Session => Call = answers => {
    if (answers.forType.equals(ForTypes.for6015) || answers.forType.equals(ForTypes.for6016)) {
      answers.aboutYouAndTheProperty.flatMap(_.premisesLicenseGrantedDetail.map(_.name)) match {
        case Some("yes") =>
          controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedDetailsController.show()
        case Some("no")  =>
          controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
        case _           =>
          logger.warn(
            s"Navigation for about the property reached without correct selection of premises licence granted by controller"
          )
          throw new RuntimeException("Invalid option exception for licence activity routing")
      }
    } else {
      controllers.routes.LoginController.show
    }
  }

  private def premisesLicenseGrantedDetailsRouting: Session => Call = answers => {
    if (answers.forType == ForTypes.for6015 || answers.forType == ForTypes.for6016)
      controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
    else
      controllers.aboutyouandtheproperty.routes.LicensableActivitiesController.show()
  }

  private def licensableActivityRouting: Session => Call = answers => {
    answers.aboutYouAndTheProperty.flatMap(_.licensableActivities.map(_.name)) match {
      case Some("yes") => controllers.aboutyouandtheproperty.routes.LicensableActivitiesDetailsController.show()
      case Some("no")  => controllers.aboutyouandtheproperty.routes.PremisesLicenseConditionsController.show()
      case _           =>
        logger.warn(
          s"Navigation for about the property reached without correct selection of licence activity by controller"
        )
        throw new RuntimeException("Invalid option exception for licence activity routing")
    }
  }

  private def premisesLicenceConditionsRouting: Session => Call = answers => {
    answers.aboutYouAndTheProperty.flatMap(_.premisesLicenseConditions.map(_.name)) match {
      case Some("yes") => controllers.aboutyouandtheproperty.routes.PremisesLicenseConditionsDetailsController.show()
      case Some("no")  => controllers.aboutyouandtheproperty.routes.EnforcementActionBeenTakenController.show()
      case _           =>
        logger.warn(
          s"Navigation for about the property reached without correct selection of premises licence conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for premises licence conditions routing")
    }
  }

  private def enforcementActionTakenRouting: Session => Call = answers => {
    answers.aboutYouAndTheProperty.flatMap(_.enforcementAction.map(_.name)) match {
      case Some("yes") => controllers.aboutyouandtheproperty.routes.EnforcementActionBeenTakenDetailsController.show()
      case Some("no")  =>
        controllers.aboutyouandtheproperty.routes.TiedForGoodsController.show()
      case _           =>
        logger.warn(
          s"Navigation for about the property reached without correct selection of enforcement action taken by controller"
        )
        throw new RuntimeException("Invalid option exception for enforcement action taken routing")
    }
  }

  private def tiedGoodsRouting: Session => Call = answers => {
    answers.aboutYouAndTheProperty.flatMap(_.tiedForGoods.map(_.name)) match {
      case Some("yes") => controllers.aboutyouandtheproperty.routes.TiedForGoodsDetailsController.show()
      case Some("no")  => controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
      case _           =>
        logger.warn(
          s"Navigation for about the property reached without correct selection of tied goods by controller"
        )
        throw new RuntimeException("Invalid option exception for tied goods routing")
    }
  }

  private def contactDetailsQuestionRouting: Session => Call = answers => {
    answers.aboutYouAndTheProperty.flatMap(_.altDetailsQuestion.map(_.contactDetailsQuestion)) match {
      case Some(AnswerYes) => controllers.aboutyouandtheproperty.routes.AlternativeContactDetailsController.show()
      case Some(AnswerNo)  =>
        answers.forType match {
          case ForTypes.for6020 | ForTypes.for6030 =>
            controllers.aboutyouandtheproperty.routes.AboutThePropertyStringController.show()
          case ForTypes.for6076                    => controllers.aboutyouandtheproperty.routes.RenewablesPlantController.show()
          case ForTypes.for6045 | ForTypes.for6046 =>
            controllers.aboutyouandtheproperty.routes.PropertyCurrentlyUsedController.show()
          case _                                   => controllers.aboutyouandtheproperty.routes.AboutThePropertyController.show()
        }
      case _               =>
        logger.warn(
          s"Navigation for alternative details question reached without correct selection of conditions by controller"
        )

        throw new RuntimeException("Invalid option exception for alternative details question routing")
    }
  }

  private def threeYearsConstructedRouting: Session => Call = answers => {
    answers.aboutYouAndTheProperty.flatMap(_.threeYearsConstructed) match {
      case Some(AnswerYes) => controllers.aboutyouandtheproperty.routes.CostsBreakdownController.show()
      case Some(AnswerNo)  => controllers.aboutyouandtheproperty.routes.PlantAndTechnologyController.show()
      case _               =>
        logger.warn(
          s"Navigation for about the property reached without correct selection of was the site constructed within last 3 years by controller"
        )
        throw new RuntimeException("Invalid option exception for tied goods routing")
    }
  }

  override val routeMap: Map[Identifier, Session => Call] = Map(
    AboutYouPageId                          -> (_ => controllers.aboutyouandtheproperty.routes.ContactDetailsQuestionController.show()),
    ContactDetailsQuestionId                -> contactDetailsQuestionRouting,
    AlternativeContactDetailsId             -> alternativeContactDetailsRouting,
    AboutThePropertyPageId                  -> aboutThePropertyDescriptionRouting,
    PropertyCurrentlyUsedPageId             -> (_ => controllers.aboutyouandtheproperty.routes.WebsiteForPropertyController.show()),
    WebsiteForPropertyPageId                -> websiteForPropertyRouting,
    CharityQuestionPageId                   -> charityQuestionRouting,
    TradingActivityPageId                   -> (_ =>
      controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
    ),
    PremisesLicenseGrantedId                -> premisesLicenseGrantedRouting,
    PremisesLicenseGrantedDetailsId         -> premisesLicenseGrantedDetailsRouting,
    LicensableActivityPageId                -> licensableActivityRouting,
    LicensableActivityDetailsPageId         -> (_ =>
      controllers.aboutyouandtheproperty.routes.PremisesLicenseConditionsController.show()
    ),
    PremisesLicenceConditionsPageId         -> premisesLicenceConditionsRouting,
    PremisesLicenceConditionsDetailsPageId  -> (_ =>
      controllers.aboutyouandtheproperty.routes.EnforcementActionBeenTakenController.show()
    ),
    EnforcementActionBeenTakenPageId        -> enforcementActionTakenRouting,
    EnforcementActionBeenTakenDetailsPageId -> (_ =>
      controllers.aboutyouandtheproperty.routes.TiedForGoodsController.show()
    ),
    TiedForGoodsPageId                      -> tiedGoodsRouting,
    TiedForGoodsDetailsPageId               -> (_ =>
      controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
    ),
    RenewablesPlantPageId                   -> (_ => controllers.aboutyouandtheproperty.routes.ThreeYearsConstructedController.show()),
    ThreeYearsConstructedPageId             -> threeYearsConstructedRouting,
    CostsBreakdownId                        -> (_ => controllers.aboutyouandtheproperty.routes.PlantAndTechnologyController.show()),
    PlantAndTechnologyId                    -> (_ => controllers.aboutyouandtheproperty.routes.GeneratorCapacityController.show()),
    GeneratorCapacityId                     -> (_ => controllers.aboutyouandtheproperty.routes.BatteriesCapacityController.show()),
    BatteriesCapacityId                     -> (_ =>
      controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
    ),
    CheckYourAnswersAboutThePropertyPageId  -> (_ => controllers.routes.TaskListController.show())
  )
}
