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
import controllers.connectiontoproperty.routes
import identifiers._
import play.api.mvc.Call
import models.ForType.*
import models.Session
import models.submissions.common.AnswersYesNo.*
import models.submissions.connectiontoproperty.AddressConnectionType.*
import models.submissions.connectiontoproperty.LettingPartOfPropertyDetails
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
      .flatMap(_.isPropertyVacant)
      .contains(AnswerYes)

  def isNotConnectedPropertySubmission(session: Session): Boolean =
    session.stillConnectedDetails
      .flatMap(_.addressConnectionType)
      .contains(AddressConnectionTypeNo)

  override val postponeCYARedirectPages: Set[String] = Set(
    controllers.connectiontoproperty.routes.EditAddressController.show(),
    controllers.connectiontoproperty.routes.TradingNamePayingRentController.show()
  ).map(_.url)

  private def areYouStillConnectedRouting: Session => Call = answers =>
    answers.stillConnectedDetails.flatMap(_.addressConnectionType) match {
      case Some(AddressConnectionTypeYes)              =>
        answers.forType match {
          case FOR6076 =>
            controllers.connectiontoproperty.routes.TradingNameOperatingFromPropertyController.show()
          case _       =>
            controllers.connectiontoproperty.routes.VacantPropertiesController.show()
        }
      case Some(AddressConnectionTypeYesChangeAddress) =>
        controllers.connectiontoproperty.routes.EditAddressController.show()
      case _                                           => controllers.notconnected.routes.PastConnectionController.show()
    }

  private def editAddressRouting: Session => Call = answers =>
    answers.forType match {
      case FOR6076 =>
        controllers.connectiontoproperty.routes.TradingNameOperatingFromPropertyController.show()
      case _       =>
        controllers.connectiontoproperty.routes.VacantPropertiesController.show()
    }

  private def isPropertyVacant: Session => Call = answers =>
    answers.stillConnectedDetails.flatMap(_.isPropertyVacant) match {
      case Some(AnswerYes) => controllers.connectiontoproperty.routes.VacantPropertiesStartDateController.show()
      case _               => controllers.connectiontoproperty.routes.TradingNameOperatingFromPropertyController.show()
    }

  private def isAnyRentReceived: Session => Call                          = answers =>
    answers.stillConnectedDetails.flatMap(_.isAnyRentReceived) match {
      case Some(AnswerYes) =>
        answers.stillConnectedDetails.get.lettingPartOfPropertyDetails.isEmpty match {
          case true  => controllers.connectiontoproperty.routes.LettingPartOfPropertyDetailsController.show()
          case false =>
            val maybeLastDetail = answers.stillConnectedDetails.flatMap(_.lettingPartOfPropertyDetails.lastOption)
            val idx             = getLettingPartOfPropertyDetailsIndex(answers)
            maybeLastDetail match {
              case Some(lastDetail) if isIncomplete(lastDetail) =>
                getIncompleteSectionCall(lastDetail, idx)
              case _                                            =>
                controllers.connectiontoproperty.routes.AddAnotherLettingPartOfPropertyController.show(idx)
            }
        }
      case _               => controllers.connectiontoproperty.routes.ProvideContactDetailsController.show()
    }
  private def tradingNameOwnTheProperty: Session => Call                  = answers =>
    answers.stillConnectedDetails.flatMap(_.tradingNameOwnTheProperty) match {
      case Some(AnswerYes) => controllers.connectiontoproperty.routes.AreYouThirdPartyController.show()
      case _               => controllers.connectiontoproperty.routes.TradingNamePayingRentController.show()
    }
  private def getLettingPartOfPropertyDetailsIndex(session: Session): Int =
    session.stillConnectedDetails.map(_.lettingPartOfPropertyDetailsIndex).getOrElse(0)

  private def lettingPartOfPropertyRentDetailsConditionsRouting: Session => Call = answers =>
    controllers.connectiontoproperty.routes.LettingPartOfPropertyDetailsRentController
      .show(getLettingPartOfPropertyDetailsIndex(answers))

  private def lettingsPartOfPropertyRentDetailsConditionsRouting: Session => Call = answers =>
    controllers.connectiontoproperty.routes.LettingPartOfPropertyItemsIncludedInRentController
      .show(getLettingPartOfPropertyDetailsIndex(answers))

  private def lettingPartOfPropertyItemsIncludedInRentConditionsRouting: Session => Call = answers =>
    controllers.connectiontoproperty.routes.AddAnotherLettingPartOfPropertyController
      .show(getLettingPartOfPropertyDetailsIndex(answers))

  private def addAnotherLettingsConditionsRouting: Session => Call = answers => {
    val existingSectionOpt = answers.stillConnectedDetails.flatMap(
      _.lettingPartOfPropertyDetails.lift(getLettingPartOfPropertyDetailsIndex(answers))
    )
    existingSectionOpt match {
      case Some(existingSection) if isIncomplete(existingSection) =>
        getIncompleteSectionCall(existingSection, getLettingPartOfPropertyDetailsIndex(answers))
      case Some(existingSection)                                  =>
        existingSection.addAnotherLettingToProperty match {
          case Some(AnswerYes) =>
            controllers.connectiontoproperty.routes.LettingPartOfPropertyDetailsController
              .show(Some(getLettingPartOfPropertyDetailsIndex(answers) + 1))
          case _               =>
            controllers.connectiontoproperty.routes.ProvideContactDetailsController.show()
        }
      case _                                                      => controllers.connectiontoproperty.routes.ProvideContactDetailsController.show()
    }
  }

  private def isIncomplete(detail: LettingPartOfPropertyDetails): Boolean =
    detail.tenantDetails == null ||
      detail.lettingPartOfPropertyRentDetails.isEmpty ||
      detail.itemsIncludedInRent.isEmpty

  def getIncompleteSectionCall(detail: LettingPartOfPropertyDetails, idx: Int): Call =
    if (detail.tenantDetails == null) routes.LettingPartOfPropertyDetailsController.show(Some(idx))
    else if (detail.lettingPartOfPropertyRentDetails.isEmpty)
      routes.LettingPartOfPropertyDetailsRentController.show(idx)
    else if (detail.itemsIncludedInRent.isEmpty) routes.LettingPartOfPropertyItemsIncludedInRentController.show(idx)
    else routes.LettingPartOfPropertyDetailsController.show(Some(idx))

  override val routeMap: Map[Identifier, Session => Call] = Map(
    AreYouStillConnectedPageId                     -> areYouStillConnectedRouting,
    EditAddressPageId                              -> editAddressRouting,
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
    MaxOfLettingsReachedId                         -> (_ => controllers.connectiontoproperty.routes.ProvideContactDetailsController.show()),
    CheckYourAnswersConnectionToPropertyId         -> (_ => controllers.routes.TaskListController.show())
  )
}
