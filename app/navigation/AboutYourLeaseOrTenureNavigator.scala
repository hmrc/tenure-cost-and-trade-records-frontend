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

class AboutYourLeaseOrTenureNavigator @Inject() (audit: Audit)(implicit ec: ExecutionContext)
    extends Navigator(audit)
    with Logging {

  private def aboutYourLandlordRouting: Session => Call = answers => {
    answers.userLoginDetails.forNumber match {
      case ForTypes.for6011                    =>
        controllers.aboutYourLeaseOrTenure.routes.CurrentAnnualRentController.show()
      case ForTypes.for6015 | ForTypes.for6016 =>
        controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordController.show()
      case _                                   =>
        controllers.aboutYourLeaseOrTenure.routes.LeaseOrAgreementYearsController.show()
    }

  }

  private def currentRentFirstPaidRouting: Session => Call = answers => {
    if (answers.userLoginDetails.forNumber == ForTypes.for6011)
      controllers.aboutYourLeaseOrTenure.routes.TenancyLeaseAgreementExpireController.show()
    else
      controllers.aboutYourLeaseOrTenure.routes.CurrentLeaseOrAgreementBeginController.show()
  }

  private def connectedToLandlordRouting: Session => Call = answers => {
    answers.aboutLeaseOrAgreementPartOne.flatMap(_.connectedToLandlord.map(_.name)) match {
      case Some("yes") => controllers.aboutYourLeaseOrTenure.routes.ConnectedToLandlordDetailsController.show()
      case Some("no")  => controllers.aboutYourLeaseOrTenure.routes.LeaseOrAgreementYearsController.show()
      case _           =>
        logger.warn(
          s"Navigation for connected to landlord reached without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for connected to landlord routing")
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
      case _                                    => controllers.aboutYourLeaseOrTenure.routes.CurrentAnnualRentController.show()
    }
  }

  override val routeMap: Map[Identifier, Session => Call] = Map(
    AboutTheLandlordPageId                 -> aboutYourLandlordRouting,
    ConnectedToLandlordPageId              -> connectedToLandlordRouting,
    ConnectedToLandlordDetailsPageId       -> (_ =>
      controllers.aboutYourLeaseOrTenure.routes.LeaseOrAgreementYearsController.show()
    ),
    LeaseOrAgreementDetailsPageId          -> leaseOrAgreementDetailsRouting,
    CurrentRentPayableWithin12monthsPageId -> (_ => controllers.routes.TaskListController.show()),
    CurrentAnnualRentPageId                -> (_ => controllers.aboutYourLeaseOrTenure.routes.CurrentRentFirstPaidController.show()),
    CurrentRentFirstPaidPageId             -> currentRentFirstPaidRouting,
    TenancyLeaseAgreementExpirePageId      -> (_ => controllers.routes.TaskListController.show())
  )
}
