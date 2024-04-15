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
import controllers.aboutYourLeaseOrTenure
import models.submissions.common.{AnswerNo, AnswerYes}
import models.{ForTypes, Session}
import navigation.identifiers._
import play.api.Logging
import play.api.mvc.Call

import javax.inject.Inject

class AboutYourLeaseOrTenureNavigator @Inject() (audit: Audit) extends Navigator(audit) with Logging {

  override def cyaPage: Option[Call] =
    Some(aboutYourLeaseOrTenure.routes.CheckYourAnswersAboutYourLeaseOrTenureController.show())

  override val overrideRedirectIfFromCYA: Map[String, Session => Call] = Map(
    (
      aboutYourLeaseOrTenure.routes.PropertyUseLeasebackArrangementController.show().url,
      _ => aboutYourLeaseOrTenure.routes.PropertyUseLeasebackArrangementController.show()
    ),
    (
      aboutYourLeaseOrTenure.routes.CurrentRentPayableWithin12MonthsController.show().url,
      _ => aboutYourLeaseOrTenure.routes.CurrentRentPayableWithin12MonthsController.show()
    )
  )

  override val postponeCYARedirectPages: Set[String] = Set(
    aboutYourLeaseOrTenure.routes.ConnectedToLandlordDetailsController.show(),
    aboutYourLeaseOrTenure.routes.RentIncludeTradeServicesDetailsController.show(),
    aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsDetailsController.show(),
    aboutYourLeaseOrTenure.routes.WhatIsYourRentBasedOnController.show(),
    aboutYourLeaseOrTenure.routes.RentPayableVaryAccordingToGrossOrNetDetailsController.show(),
    aboutYourLeaseOrTenure.routes.RentPayableVaryOnQuantityOfBeersDetailsController.show(),
    aboutYourLeaseOrTenure.routes.TenantsAdditionsDisregardedDetailsController.show(),
    aboutYourLeaseOrTenure.routes.LegalOrPlanningRestrictionsDetailsController.show()
  ).map(_.url)

  private def aboutYourLandlordRouting: Session => Call = answers => {
    answers.forType match {
      case ForTypes.for6010 | ForTypes.for6011 | ForTypes.for6015 | ForTypes.for6016 | ForTypes.for6030 =>
        controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordController.show()
      case _                                                                                            =>
        controllers.aboutYourLeaseOrTenure.routes.LeaseOrAgreementYearsController.show()
    }
  }

  def connectedToLandlordRouting: Session => Call = answers => {
    answers.aboutLeaseOrAgreementPartOne.flatMap(_.connectedToLandlord.map(_.name)) match {
      case Some("yes") => controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordDetailsController.show()
      case Some("no")  =>
        answers.forType match {
          case ForTypes.for6011 => controllers.aboutYourLeaseOrTenure.routes.CurrentAnnualRentController.show()
          case ForTypes.for6020 =>
            controllers.aboutYourLeaseOrTenure.routes.PropertyUseLeasebackArrangementController.show()
          case _                => controllers.aboutYourLeaseOrTenure.routes.LeaseOrAgreementYearsController.show()
        }
      case _           =>
        logger.warn(
          s"Navigation for connected to landlord reached without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for connected to landlord routing")
    }
  }

  private def connectedToLandlordDetailsRouting: Session => Call = answers => {
    answers.forType match {
      case ForTypes.for6011 =>
        controllers.aboutYourLeaseOrTenure.routes.CurrentAnnualRentController.show()
      case ForTypes.for6020 =>
        controllers.aboutYourLeaseOrTenure.routes.PropertyUseLeasebackArrangementController.show()
      case _                =>
        controllers.aboutYourLeaseOrTenure.routes.LeaseOrAgreementYearsController.show()
    }
  }

  private def currentAnnualRentRouting: Session => Call = answers => {
    answers.forType match {
      case ForTypes.for6011 =>
        controllers.aboutYourLeaseOrTenure.routes.RentIncludesVatController.show()
      case _                =>
        controllers.aboutYourLeaseOrTenure.routes.CurrentRentFirstPaidController.show()
    }
  }

  private def leaseOrAgreementDetailsRouting: Session => Call = answers => {
    (
      answers.aboutLeaseOrAgreementPartOne.flatMap(_.leaseOrAgreementYearsDetails.map(_.commenceWithinThreeYears.name)),
      answers.aboutLeaseOrAgreementPartOne.flatMap(
        _.leaseOrAgreementYearsDetails.map(_.agreedReviewedAlteredThreeYears.name)
      ),
      answers.aboutLeaseOrAgreementPartOne.flatMap(_.leaseOrAgreementYearsDetails.map(_.rentUnderReviewNegotiated.name))
    ) match {
      case (Some("no"), Some("no"), Some("no")) =>
        controllers.aboutYourLeaseOrTenure.routes.CurrentRentPayableWithin12MonthsController.show()
      case _                                    => controllers.aboutYourLeaseOrTenure.routes.PropertyUseLeasebackArrangementController.show()
    }
  }

  private def currentRentFirstPaidRouting: Session => Call = answers => {
    if (answers.forType == ForTypes.for6011)
      controllers.aboutYourLeaseOrTenure.routes.TenancyLeaseAgreementExpireController.show()
    else
      controllers.aboutYourLeaseOrTenure.routes.CurrentLeaseOrAgreementBeginController.show()
  }

  private def rentIncludeTradeServicesRouting: Session => Call = answers => {
    answers.aboutLeaseOrAgreementPartOne.flatMap(
      _.rentIncludeTradeServicesDetails.map(_.rentIncludeTradeServices.name)
    ) match {
      case Some("yes") =>
        answers.forType match {
          case ForTypes.for6030 => controllers.aboutYourLeaseOrTenure.routes.TradeServicesDescriptionController.show()
          case _                => controllers.aboutYourLeaseOrTenure.routes.RentIncludeTradeServicesDetailsController.show()
        }
      case Some("no")  =>
        answers.forType match {
          case ForTypes.for6030 => controllers.aboutYourLeaseOrTenure.routes.PaymentForTradeServicesController.show()
          case _                => controllers.aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsController.show()
        }
      case _           =>
        logger.warn(
          s"Navigation for rent include trade services reached without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for rent include trade services routing")
    }
  }

  private def rentFixtureAndFittingsRouting: Session => Call = answers => {
    answers.aboutLeaseOrAgreementPartOne.flatMap(
      _.rentIncludeFixturesAndFittingsDetails.map(_.rentIncludeFixturesAndFittingsDetails.name)
    ) match {
      case Some("yes") =>
        controllers.aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsDetailsController.show()
      case Some("no")  => controllers.aboutYourLeaseOrTenure.routes.RentOpenMarketValueController.show()
      case _           =>
        logger.warn(
          s"Navigation for fixture and fittings reached without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for fixture and fittings routing")
    }
  }

  private def rentRentOpenMarketRouting: Session => Call = answers => {
    answers.aboutLeaseOrAgreementPartOne.flatMap(_.rentOpenMarketValueDetails.map(_.rentOpenMarketValues.name)) match {
      case Some("yes") => controllers.aboutYourLeaseOrTenure.routes.RentIncreaseAnnuallyWithRPIController.show()
      case Some("no")  => controllers.aboutYourLeaseOrTenure.routes.WhatIsYourRentBasedOnController.show()
      case _           =>
        logger.warn(
          s"Navigation for rent open market reached without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for open market reached routing")
    }
  }

  private def payableGrossOrNetRouting: Session => Call = answers => {
    answers.aboutLeaseOrAgreementPartTwo.flatMap(
      _.rentPayableVaryAccordingToGrossOrNetDetails.map(_.rentPayableVaryAccordingToGrossOrNets.name)
    ) match {
      case Some("yes") =>
        controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryAccordingToGrossOrNetDetailsController.show()
      case Some("no")  =>
        answers.forType match {
          case ForTypes.for6010 =>
            controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryOnQuantityOfBeersController.show()
          case _                => controllers.aboutYourLeaseOrTenure.routes.HowIsCurrentRentFixedController.show()
        }
      case _           =>
        logger.warn(
          s"Navigation for rent payable by gross or net turnover without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for rent payable by gross or net turnover routing")
    }
  }

  private def payableGrossOrNetDetailsRouting: Session => Call = answers => {
    answers.forType match {
      case ForTypes.for6015 | ForTypes.for6016 | ForTypes.for6030 =>
        controllers.aboutYourLeaseOrTenure.routes.HowIsCurrentRentFixedController.show()
      case _                                                      =>
        controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryOnQuantityOfBeersController.show()
    }
  }

  private def rentVaryQuantityOfBeersRouting: Session => Call = answers => {
    answers.aboutLeaseOrAgreementPartTwo.flatMap(
      _.rentPayableVaryOnQuantityOfBeersDetails.map(_.rentPayableVaryOnQuantityOfBeersDetails.name)
    ) match {
      case Some("yes") =>
        controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryOnQuantityOfBeersDetailsController.show()
      case Some("no")  => controllers.aboutYourLeaseOrTenure.routes.HowIsCurrentRentFixedController.show()
      case _           =>
        logger.warn(
          s"Navigation for rent payable vary quantity of beer without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for rent payable vary quantity of beer routing")
    }
  }

  private def tenantsAdditionsDisregardedRouting: Session => Call = answers => {
    answers.aboutLeaseOrAgreementPartTwo.flatMap(
      _.tenantAdditionsDisregardedDetails.map(_.tenantAdditionalDisregarded.name)
    ) match {
      case Some("yes") => controllers.aboutYourLeaseOrTenure.routes.TenantsAdditionsDisregardedDetailsController.show()
      case Some("no")  => controllers.aboutYourLeaseOrTenure.routes.PayACapitalSumController.show()
      case _           =>
        logger.warn(
          s"Navigation for tenants additions disregarded reached without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for tenants additions disregarded routing")
    }
  }

  private def legalOrPlanningRestrictionRouting: Session => Call = answers => {
    answers.aboutLeaseOrAgreementPartTwo.flatMap(
      _.legalOrPlanningRestrictions.map(_.legalPlanningRestrictions.name)
    ) match {
      case Some("yes") => controllers.aboutYourLeaseOrTenure.routes.LegalOrPlanningRestrictionsDetailsController.show()
      case Some("no")  =>
        controllers.aboutYourLeaseOrTenure.routes.CheckYourAnswersAboutYourLeaseOrTenureController.show()
      case _           =>
        logger.warn(
          s"Navigation for legal or planning restriction without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for legal or planning restriction routing")
    }
  }

  private def payCapitalSumRouting: Session => Call = answers => {
    answers.aboutLeaseOrAgreementPartTwo.flatMap(
      _.payACapitalSumDetails.map(_.capitalSumOrPremium.name)
    ) match {
      case Some("yes") =>
        answers.forType match {
          case ForTypes.for6030 => controllers.aboutYourLeaseOrTenure.routes.PayACapitalSumDetailsController.show()
          case _                => controllers.aboutYourLeaseOrTenure.routes.PaymentWhenLeaseIsGrantedController.show()
        }
      case Some("no")  =>
        controllers.aboutYourLeaseOrTenure.routes.PaymentWhenLeaseIsGrantedController.show()
      case _           =>
        logger.warn(
          s"Navigation for pay capital sum without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for pay capital sum routing")
    }
  }

  private def RPIRouting: Session => Call = answers => {
    answers.forType match {
      case ForTypes.for6030 =>
        controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryAccordingToGrossOrNetController.show()
      case _                => controllers.aboutYourLeaseOrTenure.routes.RentIncreaseAnnuallyWithRPIController.show()
    }

  }
  private def tradeServicesDescriptionRouting: Session => Call = answers => {
    controllers.aboutYourLeaseOrTenure.routes.TradeServicesListController.show(getIndexOfTradeServices(answers))
  }
  private def servicePaidSeparatelyRouting: Session => Call    = answers => {
    controllers.aboutYourLeaseOrTenure.routes.ServicePaidSeparatelyChargeController
      .show(getIndexOfPaidServices(answers))
  }

  private def servicePaidSeparatelyChargeRouting: Session => Call = answers => {
    controllers.aboutYourLeaseOrTenure.routes.ServicePaidSeparatelyListController.show(getIndexOfPaidServices(answers))
  }

  private def servicePaidSeparatelyListRouting: Session => Call = answers => {
    val existingSection =
      answers.aboutLeaseOrAgreementPartThree.flatMap(_.servicesPaid.lift(getIndexOfPaidServices(answers)))
    existingSection.flatMap(_.addAnotherPaidService) match {
      case Some(AnswerYes) =>
        controllers.aboutYourLeaseOrTenure.routes.ServicePaidSeparatelyController
          .show(Some(getIndexOfPaidServices(answers) + 1))
      case Some(AnswerNo)  =>
        controllers.aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsController.show()
      case _               =>
        logger.warn(
          s"Navigation for add another service paid separately reached without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for add another service conditions routing")
    }

  }
  private def tradeServicesListRouting: Session => Call         = answers => {
    val existingSection =
      answers.aboutLeaseOrAgreementPartThree.flatMap(_.tradeServices.lift(getIndexOfTradeServices(answers)))
    existingSection.flatMap(_.addAnotherService) match {
      case Some(AnswerYes) =>
        controllers.aboutYourLeaseOrTenure.routes.TradeServicesDescriptionController
          .show(Some(getIndexOfTradeServices(answers) + 1))
      case Some(AnswerNo)  =>
        controllers.aboutYourLeaseOrTenure.routes.PaymentForTradeServicesController.show()
      case _               =>
        logger.warn(
          s"Navigation for add another service reached without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for add another service conditions routing")
    }

  }
  private def paymentForTradeServicesRouting: Session => Call   = answers => {
    answers.aboutLeaseOrAgreementPartThree.flatMap(_.paymentForTradeServices.map(_.paymentForTradeService)) match {
      case Some(AnswerYes) => controllers.aboutYourLeaseOrTenure.routes.ServicePaidSeparatelyController.show()
      case Some(AnswerNo)  => controllers.aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsController.show()
      case _               =>
        logger.warn(
          s"Navigation for payment for trade services reached without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for payment for trade services")
    }
  }

  private def getIndexOfTradeServices(session: Session): Int =
    session.aboutLeaseOrAgreementPartThree.map(_.tradeServicesIndex).getOrElse(0)

  private def getIndexOfPaidServices(session: Session): Int =
    session.aboutLeaseOrAgreementPartThree.map(_.servicesPaidIndex).getOrElse(0)

  private def doesRentIncludeParkingRouting: Session => Call =
    _.aboutLeaseOrAgreementPartThree.flatMap(_.carParking).flatMap(_.doesRentIncludeParkingOrGarage) match {
      case Some(AnswerYes) => controllers.aboutYourLeaseOrTenure.routes.DoesRentIncludeParkingController.show() // TODO: IncludedInRentParkingSpacesController
      case _  => controllers.aboutYourLeaseOrTenure.routes.IsParkingRentPaidSeparatelyController.show()
    }

  private def isParkingRentPaidSeparatelyRouting: Session => Call =
    _.aboutLeaseOrAgreementPartThree.flatMap(_.carParking).flatMap(_.isRentPaidSeparately) match {
      case Some(AnswerYes) => controllers.aboutYourLeaseOrTenure.routes.IsParkingRentPaidSeparatelyController.show() // TODO: RentedSeparatelyParkingSpacesController
      case _  => controllers.aboutYourLeaseOrTenure.routes.CheckYourAnswersAboutYourLeaseOrTenureController.show() // TODO: any equipment?
    }

  override val routeMap: Map[Identifier, Session => Call] = Map(
    AboutTheLandlordPageId                        -> aboutYourLandlordRouting,
    ConnectedToLandlordPageId                     -> connectedToLandlordRouting,
    ConnectedToLandlordDetailsPageId              -> connectedToLandlordDetailsRouting,
    LeaseOrAgreementDetailsPageId                 -> leaseOrAgreementDetailsRouting,
    CurrentRentPayableWithin12monthsPageId        -> (_ =>
      controllers.aboutYourLeaseOrTenure.routes.CheckYourAnswersAboutYourLeaseOrTenureController.show()
    ),
    PropertyUseLeasebackAgreementId               -> (_ =>
      controllers.aboutYourLeaseOrTenure.routes.CurrentAnnualRentController.show()
    ),
    CurrentAnnualRentPageId                       -> currentAnnualRentRouting,
    CurrentRentFirstPaidPageId                    -> currentRentFirstPaidRouting,
    TenancyLeaseAgreementExpirePageId             -> (_ =>
      controllers.aboutYourLeaseOrTenure.routes.CheckYourAnswersAboutYourLeaseOrTenureController.show()
    ),
    CurrentLeaseBeginPageId                       -> (_ => controllers.aboutYourLeaseOrTenure.routes.IncludedInYourRentController.show()),
    IncludedInYourRentPageId                      -> (_ => controllers.aboutYourLeaseOrTenure.routes.DoesTheRentPayableController.show()),
    DoesRentPayablePageId                         -> (_ =>
      controllers.aboutYourLeaseOrTenure.routes.UltimatelyResponsibleInsideRepairsController.show()
    ),
    UltimatelyResponsibleInsideRepairsPageId      -> (_ =>
      controllers.aboutYourLeaseOrTenure.routes.UltimatelyResponsibleOutsideRepairsController.show()
    ),
    UltimatelyResponsibleOutsideRepairsPageId     -> (_ =>
      controllers.aboutYourLeaseOrTenure.routes.UltimatelyResponsibleBuildingInsuranceController.show()
    ),
    UltimatelyResponsibleBusinessInsurancePageId  -> (_ =>
      controllers.aboutYourLeaseOrTenure.routes.RentIncludeTradeServicesController.show()
    ),
    RentIncludeTradeServicesPageId                -> rentIncludeTradeServicesRouting,
    RentIncludeTradeServicesDetailsPageId         -> (_ =>
      controllers.aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsController.show()
    ),
    RentIncludesVatPageId                         -> (_ => controllers.aboutYourLeaseOrTenure.routes.CurrentRentFirstPaidController.show()),
    RentFixtureAndFittingsPageId                  -> rentFixtureAndFittingsRouting,
    RentFixtureAndFittingsDetailsPageId           -> (_ =>
      controllers.aboutYourLeaseOrTenure.routes.RentOpenMarketValueController.show()
    ),
    RentOpenMarketPageId                          -> rentRentOpenMarketRouting,
    WhatRentBasedOnPageId                         -> RPIRouting,
    RentIncreaseByRPIPageId                       -> (_ =>
      controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryAccordingToGrossOrNetController.show()
    ),
    RentPayableVaryAccordingToGrossOrNetId        -> payableGrossOrNetRouting,
    RentPayableVaryAccordingToGrossOrNetDetailsId -> payableGrossOrNetDetailsRouting,
    rentVaryQuantityOfBeersId                     -> rentVaryQuantityOfBeersRouting,
    rentVaryQuantityOfBeersDetailsId              -> (_ =>
      controllers.aboutYourLeaseOrTenure.routes.HowIsCurrentRentFixedController.show()
    ),
    HowIsCurrentRentFixedId                       -> (_ => controllers.aboutYourLeaseOrTenure.routes.MethodToFixCurrentRentController.show()),
    MethodToFixCurrentRentsId                     -> (_ =>
      controllers.aboutYourLeaseOrTenure.routes.IntervalsOfRentReviewController.show()
    ),
    IntervalsOfRentReviewId                       -> (_ =>
      controllers.aboutYourLeaseOrTenure.routes.CanRentBeReducedOnReviewController.show()
    ),
    CanRentBeReducedOnReviewId                    -> (_ =>
      controllers.aboutYourLeaseOrTenure.routes.IncentivesPaymentsConditionsController.show()
    ),
    IncentivesPaymentsConditionsId                -> (_ =>
      controllers.aboutYourLeaseOrTenure.routes.TenantsAdditionsDisregardedController.show()
    ),
    TenantsAdditionsDisregardedId                 -> tenantsAdditionsDisregardedRouting,
    TenantsAdditionsDisregardedDetailsId          -> (_ =>
      controllers.aboutYourLeaseOrTenure.routes.PayACapitalSumController.show()
    ),
    PayCapitalSumId                               -> payCapitalSumRouting,
    PayCapitalSumDetailsId                        -> (_ =>
      controllers.aboutYourLeaseOrTenure.routes.PaymentWhenLeaseIsGrantedController.show()
    ),
    PayWhenLeaseGrantedId                         -> (_ =>
      controllers.aboutYourLeaseOrTenure.routes.LegalOrPlanningRestrictionsController.show()
    ),
    LegalOrPlanningRestrictionId                  -> legalOrPlanningRestrictionRouting,
    LegalOrPlanningRestrictionDetailsId           -> (_ =>
      controllers.aboutYourLeaseOrTenure.routes.CheckYourAnswersAboutYourLeaseOrTenureController.show()
    ),
    TradeServicesDescriptionId                    -> tradeServicesDescriptionRouting,
    TradeServicesListId                           -> tradeServicesListRouting,
    ServicePaidSeparatelyId                       -> servicePaidSeparatelyRouting,
    ServicePaidSeparatelyChargeId                 -> servicePaidSeparatelyChargeRouting,
    ServicePaidSeparatelyListId                   -> servicePaidSeparatelyListRouting,
    PaymentForTradeServicesId                     -> paymentForTradeServicesRouting,
    TypeOfTenureId                                -> (_ => controllers.aboutYourLeaseOrTenure.routes.AboutYourLandlordController.show()),
    DoesRentIncludeParkingId                      -> doesRentIncludeParkingRouting,

    IsParkingRentPaidSeparatelyId                 -> isParkingRentPaidSeparatelyRouting,

    CheckYourAnswersAboutYourLeaseOrTenureId      -> (_ => controllers.routes.TaskListController.show())
  )
}
