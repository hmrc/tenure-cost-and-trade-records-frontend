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
import models.ForType.*
import models.Session
import models.submissions.common.AnswersYesNo.*
import navigation.identifiers.*
import play.api.Logging
import play.api.mvc.Call

import javax.inject.Inject

class AboutFranchisesOrLettingsNavigator @Inject() (audit: Audit) extends Navigator(audit) with Logging {

  override def cyaPage: Option[Call] =
    Some(aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController.show())

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

  private def franchiseTypeDetailsRouting: Session => Call = answers =>
    answers.forType match {
      case FOR6015 | FOR6016 =>
        controllers.aboutfranchisesorlettings.routes.RentReceivedFromController
          .show(getRentalIncomeIndex(answers))
      case _                 =>
        controllers.aboutfranchisesorlettings.routes.RentalIncomeRentController
          .show(getRentalIncomeIndex(answers))
    }
  override val routeMap: Map[Identifier, Session => Call]  = Map(
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
    MaxOfLettingsReachedCurrentId              -> (_ =>
      controllers.aboutfranchisesorlettings.routes.CheckYourAnswersAboutFranchiseOrLettingsController.show()
    ),
    CheckYourAnswersAboutFranchiseOrLettingsId -> (_ =>
      controllers.routes.TaskListController.show().withFragment("franchiseAndLettings")
    )
  )
}
