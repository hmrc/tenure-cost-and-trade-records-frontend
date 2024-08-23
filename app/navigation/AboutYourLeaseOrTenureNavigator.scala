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
      aboutYourLeaseOrTenure.routes.CurrentRentPayableWithin12MonthsController.show().url,
      _ => aboutYourLeaseOrTenure.routes.CurrentRentPayableWithin12MonthsController.show()
    )
  )

  override val postponeCYARedirectPages: Set[String] = Set(
    aboutYourLeaseOrTenure.routes.ConnectedToLandlordDetailsController.show(),
    aboutYourLeaseOrTenure.routes.ThroughputAffectsRentDetailsController.show(),
    aboutYourLeaseOrTenure.routes.IsVATPayableForWholePropertyController.show(),
    aboutYourLeaseOrTenure.routes.IsRentUnderReviewController.show(),
    aboutYourLeaseOrTenure.routes.IncludedInRentParkingSpacesController.show(),
    aboutYourLeaseOrTenure.routes.RentedSeparatelyParkingSpacesController.show(),
    aboutYourLeaseOrTenure.routes.CarParkingAnnualRentController.show(),
    aboutYourLeaseOrTenure.routes.RentedEquipmentDetailsController.show(),
    aboutYourLeaseOrTenure.routes.RentIncludeTradeServicesDetailsController.show(),
    aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsDetailsController.show(),
    aboutYourLeaseOrTenure.routes.WhatIsYourRentBasedOnController.show(),
    aboutYourLeaseOrTenure.routes.RentPayableVaryAccordingToGrossOrNetDetailsController.show(),
    aboutYourLeaseOrTenure.routes.RentPayableVaryOnQuantityOfBeersDetailsController.show(),
    aboutYourLeaseOrTenure.routes.TenantsAdditionsDisregardedDetailsController.show(),
    aboutYourLeaseOrTenure.routes.LegalOrPlanningRestrictionsDetailsController.show()
  ).map(_.url)

  private def aboutYourLandlordRouting: Session => Call = answers =>
    answers.forType match {
      case ForTypes.for6010 | ForTypes.for6011 | ForTypes.for6015 | ForTypes.for6016 | ForTypes.for6030 |
          ForTypes.for6076 =>
        controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordController.show()
      case _ =>
        controllers.aboutYourLeaseOrTenure.routes.LeaseOrAgreementYearsController.show()
    }

  def connectedToLandlordRouting: Session => Call = answers =>
    answers.aboutLeaseOrAgreementPartOne.flatMap(_.connectedToLandlord.map(_.name)) match {
      case Some("yes") => controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordDetailsController.show()
      case Some("no")  =>
        answers.forType match {
          case ForTypes.for6011                                                          =>
            controllers.aboutYourLeaseOrTenure.routes.CurrentAnnualRentController.show()
          case ForTypes.for6020 | ForTypes.for6076 | ForTypes.for6045 | ForTypes.for6046 =>
            controllers.aboutYourLeaseOrTenure.routes.PropertyUseLeasebackArrangementController.show()
          case _                                                                         => controllers.aboutYourLeaseOrTenure.routes.LeaseOrAgreementYearsController.show()
        }
      case _           =>
        logger.warn(
          s"Navigation for connected to landlord reached without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for connected to landlord routing")
    }

  private def connectedToLandlordDetailsRouting: Session => Call = answers =>
    answers.forType match {
      case ForTypes.for6011                                                          =>
        controllers.aboutYourLeaseOrTenure.routes.CurrentAnnualRentController.show()
      case ForTypes.for6020 | ForTypes.for6076 | ForTypes.for6045 | ForTypes.for6046 =>
        controllers.aboutYourLeaseOrTenure.routes.PropertyUseLeasebackArrangementController.show()
      case _                                                                         =>
        controllers.aboutYourLeaseOrTenure.routes.LeaseOrAgreementYearsController.show()
    }

  private def currentAnnualRentRouting: Session => Call =
    _.forType match {
      case ForTypes.for6011 =>
        controllers.aboutYourLeaseOrTenure.routes.RentIncludesVatController.show()
      case ForTypes.for6020 =>
        controllers.aboutYourLeaseOrTenure.routes.ThroughputAffectsRentController.show()
      case _                =>
        controllers.aboutYourLeaseOrTenure.routes.CurrentRentFirstPaidController.show()
    }

  private def leaseOrAgreementDetailsRouting: Session => Call = answers =>
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

  private def currentRentFirstPaidRouting: Session => Call = answers =>
    if (answers.forType == ForTypes.for6011)
      controllers.aboutYourLeaseOrTenure.routes.TenancyLeaseAgreementExpireController.show()
    else
      controllers.aboutYourLeaseOrTenure.routes.CurrentLeaseOrAgreementBeginController.show()

  private def includedInYourRentRouting: Session => Call = answers =>
    answers.forType match {
      case ForTypes.for6020 =>
        if (
          answers.aboutLeaseOrAgreementPartOne
            .flatMap(_.includedInYourRentDetails)
            .exists(_.includedInYourRent contains "vat")
        ) {
          aboutYourLeaseOrTenure.routes.IsVATPayableForWholePropertyController.show()
        } else {
          aboutYourLeaseOrTenure.routes.UltimatelyResponsibleOutsideRepairsController.show()
        }
      case _                => aboutYourLeaseOrTenure.routes.DoesTheRentPayableController.show()
    }

  private def rentIncludeTradeServicesRouting: Session => Call = answers =>
    answers.aboutLeaseOrAgreementPartOne.flatMap(
      _.rentIncludeTradeServicesDetails.map(_.rentIncludeTradeServices.name)
    ) match {
      case Some("yes") =>
        answers.forType match {
          case ForTypes.for6020 | ForTypes.for6030 =>
            controllers.aboutYourLeaseOrTenure.routes.TradeServicesDescriptionController.show()
          case _                                   => controllers.aboutYourLeaseOrTenure.routes.RentIncludeTradeServicesDetailsController.show()
        }
      case Some("no")  =>
        answers.forType match {
          case ForTypes.for6020 | ForTypes.for6030 =>
            controllers.aboutYourLeaseOrTenure.routes.PaymentForTradeServicesController.show()
          case _                                   => controllers.aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsController.show()
        }
      case _           =>
        logger.warn(
          s"Navigation for rent include trade services reached without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for rent include trade services routing")
    }

  private def rentFixtureAndFittingsRouting: Session => Call = answers =>
    if (
      answers.aboutLeaseOrAgreementPartOne
        .flatMap(_.rentIncludeFixturesAndFittingsDetails)
        .map(_.rentIncludeFixturesAndFittingsDetails)
        .contains(AnswerYes)
    ) {
      answers.forType match {
        case ForTypes.for6020 => controllers.aboutYourLeaseOrTenure.routes.RentedEquipmentDetailsController.show()
        case _                => controllers.aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsDetailsController.show()
      }
    } else {
      answers.forType match {
        case ForTypes.for6020 => controllers.aboutYourLeaseOrTenure.routes.IncludedInRent6020Controller.show()
        case _                => controllers.aboutYourLeaseOrTenure.routes.RentOpenMarketValueController.show()
      }
    }

  private def rentRentOpenMarketRouting: Session => Call = answers =>
    answers.aboutLeaseOrAgreementPartOne.flatMap(_.rentOpenMarketValueDetails.map(_.rentOpenMarketValues.name)) match {
      case Some("yes") =>
        answers.forType match {
          case ForTypes.for6020 | ForTypes.for6045 | ForTypes.for6046 =>
            controllers.aboutYourLeaseOrTenure.routes.HowIsCurrentRentFixedController.show()
          case _                                                      => controllers.aboutYourLeaseOrTenure.routes.RentIncreaseAnnuallyWithRPIController.show()
        }
      case Some("no")  => controllers.aboutYourLeaseOrTenure.routes.WhatIsYourRentBasedOnController.show()
      case _           =>
        logger.warn(
          s"Navigation for rent open market reached without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for open market reached routing")
    }

  private def payableGrossOrNetRouting: Session => Call = answers =>
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

  private def payableGrossOrNetDetailsRouting: Session => Call = answers =>
    answers.forType match {
      case ForTypes.for6015 | ForTypes.for6016 | ForTypes.for6030 =>
        controllers.aboutYourLeaseOrTenure.routes.HowIsCurrentRentFixedController.show()
      case _                                                      =>
        controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryOnQuantityOfBeersController.show()
    }

  private def rentVaryQuantityOfBeersRouting: Session => Call = answers =>
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

  private def intervalsOfRentReviewRouting: Session => Call = answers =>
    answers.forType match {
      case ForTypes.for6020 =>
        if (
          answers.aboutLeaseOrAgreementPartTwo
            .flatMap(_.intervalsOfRentReview)
            .exists(_.intervalsOfRentReview.isDefined)
        ) {
          controllers.aboutYourLeaseOrTenure.routes.CanRentBeReducedOnReviewController.show()
        } else {
          controllers.aboutYourLeaseOrTenure.routes.IsRentUnderReviewController.show()
        }
      case _                => aboutYourLeaseOrTenure.routes.CanRentBeReducedOnReviewController.show()
    }

  private def canRentBeReducedRouting: Session => Call = answers =>
    answers.forType match {
      case ForTypes.for6020 => aboutYourLeaseOrTenure.routes.PropertyUpdatesController.show()
      case _                => aboutYourLeaseOrTenure.routes.IncentivesPaymentsConditionsController.show()
    }

  private def propertyUpdatesRouting: Session => Call = answers =>
    answers.aboutLeaseOrAgreementPartThree.flatMap(
      _.propertyUpdates.map(_.updates)
    ) match {
      case Some(AnswerYes) => aboutYourLeaseOrTenure.routes.WorkCarriedOutDetailsController.show()
      case Some(AnswerNo)  => aboutYourLeaseOrTenure.routes.WorkCarriedOutConditionController.show()
      case _               =>
        logger.warn(
          s"Navigation for property updates reached without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for property updates routing")
    }

  private def tenantsAdditionsDisregardedRouting: Session => Call = answers =>
    answers.aboutLeaseOrAgreementPartTwo.flatMap(
      _.tenantAdditionsDisregardedDetails.map(_.tenantAdditionalDisregarded.name)
    ) match {
      case Some("yes") => controllers.aboutYourLeaseOrTenure.routes.TenantsAdditionsDisregardedDetailsController.show()
      case Some("no")  =>
        answers.forType match {
          case ForTypes.for6020                    => controllers.aboutYourLeaseOrTenure.routes.LeaseSurrenderedEarlyController.show()
          case ForTypes.for6045 | ForTypes.for6046 =>
            controllers.aboutYourLeaseOrTenure.routes.PropertyUpdatesController.show()
          case _                                   => controllers.aboutYourLeaseOrTenure.routes.PayACapitalSumController.show()
        }
      case _           =>
        logger.warn(
          s"Navigation for tenants additions disregarded reached without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for tenants additions disregarded routing")
    }

  private def tenantsAdditionsDisregardedDetailsRouting: Session => Call = answers =>
    answers.forType match {
      case ForTypes.for6020                    => aboutYourLeaseOrTenure.routes.LeaseSurrenderedEarlyController.show()
      case ForTypes.for6045 | ForTypes.for6046 =>
        controllers.aboutYourLeaseOrTenure.routes.PropertyUpdatesController.show()
      case _                                   => aboutYourLeaseOrTenure.routes.PayACapitalSumController.show()
    }

  private def benefitsGivenRouting: Session => Call = answers =>
    answers.aboutLeaseOrAgreementPartThree.flatMap(
      _.benefitsGiven.map(_.benefitsGiven)
    ) match {
      case Some(AnswerYes) => controllers.aboutYourLeaseOrTenure.routes.BenefitsGivenDetailsController.show()
      case Some(AnswerNo)  =>
        controllers.aboutYourLeaseOrTenure.routes.PayACapitalSumController.show()
      case _               =>
        logger.warn(
          s"Navigation for benefits given page reached without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for legal or benefits given routing")
    }

  private def legalOrPlanningRestrictionRouting: Session => Call = answers =>
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

  private def payCapitalSumRouting: Session => Call = answers =>
    answers.aboutLeaseOrAgreementPartTwo.flatMap(
      _.payACapitalSumDetails.map(_.capitalSumOrPremium.name)
    ) match {
      case Some("yes") =>
        answers.forType match {
          case ForTypes.for6020 => controllers.aboutYourLeaseOrTenure.routes.CapitalSumDescriptionController.show()
          case ForTypes.for6030 => controllers.aboutYourLeaseOrTenure.routes.PayACapitalSumDetailsController.show()
          case _                => controllers.aboutYourLeaseOrTenure.routes.PaymentWhenLeaseIsGrantedController.show()
        }
      case _           =>
        answers.forType match {
          case ForTypes.for6020 =>
            controllers.aboutYourLeaseOrTenure.routes.LegalOrPlanningRestrictionsController.show()
          case _                => controllers.aboutYourLeaseOrTenure.routes.PaymentWhenLeaseIsGrantedController.show()
        }
    }

  private def RPIRouting: Session => Call = answers =>
    answers.forType match {
      case ForTypes.for6030                                       =>
        controllers.aboutYourLeaseOrTenure.routes.RentPayableVaryAccordingToGrossOrNetController.show()
      case ForTypes.for6020 | ForTypes.for6045 | ForTypes.for6046 =>
        controllers.aboutYourLeaseOrTenure.routes.HowIsCurrentRentFixedController.show()
      case _                                                      => controllers.aboutYourLeaseOrTenure.routes.RentIncreaseAnnuallyWithRPIController.show()
    }

  private def tradeServicesDescriptionRouting: Session => Call = answers =>
    controllers.aboutYourLeaseOrTenure.routes.TradeServicesListController.show(getIndexOfTradeServices(answers))

  private def servicePaidSeparatelyRouting: Session => Call = answers =>
    controllers.aboutYourLeaseOrTenure.routes.ServicePaidSeparatelyChargeController
      .show(getIndexOfPaidServices(answers))

  private def servicePaidSeparatelyChargeRouting: Session => Call = answers =>
    controllers.aboutYourLeaseOrTenure.routes.ServicePaidSeparatelyListController.show(getIndexOfPaidServices(answers))

  private def servicePaidSeparatelyListRouting: Session => Call = answers => {
    val existingSection =
      answers.aboutLeaseOrAgreementPartThree.flatMap(_.servicesPaid.lift(getIndexOfPaidServices(answers)))
    existingSection.flatMap(_.addAnotherPaidService) match {
      case Some(AnswerYes) =>
        controllers.aboutYourLeaseOrTenure.routes.ServicePaidSeparatelyController
          .show(Some(getIndexOfPaidServices(answers) + 1))
      case _               =>
        answers.forType match {
          case ForTypes.for6020 => aboutYourLeaseOrTenure.routes.DoesRentIncludeParkingController.show()
          case _                => aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsController.show()
        }
    }
  }

  private def tradeServicesListRouting: Session => Call = answers => {
    def getLastTradeServicesIndex(session: Session): Option[Int] =
      session.aboutLeaseOrAgreementPartThree.flatMap { aboutLeaseOrAgreementPartThree =>
        aboutLeaseOrAgreementPartThree.tradeServices.lastOption.map(_ =>
          aboutLeaseOrAgreementPartThree.tradeServices.size
        )
      }
    val existingSection                                          =
      answers.aboutLeaseOrAgreementPartThree.flatMap(_.tradeServices.lift(getIndexOfTradeServices(answers)))
    existingSection match {
      case None =>
        controllers.aboutYourLeaseOrTenure.routes.PaymentForTradeServicesController
          .show()
      case _    =>
        existingSection.flatMap(_.addAnotherService) match {
          case Some(AnswerYes) =>
            controllers.aboutYourLeaseOrTenure.routes.TradeServicesDescriptionController
              .show(getLastTradeServicesIndex(answers))
          case Some(AnswerNo)  =>
            controllers.aboutYourLeaseOrTenure.routes.PaymentForTradeServicesController.show()
          case _               =>
            logger.warn(
              s"Navigation for add another service reached without correct selection of conditions by controller"
            )
            throw new RuntimeException("Invalid option exception for add another service conditions routing")
        }
    }
  }

  private def paymentForTradeServicesRouting: Session => Call = answers =>
    answers.aboutLeaseOrAgreementPartThree.flatMap(_.paymentForTradeServices.map(_.paymentForTradeService)) match {
      case Some(AnswerYes) => aboutYourLeaseOrTenure.routes.ServicePaidSeparatelyController.show()
      case _               =>
        answers.forType match {
          case ForTypes.for6020 => aboutYourLeaseOrTenure.routes.DoesRentIncludeParkingController.show()
          case _                => aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsController.show()
        }
    }

  private def getIndexOfTradeServices(session: Session): Int =
    session.aboutLeaseOrAgreementPartThree.map(_.tradeServicesIndex).getOrElse(0)

  private def getIndexOfPaidServices(session: Session): Int =
    session.aboutLeaseOrAgreementPartThree.map(_.servicesPaidIndex).getOrElse(0)

  private def doesRentVaryToThroughputRouting: Session => Call =
    _.aboutLeaseOrAgreementPartThree.flatMap(_.throughputAffectsRent).map(_.doesRentVaryToThroughput) match {
      case Some(AnswerYes) => aboutYourLeaseOrTenure.routes.ThroughputAffectsRentDetailsController.show()
      case _               => aboutYourLeaseOrTenure.routes.CurrentRentFirstPaidController.show()
    }

  private def doesRentIncludeParkingRouting: Session => Call =
    _.aboutLeaseOrAgreementPartThree.flatMap(_.carParking).flatMap(_.doesRentIncludeParkingOrGarage) match {
      case Some(AnswerYes) => aboutYourLeaseOrTenure.routes.IncludedInRentParkingSpacesController.show()
      case _               => aboutYourLeaseOrTenure.routes.IsParkingRentPaidSeparatelyController.show()
    }

  private def isParkingRentPaidSeparatelyRouting: Session => Call =
    _.aboutLeaseOrAgreementPartThree.flatMap(_.carParking).flatMap(_.isRentPaidSeparately) match {
      case Some(AnswerYes) => aboutYourLeaseOrTenure.routes.RentedSeparatelyParkingSpacesController.show()
      case _               => aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsController.show()
    }

  // Form 6076 only
  private def provideDetailsOfYourLeaseRouting: Session => Call = answers =>
    answers.forType match {
      case ForTypes.for6076 =>
        controllers.aboutYourLeaseOrTenure.routes.CheckYourAnswersAboutYourLeaseOrTenureController.show()
      case _                =>
        controllers.aboutYourLeaseOrTenure.routes.LeaseOrAgreementYearsController.show()
    }

  private def doesRentPayableRouting: Session => Call = answers =>
    answers.forType match {
      case ForTypes.for6045 | ForTypes.for6046 =>
        controllers.aboutYourLeaseOrTenure.routes.RentDevelopedLandController.show()
      case _                                   =>
        controllers.aboutYourLeaseOrTenure.routes.UltimatelyResponsibleOutsideRepairsController.show()
    }

  private def propertyUseLeasebackAgreementRouting: Session => Call = answers =>
    answers.aboutLeaseOrAgreementPartOne.flatMap(
      _.propertyUseLeasebackAgreement.map(_.propertyUseLeasebackArrangement.name)
    ) match {
      case Some("yes") =>
        answers.forType match {
          case ForTypes.for6076 => controllers.aboutYourLeaseOrTenure.routes.ProvideDetailsOfYourLeaseController.show()
          case _                => aboutYourLeaseOrTenure.routes.CurrentAnnualRentController.show()
        }
      case Some("no")  =>
        answers.forType match {
          case ForTypes.for6076 => controllers.aboutYourLeaseOrTenure.routes.ProvideDetailsOfYourLeaseController.show()
          case _                => controllers.aboutYourLeaseOrTenure.routes.CurrentAnnualRentController.show()
        }
      case _           =>
        logger.warn(
          s"Navigation for property use leaseback agreement without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for property use leaseback agreement routing")
    }

  private def rentDevelopedLandRouting: Session => Call =
    _.aboutLeaseOrAgreementPartThree.flatMap(_.rentDevelopedLand) match {
      case Some(AnswerYes) => aboutYourLeaseOrTenure.routes.RentDevelopedLandDetailsController.show()
      case _               => aboutYourLeaseOrTenure.routes.RentOpenMarketValueController.show()
    }

  private def incentivesPaymentsConditionsRouting: Session => Call = answers =>
    answers.aboutLeaseOrAgreementPartTwo.flatMap(
      _.incentivesPaymentsConditionsDetails.map(_.formerLeaseSurrendered.name)
    ) match {
      case Some("yes") =>
        answers.forType match {
          case ForTypes.for6045 | ForTypes.for6046 =>
            controllers.aboutYourLeaseOrTenure.routes.SurrenderLeaseAgreementDetailsController.show()
          case _                                   => aboutYourLeaseOrTenure.routes.TenantsAdditionsDisregardedController.show()
        }
      case Some("no")  => controllers.aboutYourLeaseOrTenure.routes.TenantsAdditionsDisregardedController.show()
      case _           =>
        logger.warn(
          s"Navigation for incentive payments conditions without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for incentive payments conditions routing")
    }

  private def workCarriedOutConditionRouting: Session => Call = answers =>
    answers.forType match {
      case ForTypes.for6045 | ForTypes.for6046 =>
        controllers.aboutYourLeaseOrTenure.routes.BenefitsGivenController.show()
      case _                                   =>
        controllers.aboutYourLeaseOrTenure.routes.TenantsAdditionsDisregardedController.show()
    }

  override val routeMap: Map[Identifier, Session => Call] = Map(
    AboutTheLandlordPageId                        -> aboutYourLandlordRouting,
    ConnectedToLandlordPageId                     -> connectedToLandlordRouting,
    ConnectedToLandlordDetailsPageId              -> connectedToLandlordDetailsRouting,
    LeaseOrAgreementDetailsPageId                 -> leaseOrAgreementDetailsRouting,
    CurrentRentPayableWithin12monthsPageId        -> (_ =>
      aboutYourLeaseOrTenure.routes.CheckYourAnswersAboutYourLeaseOrTenureController.show()
    ),
    ProvideDetailsOfYourLeasePageId               -> provideDetailsOfYourLeaseRouting,
    PropertyUseLeasebackAgreementId               -> propertyUseLeasebackAgreementRouting,
    CurrentAnnualRentPageId                       -> currentAnnualRentRouting,
    CurrentRentFirstPaidPageId                    -> currentRentFirstPaidRouting,
    TenancyLeaseAgreementExpirePageId             -> (_ =>
      aboutYourLeaseOrTenure.routes.CheckYourAnswersAboutYourLeaseOrTenureController.show()
    ),
    CurrentLeaseBeginPageId                       -> (_ => aboutYourLeaseOrTenure.routes.IncludedInYourRentController.show()),
    IncludedInYourRentPageId                      -> includedInYourRentRouting,
    DoesRentPayablePageId                         -> doesRentPayableRouting,
    UltimatelyResponsibleInsideRepairsPageId      -> (_ =>
      aboutYourLeaseOrTenure.routes.UltimatelyResponsibleBuildingInsuranceController.show()
    ),
    UltimatelyResponsibleOutsideRepairsPageId     -> (_ =>
      aboutYourLeaseOrTenure.routes.UltimatelyResponsibleInsideRepairsController.show()
    ),
    UltimatelyResponsibleBusinessInsurancePageId  -> (_ =>
      aboutYourLeaseOrTenure.routes.RentIncludeTradeServicesController.show()
    ),
    RentIncludeTradeServicesPageId                -> rentIncludeTradeServicesRouting,
    RentIncludeTradeServicesDetailsPageId         -> (_ =>
      aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsController.show()
    ),
    RentIncludesVatPageId                         -> (_ => aboutYourLeaseOrTenure.routes.CurrentRentFirstPaidController.show()),
    RentFixtureAndFittingsPageId                  -> rentFixtureAndFittingsRouting,
    RentFixtureAndFittingsDetailsPageId           -> (_ => aboutYourLeaseOrTenure.routes.RentOpenMarketValueController.show()),
    RentOpenMarketPageId                          -> rentRentOpenMarketRouting,
    WhatRentBasedOnPageId                         -> RPIRouting,
    RentIncreaseByRPIPageId                       -> (_ =>
      aboutYourLeaseOrTenure.routes.RentPayableVaryAccordingToGrossOrNetController.show()
    ),
    RentPayableVaryAccordingToGrossOrNetId        -> payableGrossOrNetRouting,
    RentPayableVaryAccordingToGrossOrNetDetailsId -> payableGrossOrNetDetailsRouting,
    rentVaryQuantityOfBeersId                     -> rentVaryQuantityOfBeersRouting,
    rentVaryQuantityOfBeersDetailsId              -> (_ => aboutYourLeaseOrTenure.routes.HowIsCurrentRentFixedController.show()),
    HowIsCurrentRentFixedId                       -> (_ => aboutYourLeaseOrTenure.routes.MethodToFixCurrentRentController.show()),
    MethodToFixCurrentRentsId                     -> (_ => aboutYourLeaseOrTenure.routes.IntervalsOfRentReviewController.show()),
    IntervalsOfRentReviewId                       -> intervalsOfRentReviewRouting,
    CanRentBeReducedOnReviewId                    -> canRentBeReducedRouting,
    PropertyUpdatesId                             -> propertyUpdatesRouting,
    IncentivesPaymentsConditionsId                -> incentivesPaymentsConditionsRouting,
    TenantsAdditionsDisregardedId                 -> tenantsAdditionsDisregardedRouting,
    TenantsAdditionsDisregardedDetailsId          -> tenantsAdditionsDisregardedDetailsRouting,
    LeaseSurrenderedEarlyId                       -> (_ => aboutYourLeaseOrTenure.routes.BenefitsGivenController.show()),
    BenefitsGivenId                               -> benefitsGivenRouting,
    BenefitsGivenDetailsId                        -> (_ => aboutYourLeaseOrTenure.routes.PayACapitalSumController.show()),
    CapitalSumDescriptionId                       -> (_ => aboutYourLeaseOrTenure.routes.LegalOrPlanningRestrictionsController.show()),
    WorkCarriedOutDetailsId                       -> (_ => aboutYourLeaseOrTenure.routes.WorkCarriedOutConditionController.show()),
    WorkCarriedOutConditionId                     -> workCarriedOutConditionRouting,
    PayCapitalSumId                               -> payCapitalSumRouting,
    PayCapitalSumDetailsId                        -> (_ => aboutYourLeaseOrTenure.routes.PaymentWhenLeaseIsGrantedController.show()),
    PayWhenLeaseGrantedId                         -> (_ => aboutYourLeaseOrTenure.routes.LegalOrPlanningRestrictionsController.show()),
    LegalOrPlanningRestrictionId                  -> legalOrPlanningRestrictionRouting,
    LegalOrPlanningRestrictionDetailsId           -> (_ =>
      aboutYourLeaseOrTenure.routes.CheckYourAnswersAboutYourLeaseOrTenureController.show()
    ),
    TradeServicesDescriptionId                    -> tradeServicesDescriptionRouting,
    TradeServicesListId                           -> tradeServicesListRouting,
    ServicePaidSeparatelyId                       -> servicePaidSeparatelyRouting,
    ServicePaidSeparatelyChargeId                 -> servicePaidSeparatelyChargeRouting,
    ServicePaidSeparatelyListId                   -> servicePaidSeparatelyListRouting,
    PaymentForTradeServicesId                     -> paymentForTradeServicesRouting,
    TypeOfTenureId                                -> (_ => aboutYourLeaseOrTenure.routes.AboutYourLandlordController.show()),
    ThroughputAffectsRentId                       -> doesRentVaryToThroughputRouting,
    ThroughputAffectsRentDetailsId                -> (_ => aboutYourLeaseOrTenure.routes.CurrentRentFirstPaidController.show()),
    IsVATPayableForWholePropertyId                -> (_ =>
      aboutYourLeaseOrTenure.routes.UltimatelyResponsibleOutsideRepairsController.show()
    ),
    IsRentUnderReviewId                           -> (_ => aboutYourLeaseOrTenure.routes.CanRentBeReducedOnReviewController.show()),
    DoesRentIncludeParkingId                      -> doesRentIncludeParkingRouting,
    IncludedInRentParkingSpacesId                 -> (_ => aboutYourLeaseOrTenure.routes.IsParkingRentPaidSeparatelyController.show()),
    IsParkingRentPaidSeparatelyId                 -> isParkingRentPaidSeparatelyRouting,
    RentedSeparatelyParkingSpacesId               -> (_ => aboutYourLeaseOrTenure.routes.CarParkingAnnualRentController.show()),
    CarParkingAnnualRentId                        -> (_ => aboutYourLeaseOrTenure.routes.RentIncludeFixtureAndFittingsController.show()),
    RentedEquipmentDetailsId                      -> (_ => aboutYourLeaseOrTenure.routes.IncludedInRent6020Controller.show()),
    IncludedInRent6020Id                          -> (_ => aboutYourLeaseOrTenure.routes.RentOpenMarketValueController.show()),
    RentDevelopedLandId                           -> rentDevelopedLandRouting,
    RentDevelopedLandDetailsId                    -> (_ => aboutYourLeaseOrTenure.routes.RentIncludeStructuresBuildingsController.show()),
    RentIncludeStructuresBuildingsId              -> (_ =>
      aboutYourLeaseOrTenure.routes.RentIncludeStructuresBuildingsDetailsController.show()
    ),
    RentIncludeStructuresBuildingsDetailsId       -> (_ =>
      aboutYourLeaseOrTenure.routes.UltimatelyResponsibleOutsideRepairsController.show()
    ),
    SurrenderedLeaseAgreementDetailsId            -> (_ =>
      aboutYourLeaseOrTenure.routes.TenantsAdditionsDisregardedController.show()
    ),
    CheckYourAnswersAboutYourLeaseOrTenureId      -> (_ =>
      controllers.routes.TaskListController.show().withFragment("leaseOrAgreement")
    )
  )
}
