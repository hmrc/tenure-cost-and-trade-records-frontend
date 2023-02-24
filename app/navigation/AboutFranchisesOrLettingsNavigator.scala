/*
 * Copyright 2023 HM Revenue & Customs
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
import models.{ForTypes, Session}
import navigation.identifiers._
import play.api.Logging
import play.api.mvc.Call

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class AboutFranchisesOrLettingsNavigator @Inject() (audit: Audit)(implicit ec: ExecutionContext)
    extends Navigator(audit)
    with Logging {

  private def franchiseOrLettingConditionsRouting: Session => Call = answers => {
    if (
      answers.userLoginDetails.forNumber
        .equals(ForTypes.for6015) || answers.userLoginDetails.forNumber.equals(ForTypes.for6016)
    ) {
      answers.aboutFranchisesOrLettings.flatMap(_.franchisesOrLettingsTiedToProperty.map(_.name)) match {
        case Some("yes") =>
          controllers.aboutfranchisesorlettings.routes.ConcessionOrFranchiseController.show()
        case Some("no")  =>
          controllers.routes.TaskListController.show()
        case _           =>
          logger.warn(
            s"Navigation for franchise or letting reached without correct selection of conditions by controller"
          )
          throw new RuntimeException("Invalid option exception for franchise or letting conditions routing")
      }
    } else {
      answers.aboutFranchisesOrLettings.flatMap(_.franchisesOrLettingsTiedToProperty.map(_.name)) match {
        case Some("yes") => controllers.aboutfranchisesorlettings.routes.CateringOperationController.show()
        case Some("no")  => controllers.routes.TaskListController.show() // TODO Insert CYA page.
        case _           =>
          logger.warn(
            s"Navigation for franchise or letting reached without correct selection of conditions by controller"
          )
          throw new RuntimeException("Invalid option exception for franchise or letting conditions routing")
      }
    }
  }

  private def cateringOperationsConditionsRouting: Session => Call = answers => {
    answers.aboutFranchisesOrLettings.flatMap(_.cateringOperation.map(_.name)) match {
      case Some("yes") => controllers.aboutfranchisesorlettings.routes.CateringOperationDetailsController.show()
      case Some("no")  => controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyController.show()
      case _           =>
        logger.warn(
          s"Navigation for catering operations reached without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for catering operations conditions routing")
    }
  }

  private def getCateringOperationsIndex(session: Session): Int =
    session.aboutFranchisesOrLettings.map(_.cateringOperationCurrentIndex).getOrElse(0)

  private def cateringOperationsDetailsConditionsRouting: Session => Call = answers => {
    controllers.aboutfranchisesorlettings.routes.CateringOperationDetailsRentController
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
    val existingSection =
      answers.aboutFranchisesOrLettings.flatMap(_.cateringOperationSections.lift(getCateringOperationsIndex(answers)))
    existingSection.flatMap(_.addAnotherOperationToProperty).get.name match {
      case "yes" => controllers.aboutfranchisesorlettings.routes.CateringOperationDetailsController.show()
      case "no"  => controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyController.show()
      case _     =>
        logger.warn(
          s"Navigation for add another catering operation reached without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for add another catering operation conditions routing")
    }
  }

  private def lettingAccommodationConditionsRouting: Session => Call = answers => {
    answers.aboutFranchisesOrLettings.flatMap(_.lettingOtherPartOfProperty.map(_.name)) match {
      case Some("yes") =>
        controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyDetailsController.show()
      case Some("no")  => controllers.routes.TaskListController.show()
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

  private def cateringOrFranchiseRouting: Session => Call = answers => {
    answers.aboutFranchisesOrLettings.flatMap(_.concessionOrFranchise.map(_.name)) match {
      case Some("yes") =>
        controllers.aboutfranchisesorlettings.routes.CateringOperationDetailsController.show()
      case Some("no")  => controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyController.show()
      case _           =>
        logger.warn(
          s"Navigation for catering or franchise reached without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for catering or franchise routing")

    }
  }

  override val routeMap: Map[Identifier, Session => Call] = Map(
    FranchiseOrLettingsTiedToPropertyId    -> franchiseOrLettingConditionsRouting,
    CateringOperationPageId                -> cateringOperationsConditionsRouting,
    CateringOperationDetailsPageId         -> cateringOperationsDetailsConditionsRouting,
    CateringOperationRentDetailsPageId     -> cateringOperationsRentDetailsConditionsRouting,
    CateringOperationRentIncludesPageId    -> cateringOperationsRentIncludesConditionsRouting,
    AddAnotherCateringOperationPageId      -> addAnotherCateringOperationsConditionsRouting,
    LettingAccommodationPageId             -> lettingAccommodationConditionsRouting,
    LettingAccommodationDetailsPageId      -> lettingsDetailsConditionsRouting,
    LettingAccommodationRentDetailsPageId  -> lettingsRentDetailsConditionsRouting,
    LettingAccommodationRentIncludesPageId -> lettingsRentIncludesConditionsRouting,
//    AddAnotherLettingAccommodationPageId   -> addAnotherLettingsConditionsRouting,
    ConcessionOrFranchiseId                -> cateringOrFranchiseRouting
  )
}
