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
import controllers.aboutfranchisesorlettings
import models.submissions.aboutfranchisesorlettings.{CateringOperationBusinessSection, CateringOperationSection, LettingSection}
import models.{ForTypes, Session}
import navigation.identifiers._
import play.api.Logging
import play.api.mvc.Call

import javax.inject.Inject

class AboutFranchisesOrLettingsNavigator @Inject() (audit: Audit) extends Navigator(audit) with Logging {

  override def cyaPage: Option[Call] =
    Some(aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController.show())

  override val overrideRedirectIfFromCYA: Map[String, Session => Call] = Map(
    (
      aboutfranchisesorlettings.routes.ConcessionOrFranchiseController.show().url,
      _ => aboutfranchisesorlettings.routes.ConcessionOrFranchiseController.show()
    ),
    (
      aboutfranchisesorlettings.routes.CateringOperationController.show().url,
      _ => aboutfranchisesorlettings.routes.CateringOperationController.show()
    ),
    (
      aboutfranchisesorlettings.routes.ConcessionOrFranchiseController
        .show()
        .url,
      addAnotherCateringOperationsConditionsRouting
    ),
    (
      aboutfranchisesorlettings.routes.CateringOperationDetailsController
        .show()
        .url,
      addAnotherCateringOperationsConditionsRouting
    ),
    (
      aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyDetailsController
        .show()
        .url,
      lettingsRentIncludesConditionsRouting
    )
  )

  private def franchiseOrLettingConditionsRouting: Session => Call = answers => {
    answers.aboutFranchisesOrLettings.flatMap(_.franchisesOrLettingsTiedToProperty.map(_.name)) match {
      case Some("yes") =>
        answers.forType match {
          case ForTypes.for6015 | ForTypes.for6016 =>
            controllers.aboutfranchisesorlettings.routes.ConcessionOrFranchiseController.show()
          case ForTypes.for6030                    =>
            controllers.aboutfranchisesorlettings.routes.ConcessionOrFranchiseFeeController.show()
          case _                                   =>
            controllers.aboutfranchisesorlettings.routes.CateringOperationController.show()
        }
      case Some("no")  =>
        controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController.show()
      case _           =>
        logger.warn(
          s"Navigation for franchise or letting reached without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for franchise or letting conditions routing")
    }
  }

  private def concessionOrFranchiseFeeRouting: Session => Call = answers =>
    answers.aboutFranchisesOrLettings.flatMap(_.cateringOrFranchiseFee.map(_.name)) match {
      case Some("yes") => aboutfranchisesorlettings.routes.CateringOperationBusinessDetailsController.show()
      case _           => aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyController.show()
    }

  private def cateringOperationsConditionsRouting: Session => Call = answers => {
    answers.aboutFranchisesOrLettings.flatMap(_.cateringConcessionOrFranchise.map(_.name)) match {
      case Some("yes") =>
        answers.forType match {
          case ForTypes.for6010 | ForTypes.for6011 | ForTypes.for6015 | ForTypes.for6016 =>
            val maybeCatering = answers.aboutFranchisesOrLettings.flatMap(_.cateringOperationSections.lastOption)
            val idx           = getCateringOperationsIndex(answers)
            maybeCatering match {
              case Some(catering) if isCateringDetailsIncomplete(catering, answers.forType) =>
                getIncompleteCateringCall(catering, idx, answers.forType)
              case _                                                                        =>
                controllers.aboutfranchisesorlettings.routes.CateringOperationDetailsController.show(Some(idx))
            }
          case ForTypes.for6030                                                          =>
            val maybeCatering =
              answers.aboutFranchisesOrLettings.flatMap(
                _.cateringOperationBusinessSections.getOrElse(IndexedSeq.empty).lastOption
              )
            val idx           = getCateringOperationsIndex(answers)
            maybeCatering match {
              case Some(catering) if isCateringBusinessDetailsIncomplete6030(catering) =>
                getIncompleteBusinessCateringCall6030(catering, idx)
              case _                                                                   =>
                controllers.aboutfranchisesorlettings.routes.CateringOperationBusinessDetailsController.show(Some(idx))
            }
        }

      case Some("no") => controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyController.show()
      case _          =>
        logger.warn(
          s"Navigation for catering operations reached without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for catering operations conditions routing")
    }
  }

  private def getCateringOperationsIndex(session: Session): Int =
    session.aboutFranchisesOrLettings.map(_.cateringOperationCurrentIndex).getOrElse(0)

  private def isCateringDetailsIncomplete(detail: CateringOperationSection, forType: String): Boolean =
    if (forType.equals("FOR6015") || forType.equals("FOR6016")) {
      detail.cateringOperationDetails == null ||
      detail.rentReceivedFrom.isEmpty ||
      detail.calculatingTheRent.isEmpty ||
      detail.itemsInRent.isEmpty
    } else {
      detail.cateringOperationDetails == null ||
      detail.cateringOperationRentDetails.isEmpty ||
      detail.itemsInRent.isEmpty
    }

  private def isCateringBusinessDetailsIncomplete6030(detail: CateringOperationBusinessSection): Boolean =
    detail.feeReceived.isEmpty

  def getIncompleteCateringCall(detail: CateringOperationSection, idx: Int, forType: String): Call =
    if (forType.equals("FOR6015") || forType.equals("FOR6016")) {
      if (detail.rentReceivedFrom.isEmpty)
        controllers.aboutfranchisesorlettings.routes.RentReceivedFromController.show(idx)
      else if (detail.calculatingTheRent.isEmpty)
        controllers.aboutfranchisesorlettings.routes.CalculatingTheRentForController.show(idx)
      else if (detail.itemsInRent.isEmpty)
        controllers.aboutfranchisesorlettings.routes.CateringOperationRentIncludesController.show(idx)
      else controllers.aboutfranchisesorlettings.routes.CateringOperationDetailsController.show(Some(idx))
    } else {
      if (detail.cateringOperationRentDetails.isEmpty)
        controllers.aboutfranchisesorlettings.routes.CateringOperationDetailsRentController.show(idx)
      else if (detail.itemsInRent.isEmpty)
        controllers.aboutfranchisesorlettings.routes.CateringOperationRentIncludesController.show(idx)
      else controllers.aboutfranchisesorlettings.routes.CateringOperationDetailsController.show(Some(idx))
    }

  def getIncompleteBusinessCateringCall6030(detail: CateringOperationBusinessSection, idx: Int): Call =
    if (detail.feeReceived.isEmpty) {
      controllers.aboutfranchisesorlettings.routes.FeeReceivedController.show(idx)
    } else {
      controllers.aboutfranchisesorlettings.routes.CateringOperationBusinessDetailsController.show(Some(idx))
    }

  private def isLettingDetailIncomplete(detail: LettingSection, forType: String): Boolean = {
    val isCommonConditionMet   = detail.lettingOtherPartOfPropertyInformationDetails == null || detail.itemsInRent.isEmpty
    val isSpecificConditionMet = if (forType.equals("FOR6015") || forType.equals("FOR6016")) {
      detail.lettingOtherPartOfPropertyRent6015Details.isEmpty
    } else {
      detail.lettingOtherPartOfPropertyRentDetails.isEmpty
    }
    isCommonConditionMet || isSpecificConditionMet
  }
  def getIncompleteLettingCall(detail: LettingSection, forType: String, idx: Int): Call = {
    val for601516 = if (forType.equals("FOR6015") || forType.equals("FOR6016")) true else false
    if (detail.lettingOtherPartOfPropertyInformationDetails == null)
      controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyDetailsController.show(Some(idx))
    else if (detail.lettingOtherPartOfPropertyRent6015Details.isEmpty && for601516)
      controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyDetailsRentController.show(idx)
    else if (detail.lettingOtherPartOfPropertyRentDetails.isEmpty && (!for601516))
      controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyDetailsRentController.show(idx)
    else if (detail.itemsInRent.isEmpty)
      controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyRentIncludesController.show(idx)
    else controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyDetailsController.show(Some(idx))
  }

  private def cateringOperationsDetailsConditionsRouting: Session => Call = answers => {
    answers.forType match {
      case ForTypes.for6015 | ForTypes.for6016 =>
        controllers.aboutfranchisesorlettings.routes.RentReceivedFromController
          .show(getCateringOperationsIndex(answers))
      case _                                   =>
        controllers.aboutfranchisesorlettings.routes.CateringOperationDetailsRentController
          .show(getCateringOperationsIndex(answers))
    }
  }

  private def rentReceivedFromRouting: Session => Call = answers => {
    controllers.aboutfranchisesorlettings.routes.CalculatingTheRentForController
      .show(getCateringOperationsIndex(answers))
  }

  private def calculatingTheRentForRouting: Session => Call = answers => {
    controllers.aboutfranchisesorlettings.routes.CateringOperationRentIncludesController
      .show(getCateringOperationsIndex(answers))
  }

  private def cateringOperationsRentDetailsConditionsRouting: Session => Call = answers => {
    controllers.aboutfranchisesorlettings.routes.CateringOperationRentIncludesController
      .show(getCateringOperationsIndex(answers))
  }

  private def cateringOperationsRentIncludesConditionsRouting: Session => Call = answers => {
    controllers.aboutfranchisesorlettings.routes.AddAnotherCateringOperationController
      .show(getCateringOperationsIndex(answers))
  }

  private def addAnotherCateringOperationsConditionsRouting: Session => Call = answers => {
    def getLastCateringOperationIndex(session: Session): Option[Int] =
      session.aboutFranchisesOrLettings.flatMap { aboutFranchiseOrLettings =>
        aboutFranchiseOrLettings.cateringOperationSections.lastOption.map(_ =>
          aboutFranchiseOrLettings.cateringOperationSections.size
        )
      }
    val fromCYA                                                      =
      answers.aboutFranchisesOrLettings.flatMap(_.fromCYA).getOrElse(false)
    val existingSection                                              =
      answers.aboutFranchisesOrLettings.flatMap(_.cateringOperationSections.lift(getCateringOperationsIndex(answers)))
    existingSection match {
      case Some(existingSection) if isCateringDetailsIncomplete(existingSection, answers.forType) =>
        getIncompleteCateringCall(existingSection, getCateringOperationsIndex(answers), answers.forType)
      case None                                                                                   =>
        controllers.aboutfranchisesorlettings.routes.AddAnotherCateringOperationController
          .show(getCateringOperationsIndex(answers))
      case _                                                                                      =>
        existingSection.flatMap(_.addAnotherOperationToProperty).get.name match {
          case "yes" =>
            controllers.aboutfranchisesorlettings.routes.CateringOperationDetailsController
              .show(getLastCateringOperationIndex(answers))
          case "no"  =>
            if (fromCYA == true) {
              controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController.show()
            } else {
              controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyController.show()
            }
          case _     =>
            logger.warn(
              s"Navigation for add another catering operation reached without correct selection of conditions by controller"
            )
            throw new RuntimeException("Invalid option exception for add another catering operation conditions routing")
        }
    }
  }

  private def addAnotherConcessionRouting: Session => Call = answers => {
    def getLastCateringOperationConcessionIndex(session: Session): Option[Int] =
      session.aboutFranchisesOrLettings.flatMap { aboutFranchiseOrLettings =>
        aboutFranchiseOrLettings.cateringOperationSections.lastOption.map(_ =>
          aboutFranchiseOrLettings.cateringOperationSections.size
        )
      }
    val fromCYA                                                                =
      answers.aboutFranchisesOrLettings.flatMap(_.fromCYA).getOrElse(false)
    val existingSection                                                        =
      answers.aboutFranchisesOrLettings.flatMap(_.cateringOperationSections.lift(getCateringOperationsIndex(answers)))
    existingSection match {
      case Some(existingSection) if isCateringDetailsIncomplete(existingSection, answers.forType) =>
        getIncompleteCateringCall(existingSection, getCateringOperationsIndex(answers), answers.forType)
      case None                                                                                   =>
        controllers.aboutfranchisesorlettings.routes.AddAnotherCateringOperationController
          .show(getCateringOperationsIndex(answers))
      case _                                                                                      =>
        existingSection.flatMap(_.addAnotherOperationToProperty).get.name match {
          case "yes" =>
            controllers.aboutfranchisesorlettings.routes.CateringOperationDetailsController
              .show(getLastCateringOperationConcessionIndex(answers))
          case "no"  =>
            if (fromCYA == true) {
              controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController.show()
            } else {
              controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyController.show()
            }
          case _     =>
            logger.warn(
              s"Navigation for add another catering operation reached without correct selection of conditions by controller"
            )
            throw new RuntimeException("Invalid option exception for add another catering operation conditions routing")
        }
    }
  }

  private def rentForConcessionsRouting: Session => Call = answers => {
    answers.aboutFranchisesOrLettings.flatMap(_.cateringConcessionOrFranchise.map(_.name)) match {
      case Some("yes") => controllers.aboutfranchisesorlettings.routes.CateringOperationDetailsController.show()
      case Some("no")  => controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyController.show()
      case _           =>
        logger.warn(
          s"Navigation for catering operations reached without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for catering operations conditions routing")
    }
  }

  private def lettingAccommodationConditionsRouting: Session => Call = answers => {
    answers.aboutFranchisesOrLettings.flatMap(_.lettingOtherPartOfProperty.map(_.name)) match {
      case Some("yes") =>
        val maybeLetting = answers.aboutFranchisesOrLettings.flatMap(_.lettingSections.lastOption)
        val idx          = getLettingsIndex(answers)
        maybeLetting match {
          case Some(letting) if isLettingDetailIncomplete(letting, answers.forType) =>
            getIncompleteLettingCall(letting, answers.forType, idx)
          case _                                                                    =>
            controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyDetailsController.show(Some(idx))
        }
      case Some("no")  =>
        controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController.show()
      case _           =>
        logger.warn(
          s"Navigation for lettings reached without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for lettings conditions routing")
    }
  }

  private def getLettingsIndex(session: Session): Int =
    session.aboutFranchisesOrLettings.map(_.lettingCurrentIndex).getOrElse(0)

  private def lettingsDetailsConditionsRouting: Session => Call = answers => {
    controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyDetailsRentController
      .show(getLettingsIndex(answers))
  }

  private def lettingsRentDetailsConditionsRouting: Session => Call = answers => {
    controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyRentIncludesController
      .show(getLettingsIndex(answers))
  }

  private def lettingsRentIncludesConditionsRouting: Session => Call = answers => {
    controllers.aboutfranchisesorlettings.routes.AddAnotherLettingOtherPartOfPropertyController
      .show(getLettingsIndex(answers))
  }

  private def addAnotherLettingsConditionsRouting: Session => Call = answers => {
    val existingSection = answers.aboutFranchisesOrLettings.flatMap(_.lettingSections.lift(getLettingsIndex(answers)))
    existingSection match {
      case Some(letting) if isLettingDetailIncomplete(letting, answers.forType) =>
        getIncompleteLettingCall(letting, answers.forType, getLettingsIndex(answers))
      case _                                                                    =>
        existingSection.flatMap(_.addAnotherLettingToProperty).get.name match {
          case "yes" =>
            controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyDetailsController
              .show(Some(getLettingsIndex(answers) + 1))
          case "no"  =>
            controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController.show()
          case _     =>
            logger.warn(
              s"Navigation for add another letting reached without correct selection of conditions by controller"
            )
            throw new RuntimeException("Invalid option exception for add another letting conditions routing")
        }
    }
  }

  override val routeMap: Map[Identifier, Session => Call] = Map(
    FranchiseOrLettingsTiedToPropertyId        -> franchiseOrLettingConditionsRouting,
    ConcessionOrFranchiseFeePageId             -> concessionOrFranchiseFeeRouting,
    CateringOperationPageId                    -> cateringOperationsConditionsRouting,
    CateringOperationBusinessPageId            -> (answers =>
      aboutfranchisesorlettings.routes.FeeReceivedController.show(getCateringOperationsIndex(answers))
    ),
    FeeReceivedPageId                          -> (answers =>
      aboutfranchisesorlettings.routes.AddAnotherCateringOperationController.show(getCateringOperationsIndex(answers))
    ),
    CateringOperationDetailsPageId             -> cateringOperationsDetailsConditionsRouting,
    CateringOperationRentDetailsPageId         -> cateringOperationsRentDetailsConditionsRouting,
    CateringOperationRentIncludesPageId        -> cateringOperationsRentIncludesConditionsRouting,
    AddAnotherCateringOperationPageId          -> addAnotherCateringOperationsConditionsRouting,
    AddAnotherConcessionPageId                 -> addAnotherConcessionRouting,
    RentReceivedFromPageId                     -> rentReceivedFromRouting,
    CalculatingTheRentForPageId                -> calculatingTheRentForRouting,
    LettingAccommodationPageId                 -> lettingAccommodationConditionsRouting,
    LettingAccommodationDetailsPageId          -> lettingsDetailsConditionsRouting,
    LettingAccommodationRentDetailsPageId      -> lettingsRentDetailsConditionsRouting,
    LettingAccommodationRentIncludesPageId     -> lettingsRentIncludesConditionsRouting,
    AddAnotherLettingAccommodationPageId       -> addAnotherLettingsConditionsRouting,
    RentFromConcessionId                       -> rentForConcessionsRouting,
    MaxOfLettingsReachedCateringId             -> (_ =>
      controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyController.show()
    ),
    MaxOfLettingsReachedCurrentId              -> (_ =>
      controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController.show()
    ),
    CheckYourAnswersAboutFranchiseOrLettingsId -> (_ => controllers.routes.TaskListController.show())
  )
}
