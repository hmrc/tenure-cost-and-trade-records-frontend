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
    if (answers.userLoginDetails.forNumber == ForTypes.for6011)
      controllers.aboutYourLeaseOrTenure.routes.CurrentAnnualRentController.show()
    else
      controllers.aboutYourLeaseOrTenure.routes.LeaseOrAgreementYearsController.show()
  }

  private def currentRentFirstPaidRouting: Session => Call = answers => {
    if (answers.userLoginDetails.forNumber == ForTypes.for6011)
      controllers.aboutYourLeaseOrTenure.routes.TenancyLeaseAgreementExpireController.show()
    else
      controllers.Form6010.routes.CurrentLeaseOrAgreementBeginController.show()
  }

  override val routeMap: Map[Identifier, Session => Call] = Map(
    AboutTheLandlordPageId                 -> aboutYourLandlordRouting,
    // Revisit navigation when session is available
    LeaseOrAgreementDetailsPageId          -> (_ =>
      controllers.aboutYourLeaseOrTenure.routes.CurrentRentPayableWithin12MonthsController.show()
    ),
    CurrentRentPayableWithin12monthsPageId -> (_ =>
      controllers.additionalinformation.routes.FurtherInformationOrRemarksController.show()
    ),
    CurrentAnnualRentPageId                -> (_ => controllers.aboutYourLeaseOrTenure.routes.CurrentRentFirstPaidController.show()),
    CurrentRentFirstPaidPageId             -> currentRentFirstPaidRouting,
    TenancyLeaseAgreementExpirePageId      -> (_ =>
      controllers.additionalinformation.routes.FurtherInformationOrRemarksController.show()
    )
  )
}
