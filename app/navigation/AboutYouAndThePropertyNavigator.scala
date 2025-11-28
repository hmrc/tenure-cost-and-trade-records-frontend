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
import models.submissions.common.AnswersYesNo.*
import models.ForType.*
import models.Session
import navigation.identifiers._
import play.api.Logging
import play.api.mvc.Call

import javax.inject.Inject

class AboutYouAndThePropertyNavigator @Inject() (audit: Audit) extends Navigator(audit) with Logging {

  override def cyaPage: Option[Call] =
    Some(controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show())

  override val postponeCYARedirectPages: Set[String] = Set(
    controllers.aboutyouandtheproperty.routes.CostsBreakdownController.show(),
    controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedDetailsController.show(),
    controllers.aboutyouandtheproperty.routes.LicensableActivitiesDetailsController.show(),
    controllers.aboutyouandtheproperty.routes.PremisesLicenseConditionsDetailsController.show(),
    controllers.aboutyouandtheproperty.routes.EnforcementActionBeenTakenDetailsController.show(),
    controllers.aboutyouandtheproperty.routes.TiedForGoodsDetailsController.show()
  ).map(_.url)

  private def commercialLettingQuestionRouting: Session => Call = answers =>
    if answers.isWelsh then
      controllers.aboutyouandtheproperty.routes.CommercialLettingAvailabilityWelshController.show()
    else controllers.aboutyouandtheproperty.routes.CommercialLettingAvailabilityController.show()

  private def aboutThePropertyDescriptionRouting: Session => Call = answers => {
    val answersForType = answers.forType
    if (answersForType.equals(FOR6020)) {
      controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
    } else
      controllers.aboutyouandtheproperty.routes.WebsiteForPropertyController.show()
  }

  private def websiteForPropertyRouting: Session => Call     = answers =>
    answers.forType match {
      case FOR6015 | FOR6016 =>
        controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedController.show()
      case FOR6030           => controllers.aboutyouandtheproperty.routes.CharityQuestionController.show()
      case FOR6045 | FOR6046 =>
        controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
      case _                 => controllers.aboutyouandtheproperty.routes.LicensableActivitiesController.show()

    }
  private def charityQuestionRouting: Session => Call        = answers =>
    answers.aboutYouAndTheProperty.flatMap(_.charityQuestion) match {
      case Some(AnswerYes) => controllers.aboutyouandtheproperty.routes.TradingActivityController.show()
      case _               => controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
    }
  private def premisesLicenseGrantedRouting: Session => Call = answers =>
    if (answers.forType.equals(FOR6015) || answers.forType.equals(FOR6016)) {
      answers.aboutYouAndTheProperty.flatMap(_.premisesLicenseGrantedDetail) match {
        case Some(AnswerYes) =>
          controllers.aboutyouandtheproperty.routes.PremisesLicenseGrantedDetailsController.show()
        case _               =>
          controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
      }
    } else {
      controllers.routes.LoginController.show
    }

  private def premisesLicenseGrantedDetailsRouting: Session => Call = answers =>
    if (answers.forType == FOR6015 || answers.forType == FOR6016)
      controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
    else
      controllers.aboutyouandtheproperty.routes.LicensableActivitiesController.show()

  private def licensableActivityRouting: Session => Call = answers =>
    answers.aboutYouAndTheProperty.flatMap(_.licensableActivities) match {
      case Some(AnswerYes) => controllers.aboutyouandtheproperty.routes.LicensableActivitiesDetailsController.show()
      case _               => controllers.aboutyouandtheproperty.routes.PremisesLicenseConditionsController.show()
    }

  private def premisesLicenceConditionsRouting: Session => Call = answers =>
    answers.aboutYouAndTheProperty.flatMap(_.premisesLicenseConditions) match {
      case Some(AnswerYes) =>
        controllers.aboutyouandtheproperty.routes.PremisesLicenseConditionsDetailsController.show()
      case _               => controllers.aboutyouandtheproperty.routes.EnforcementActionBeenTakenController.show()
    }

  private def enforcementActionTakenRouting: Session => Call = answers =>
    answers.aboutYouAndTheProperty.flatMap(_.enforcementAction) match {
      case Some(AnswerYes) =>
        controllers.aboutyouandtheproperty.routes.EnforcementActionBeenTakenDetailsController.show()
      case _               =>
        controllers.aboutyouandtheproperty.routes.TiedForGoodsController.show()
    }

  private def tiedGoodsRouting: Session => Call = answers =>
    answers.aboutYouAndTheProperty.flatMap(_.tiedForGoods) match {
      case Some(AnswerYes) => controllers.aboutyouandtheproperty.routes.TiedForGoodsDetailsController.show()
      case _               => controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
    }

  private def contactDetailsQuestionRouting: Session => Call =
    _.forType match {
      case FOR6020 | FOR6030 => controllers.aboutyouandtheproperty.routes.AboutThePropertyStringController.show()
      case FOR6076           => controllers.aboutyouandtheproperty.routes.RenewablesPlantController.show()
      case FOR6045 | FOR6046 => controllers.aboutyouandtheproperty.routes.PropertyCurrentlyUsedController.show()
      case FOR6048           => controllers.aboutyouandtheproperty.routes.CommercialLettingQuestionController.show()
      case _                 => controllers.aboutyouandtheproperty.routes.AboutThePropertyController.show()
    }

  private def completedCommercialLettingsRouting: Session => Call = _ =>
    controllers.aboutyouandtheproperty.routes.PartsUnavailableController.show()

  private def partsUnavailableRouting: Session => Call = answers =>
    answers.aboutYouAndThePropertyPartTwo.flatMap(_.partsUnavailable) match {
      case Some(AnswerYes) => controllers.aboutyouandtheproperty.routes.OccupiersDetailsController.show()
      case _               => controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()
    }

  private def getOccupiersIndex: Session => Int = answers =>
    answers.aboutYouAndThePropertyPartTwo.fold(0)(_.occupiersListIndex)

  private def occupiersDetailsLRouting: Session => Call = answers =>
    controllers.aboutyouandtheproperty.routes.OccupiersDetailsListController.show(getOccupiersIndex(answers))

  private def occupiersDetailsListRouting: Session => Call = answers =>
    answers.aboutYouAndThePropertyPartTwo.flatMap(_.addAnotherPaidService) match
      case Some(AnswerYes) => controllers.aboutyouandtheproperty.routes.OccupiersDetailsController.show()
      case _               => controllers.aboutyouandtheproperty.routes.CheckYourAnswersAboutThePropertyController.show()

  private def threeYearsConstructedRouting: Session => Call = answers =>
    answers.aboutYouAndTheProperty.flatMap(_.threeYearsConstructed) match {
      case Some(AnswerYes) => controllers.aboutyouandtheproperty.routes.CostsBreakdownController.show()
      case _               => controllers.aboutyouandtheproperty.routes.PlantAndTechnologyController.show()
    }

  override val routeMap: Map[Identifier, Session => Call] = Map(
    AboutYouPageId                          -> (_ => controllers.aboutyouandtheproperty.routes.ContactDetailsQuestionController.show()),
    ContactDetailsQuestionId                -> contactDetailsQuestionRouting,
    CommercialLettingQuestionId             -> commercialLettingQuestionRouting,
    CommercialLettingAvailabilityId         -> (_ =>
      controllers.aboutyouandtheproperty.routes.CompletedCommercialLettingsController.show()
    ),
    CommercialLettingAvailabilityWelshId    -> (_ =>
      controllers.aboutyouandtheproperty.routes.CompletedCommercialLettingsWelshController.show()
    ),
    CompletedCommercialLettingsId           -> completedCommercialLettingsRouting,
    CompletedCommercialLettingsWelshId      -> completedCommercialLettingsRouting,
    PartsUnavailableId                      -> partsUnavailableRouting,
    OccupiersDetailsId                      -> occupiersDetailsLRouting,
    OccupiersDetailsListId                  -> occupiersDetailsListRouting,
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
