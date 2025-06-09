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
import controllers.aboutfranchisesorlettings
import models.submissions.aboutfranchisesorlettings.LettingSection
import models.ForType
import models.ForType.*
import models.Session
import models.submissions.common.AnswerYes
import navigation.identifiers.*
import play.api.Logging
import play.api.mvc.Call

import javax.inject.Inject

class AboutFranchisesOrLettingsNavigator @Inject() (audit: Audit) extends Navigator(audit) with Logging {

  override def cyaPage: Option[Call] =
    Some(aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController.show())

  override val overrideRedirectIfFromCYA: Map[String, Session => Call] = Map(
    (
      aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyController
        .show()
        .url,
      addAnotherLettingsConditionsRouting
    )
  )

  private def franchiseOrLettingConditionsRouting: Session => Call = answers =>
    answers.aboutFranchisesOrLettings.flatMap(_.franchisesOrLettingsTiedToProperty) match {
      case Some(AnswerYes) =>
        answers.forType match {
          case FOR6020 =>
            val idx: Int = answers.aboutFranchisesOrLettings.fold(0)(_.lettings.fold(0)(_.size))
            controllers.aboutfranchisesorlettings.routes.TypeOfLettingController.show(Option(idx))
          case _       =>
            controllers.aboutfranchisesorlettings.routes.TypeOfIncomeController.show()
        }
      case _               =>
        controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController.show()
    }

  private def getRentalIncomeIndex(session: Session): Int =
    session.aboutFranchisesOrLettings.map(_.rentalIncomeIndex).getOrElse(0)

  private def isLettingDetailIncomplete(detail: LettingSection, forType: ForType): Boolean = {
    val isCommonConditionMet   = detail.lettingOtherPartOfPropertyInformationDetails == null || detail.itemsInRent.isEmpty
    val isSpecificConditionMet = if (forType == FOR6015 || forType == FOR6016) {
      detail.lettingOtherPartOfPropertyRent6015Details.isEmpty
    } else {
      detail.lettingOtherPartOfPropertyRentDetails.isEmpty
    }
    isCommonConditionMet || isSpecificConditionMet
  }
  def getIncompleteLettingCall(detail: LettingSection, forType: ForType, idx: Int): Call   = {
    val for601516 = if (forType == FOR6015 || forType == FOR6016) true else false
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

  private def franchiseTypeDetailsRouting: Session => Call = answers =>
    answers.forType match {
      case FOR6015 | FOR6016 =>
        controllers.aboutfranchisesorlettings.routes.RentReceivedFromController
          .show(getRentalIncomeIndex(answers))
      case _                 =>
        controllers.aboutfranchisesorlettings.routes.RentalIncomeRentController
          .show(getRentalIncomeIndex(answers))
    }

  private def lettingAccommodationConditionsRouting: Session => Call = answers =>
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
      case _           =>
        controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController.show()
    }

  private def getLettingsIndex(session: Session): Int =
    session.aboutFranchisesOrLettings.map(_.lettingCurrentIndex).getOrElse(0)

  private def lettingsDetailsConditionsRouting: Session => Call = answers =>
    controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyDetailsRentController
      .show(getLettingsIndex(answers))

  private def lettingsRentDetailsConditionsRouting: Session => Call = answers =>
    controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyRentIncludesController
      .show(getLettingsIndex(answers))

  private def lettingsRentIncludesConditionsRouting: Session => Call = answers =>
    controllers.aboutfranchisesorlettings.routes.AddAnotherLettingOtherPartOfPropertyController
      .show(getLettingsIndex(answers))

  private def addAnotherLettingsConditionsRouting: Session => Call = answers => {
    def getLastLettingIndex(session: Session): Option[Int] =
      session.aboutFranchisesOrLettings.flatMap { aboutFranchiseOrLettings =>
        aboutFranchiseOrLettings.lettingSections.lastOption.map(_ => aboutFranchiseOrLettings.lettingSections.size)
      }
    val existingSection                                    = answers.aboutFranchisesOrLettings.flatMap(_.lettingSections.lift(getLettingsIndex(answers)))
    existingSection match {
      case Some(letting) if isLettingDetailIncomplete(letting, answers.forType) =>
        getIncompleteLettingCall(letting, answers.forType, getLettingsIndex(answers))
      case None                                                                 =>
        controllers.aboutfranchisesorlettings.routes.AddAnotherLettingOtherPartOfPropertyController
          .show(getLettingsIndex(answers))
      case _                                                                    =>
        existingSection.flatMap(_.addAnotherLettingToProperty).get.name match {
          case "yes" =>
            controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyDetailsController
              .show(getLastLettingIndex(answers))
          case _     =>
            controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController.show()
        }
    }
  }

  override val routeMap: Map[Identifier, Session => Call] = Map(
    FranchiseOrLettingsTiedToPropertyId        -> franchiseOrLettingConditionsRouting,
    CateringOperationBusinessPageId            -> (answers =>
      controllers.aboutfranchisesorlettings.routes.FeeReceivedController.show(getRentalIncomeIndex(answers))
    ),
    FeeReceivedPageId                          -> (answers =>
      controllers.aboutfranchisesorlettings.routes.RentalIncomeListController.show(getRentalIncomeIndex(answers))
    ),
    FranchiseTypeDetailsId                     -> franchiseTypeDetailsRouting,
    ConcessionTypeDetailsId                    -> (answers =>
      controllers.aboutfranchisesorlettings.routes.ConcessionTypeFeesController.show(getRentalIncomeIndex(answers))
    ),
    ConcessionTypeFeesId                       -> (answers =>
      controllers.aboutfranchisesorlettings.routes.RentalIncomeListController.show(getRentalIncomeIndex(answers))
    ),
    LettingTypeDetailsId                       -> (answers =>
      controllers.aboutfranchisesorlettings.routes.RentalIncomeRentController.show(getRentalIncomeIndex(answers))
    ),
    RentalIncomeRentId                         -> (answers =>
      controllers.aboutfranchisesorlettings.routes.RentalIncomeIncludedController.show(getRentalIncomeIndex(answers))
    ),
    RentalIncomeIncludedId                     -> (answers =>
      controllers.aboutfranchisesorlettings.routes.RentalIncomeListController.show(getRentalIncomeIndex(answers))
    ),
    RentReceivedFromPageId                     -> (answers =>
      controllers.aboutfranchisesorlettings.routes.CalculatingTheRentForController.show(getRentalIncomeIndex(answers))
    ),
    CalculatingTheRentForPageId                -> (answers =>
      controllers.aboutfranchisesorlettings.routes.RentalIncomeIncludedController.show(getRentalIncomeIndex(answers))
    ),
    LettingAccommodationPageId                 -> lettingAccommodationConditionsRouting,
    LettingAccommodationDetailsPageId          -> lettingsDetailsConditionsRouting,
    LettingAccommodationRentDetailsPageId      -> lettingsRentDetailsConditionsRouting,
    LettingAccommodationRentIncludesPageId     -> lettingsRentIncludesConditionsRouting,
    AddAnotherLettingAccommodationPageId       -> addAnotherLettingsConditionsRouting,
    MaxOfLettingsReachedCateringId             -> (_ =>
      controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyController.show()
    ),
    MaxOfLettingsReachedCurrentId              -> (_ =>
      controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController.show()
    ),
    CheckYourAnswersAboutFranchiseOrLettingsId -> (_ =>
      controllers.routes.TaskListController.show().withFragment("franchiseAndLettings")
    )
  )
}
