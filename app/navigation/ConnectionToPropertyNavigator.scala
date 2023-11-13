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
import identifiers._
import play.api.mvc.Call
import models.Session
import models.submissions.connectiontoproperty.{AddressConnectionTypeNo, VacantPropertiesDetailsYes}
import play.api.Logging

import javax.inject.Inject

class ConnectionToPropertyNavigator @Inject() (audit: Audit) extends Navigator(audit) with Logging {

  override def cyaPage: Option[Call] =
    Some(controllers.connectiontoproperty.routes.CheckYourAnswersConnectionToPropertyController.show())

  def cyaPageVacant: Option[Call] =
    Some(controllers.connectiontoproperty.routes.CheckYourAnswersConnectionToVacantPropertyController.show())

  def cyaPageNotConnected: Option[Call] =
    Some(controllers.notconnected.routes.CheckYourAnswersNotConnectedController.show())

  def cyaPageDependsOnSession(session: Session): Option[Call] =
    if (isNotConnectedPropertySubmission(session)) {
      cyaPageNotConnected
    } else if (isVacantPropertySubmission(session)) {
      cyaPageVacant
    } else {
      cyaPage
    }

  def isVacantPropertySubmission(session: Session): Boolean =
    session.stillConnectedDetails
      .flatMap(_.vacantProperties)
      .exists(_.vacantProperties == VacantPropertiesDetailsYes)

  def isNotConnectedPropertySubmission(session: Session): Boolean =
    session.stillConnectedDetails
      .flatMap(_.addressConnectionType)
      .contains(AddressConnectionTypeNo)

  override val postponeCYARedirectPages: Set[String] = Set(
    controllers.connectiontoproperty.routes.EditAddressController.show(),
    controllers.connectiontoproperty.routes.TradingNamePayingRentController.show()
  ).map(_.url)

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

  private def isPropertyVacant: Session => Call = answers => {
    answers.stillConnectedDetails.flatMap(_.vacantProperties.map(_.vacantProperties.name)) match {
      case Some("yes") => controllers.connectiontoproperty.routes.VacantPropertiesStartDateController.show()
      case Some("no")  => controllers.connectiontoproperty.routes.TradingNameOperatingFromPropertyController.show()
      case _           =>
        logger.warn(
          s"Navigation for is property vacant reached without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for is property vacant conditions routing")
    }
  }

  private def isAnyRentReceived: Session => Call                          = answers => {
    answers.stillConnectedDetails.flatMap(_.isAnyRentReceived.map(_.name)) match {
      case Some("yes") =>
        answers.stillConnectedDetails.get.lettingPartOfPropertyDetails.isEmpty match {
          case true  => controllers.connectiontoproperty.routes.LettingPartOfPropertyDetailsController.show()
          case false =>
            controllers.connectiontoproperty.routes.AddAnotherLettingPartOfPropertyController
              .show(answers.stillConnectedDetails.get.lettingPartOfPropertyDetailsIndex)
        }
      case Some("no")  => controllers.connectiontoproperty.routes.ProvideContactDetailsController.show()
      case _           =>
        logger.warn(
          s"Navigation for is any rent received without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for is any rent received conditions routing")
    }
  }
  private def tradingNameOwnTheProperty: Session => Call                  = answers => {
    answers.stillConnectedDetails.flatMap(_.tradingNameOwnTheProperty.map(_.name)) match {
      case Some("yes") => controllers.connectiontoproperty.routes.AreYouThirdPartyController.show()
      case Some("no")  => controllers.connectiontoproperty.routes.TradingNamePayingRentController.show()
      case _           =>
        logger.warn(
          s"Navigation for trading name own the property without correct selection of conditions by controller"
        )
        throw new RuntimeException("Invalid option exception for trading name own the property conditions routing")
    }
  }
  private def getLettingPartOfPropertyDetailsIndex(session: Session): Int =
    session.stillConnectedDetails.map(_.lettingPartOfPropertyDetailsIndex).getOrElse(0)

  private def lettingPartOfPropertyRentDetailsConditionsRouting: Session => Call = answers => {
    controllers.connectiontoproperty.routes.LettingPartOfPropertyDetailsRentController
      .show(getLettingPartOfPropertyDetailsIndex(answers))
  }

  private def lettingsPartOfPropertyRentDetailsConditionsRouting: Session => Call = answers => {
    controllers.connectiontoproperty.routes.LettingPartOfPropertyItemsIncludedInRentController
      .show(getLettingPartOfPropertyDetailsIndex(answers))
  }

  private def lettingPartOfPropertyItemsIncludedInRentConditionsRouting: Session => Call = answers => {
    controllers.connectiontoproperty.routes.AddAnotherLettingPartOfPropertyController
      .show(getLettingPartOfPropertyDetailsIndex(answers))
  }

  private def addAnotherLettingsConditionsRouting: Session => Call = answers => {
    val existingSection = answers.stillConnectedDetails.flatMap(
      _.lettingPartOfPropertyDetails.lift(getLettingPartOfPropertyDetailsIndex(answers))
    )
    existingSection.flatMap(_.addAnotherLettingToProperty).get.name match {
      case "yes" => controllers.connectiontoproperty.routes.LettingPartOfPropertyDetailsController.show()
      case "no"  =>
        controllers.connectiontoproperty.routes.ProvideContactDetailsController.show()
      case _     =>
        logger.warn(
          s"Navigation for add another letting part of property reached without correct selection of conditions by controller"
        )
        throw new RuntimeException(
          "Invalid option exception for add another letting part of property conditions routing"
        )
    }
  }
  override val routeMap: Map[Identifier, Session => Call]          = Map(
    AreYouStillConnectedPageId                     -> areYouStillConnectedRouting,
    EditAddressPageId                              -> (_ => controllers.connectiontoproperty.routes.VacantPropertiesController.show()),
    ConnectionToPropertyPageId                     -> (_ => controllers.routes.TaskListController.show()),
    VacantPropertiesPageId                         -> isPropertyVacant,
    PropertyBecomeVacantPageId                     -> (_ =>
      controllers.connectiontoproperty.routes.IsRentReceivedFromLettingController.show()
    ),
    LettingIncomePageId                            -> isAnyRentReceived,
    TradingNameOperatingFromPropertyPageId         -> (_ =>
      controllers.connectiontoproperty.routes.TradingNameOwnThePropertyController.show()
    ),
    TradingNameOwnThePropertyPageId                -> tradingNameOwnTheProperty,
    TradingNamePayingRentPageId                    -> (_ => controllers.connectiontoproperty.routes.AreYouThirdPartyController.show()),
    AreYouThirdPartyPageId                         -> (_ =>
      controllers.connectiontoproperty.routes.CheckYourAnswersConnectionToPropertyController.show()
    ),
    ProvideYourContactDetailsPageId                -> (_ =>
      controllers.connectiontoproperty.routes.CheckYourAnswersConnectionToVacantPropertyController.show()
    ),
    LettingPartOfPropertyDetailsPageId             -> lettingPartOfPropertyRentDetailsConditionsRouting,
    LettingPartOfPropertyRentDetailsPageId         -> lettingsPartOfPropertyRentDetailsConditionsRouting,
    LettingPartOfPropertyItemsIncludedInRentPageId -> lettingPartOfPropertyItemsIncludedInRentConditionsRouting,
    AddAnotherLettingPartOfPropertyPageId          -> addAnotherLettingsConditionsRouting,
    CheckYourAnswersConnectionToPropertyId         -> (_ => controllers.routes.TaskListController.show())
  )
}
