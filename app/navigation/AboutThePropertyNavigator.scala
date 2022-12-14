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

class AboutThePropertyNavigator @Inject() (audit: Audit)(implicit ec: ExecutionContext)
    extends Navigator(audit)
    with Logging {

  private def licensableActivityRouting: Session => Call = answers => {
    answers.aboutTheProperty.flatMap(_.licensableActivities.map(_.name)) match {
      case Some("yes") => controllers.abouttheproperty.routes.LicensableActivitiesDetailsController.show()
      case Some("no")  => controllers.abouttheproperty.routes.PremisesLicenseConditionsController.show()
      case _           =>
        logger.warn(
          s"Navigation for about the property reached without correct selection of licence activity by controller"
        )
        throw new RuntimeException("Invalid option exception for licence activity routing")
    }
  }

  private def premisesLicenceConditionsRouting: Session => Call = answers => {
    answers.aboutTheProperty.flatMap(_.premisesLicenseConditions.map(_.name)) match {
      case Some("yes") => controllers.abouttheproperty.routes.PremisesLicenseConditionsDetailsController.show()
      case Some("no")  => controllers.abouttheproperty.routes.EnforcementActionBeenTakenController.show()
      case _           =>
        logger.warn(
          s"Navigation for about the property reached without correct selection of premises licence conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for premises licence conditions routing")
    }
  }

  private def enforcementActionTakenRouting: Session => Call = answers => {
    answers.aboutTheProperty.flatMap(_.enforcementAction.map(_.name)) match {
      case Some("yes") => controllers.abouttheproperty.routes.EnforcementActionBeenTakenDetailsController.show()
      case Some("no")  =>
        if (answers.userLoginDetails.forNumber == ForTypes.for6011)
          controllers.Form6010.routes.AboutYourTradingHistoryController.show()
        else
          controllers.abouttheproperty.routes.TiedForGoodsController.show()
      case _           =>
        logger.warn(
          s"Navigation for about the property reached without correct selection of enforcement action taken by controller"
        )
        throw new RuntimeException("Invalid option exception for enforcement action taken routing")
    }
  }

  private def enforcementActionTakenDetailsRouting: Session => Call = answers => {
    if (answers.userLoginDetails.forNumber == ForTypes.for6011)
      controllers.Form6010.routes.AboutYourTradingHistoryController.show()
    else
      controllers.abouttheproperty.routes.TiedForGoodsController.show()
  }

  private def tiedGoodsRouting: Session => Call = answers => {
    answers.aboutTheProperty.flatMap(_.tiedForGoods.map(_.name)) match {
      case Some("yes") => controllers.abouttheproperty.routes.TiedForGoodsDetailsController.show()
      case Some("no")  => controllers.Form6010.routes.AboutYourTradingHistoryController.show()
      case _           =>
        logger.warn(
          s"Navigation for about the property reached without correct selection of tied goods by controller"
        )
        throw new RuntimeException("Invalid option exception for tied goods routing")
    }
  }

  override val routeMap: Map[Identifier, Session => Call] = Map(
    AboutThePropertyPageId                  -> (_ => controllers.abouttheproperty.routes.WebsiteForPropertyController.show()),
    WebsiteForPropertyPageId                -> (_ => controllers.abouttheproperty.routes.LicensableActivitiesController.show()),
    LicensableActivityPageId                -> licensableActivityRouting,
    LicensableActivityDetailsPageId         -> (_ =>
      controllers.abouttheproperty.routes.PremisesLicenseConditionsController.show()
    ),
    PremisesLicenceConditionsPageId         -> premisesLicenceConditionsRouting,
    PremisesLicenceConditionsDetailsPageId  -> (_ =>
      controllers.abouttheproperty.routes.EnforcementActionBeenTakenController.show()
    ),
    EnforcementActionBeenTakenPageId        -> enforcementActionTakenRouting,
    EnforcementActionBeenTakenDetailsPageId -> enforcementActionTakenDetailsRouting,
    TiedForGoodsPageId                      -> tiedGoodsRouting,
    TiedForGoodsDetailsPageId               -> (_ => controllers.Form6010.routes.AboutYourTradingHistoryController.show())
  )
}
