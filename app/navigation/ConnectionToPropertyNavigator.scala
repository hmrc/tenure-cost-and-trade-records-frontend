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
import identifiers.{AreYouStillConnectedPageId, ConnectionToPropertyPageId, EditAddressPageId, Identifier, NoReferenceNumberContactDetailsPageId, NoReferenceNumberPageId, VacantPropertiesPageId}
import play.api.mvc.Call
import models.Session
import play.api.Logging

import javax.inject.Inject

class ConnectionToPropertyNavigator @Inject() (audit: Audit) extends Navigator(audit) with Logging {

  private def areYouStillConnectedRouting: Session => Call = answers => {
    answers.stillConnectedDetails.flatMap(_.addressConnectionType.map(_.name)) match {
      case Some("yes")                => controllers.connectiontoproperty.routes.VacantPropertiesController.show()
      case Some("yes-change-address") => controllers.connectiontoproperty.routes.EditAddressController.show()
      case Some("no")                 => controllers.notconnected.routes.PastConnectionController.show()
      case _                          =>
        logger.warn(
          s"Navigation for are you still connected reached without correct selection of are you connected by controller"
        )
        throw new RuntimeException("Invalid option exception for are you connected routing")
    }
  }

  private def whenDidThePropertyBecomeVacant: Session => Call = answers => {
    answers.stillConnectedDetails.flatMap(_.vacantProperties.map(_.vacantProperties.name)) match {
      case Some("yes") => controllers.connectiontoproperty.routes.VacantPropertiesStartDateController.show()
      case Some("no")  => controllers.connectiontoproperty.routes.TradingNameOperatingFromPropertyController.show()
      case _ =>
        logger.warn(
          s"Navigation for catering operations reached without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for catering operations conditions routing")
    }
  }

  private def tradingNameOwnTheProperty: Session => Call = answers => {
    answers.stillConnectedDetails.flatMap(_.tradingNameOwnTheProperty.map(_.name)) match {
      case Some("yes") => controllers.connectiontoproperty.routes.AreYouThirdPartyController.show()
      case Some("no") => controllers.connectiontoproperty.routes.TradingNamePayingRentController.show()
      case _ =>
        logger.warn(
          s"Navigation for catering operations reached without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for catering operations conditions routing")
    }
  }
  override val routeMap: Map[Identifier, Session => Call] = Map(
    AreYouStillConnectedPageId                   -> areYouStillConnectedRouting,
    EditAddressPageId                            -> (_ => controllers.connectiontoproperty.routes.ConnectionToThePropertyController.show()),
    ConnectionToPropertyPageId                   -> (_ => controllers.routes.TaskListController.show()),
    VacantPropertiesPageId                       -> (_ => controllers.routes.TaskListController.show()),
    NoReferenceNumberPageId                      -> (_ =>
      controllers.requestReferenceNumber.routes.RequestReferenceNumberContactDetailsController.show()
    ),
    NoReferenceNumberContactDetailsPageId        -> (_ =>
      controllers.requestReferenceNumber.routes.CheckYourAnswersRequestReferenceNumberController.submit()
    ),
    CheckYourAnswersRequestReferenceNumberPageId -> (_ =>
      controllers.routes.RequestReferenceNumberFormSubmissionController.submit()
    )
  )
}
