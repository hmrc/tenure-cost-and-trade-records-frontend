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
          if (answers.stillConnectedDetails.flatMap(_.connectionToProperty.map(_.name)).equals(Some("ownerTrustee"))) {
            controllers.additionalinformation.routes.FurtherInformationOrRemarksController.show()
          } else {
            answers.stillConnectedDetails.flatMap(_.connectionToProperty.map(_.name)).equals(Some("occupierTrustee"))
            controllers.aboutYourLeaseOrTenure.routes.AboutYourLandlordController.show()
          }
        case _           =>
          logger.warn(
            s"Navigation for franchise or letting reached without correct selection of conditions by controller"
          )
          throw new RuntimeException("Invalid option exception for franchise or letting conditions routing")
      }
    } else {
      answers.aboutFranchisesOrLettings.flatMap(_.franchisesOrLettingsTiedToProperty.map(_.name)) match {
        case Some("yes") => controllers.aboutfranchisesorlettings.routes.CateringOperationController.show()
        case Some("no")  => controllers.aboutYourLeaseOrTenure.routes.AboutYourLandlordController.show()
        case _           =>
          logger.warn(
            s"Navigation for franchise or letting reached without correct selection of conditions by controller"
          )
          throw new RuntimeException("Invalid option exception for franchise or letting conditions routing")
      }
    }
  }

  private def cateringOperationsConditionsRouting: Session => Call = answers => {
    answers.aboutFranchisesOrLettings.flatMap(_.cateringOperationOrLettingAccommodation.map(_.name)) match {
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
        controllers.aboutfranchisesorlettings.routes.LettingOtherPartOfPropertyDetailsController.show()
      case Some("no")  => controllers.aboutYourLeaseOrTenure.routes.AboutYourLandlordController.show()
      case _           =>
        logger.warn(
          s"Navigation for lettings reached without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for lettings conditions routing")
    }
  }

  override val routeMap: Map[Identifier, Session => Call] = Map(
    FranchiseOrLettingsTiedToPropertyId -> franchiseOrLettingConditionsRouting,
    CateringOperationPageId             -> cateringOperationsConditionsRouting,
    LettingAccommodationPageId          -> lettingAccommodationConditionsRouting
  )
}
