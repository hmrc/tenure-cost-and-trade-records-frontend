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

package navigation.identifiers

case object SignInPageId extends Identifier { override def toString: String = "signInPage" }

case object AreYouStillConnectedPageId extends Identifier { override def toString: String = "areYouStillConnectedPage" }

case object EditAddressPageId extends Identifier { override def toString: String = "editAddressPage" }

case object ConnectionToPropertyPageId extends Identifier { override def toString: String = "ConnectionToPropertyPage" }

case object VacantPropertiesPageId extends Identifier { override def toString: String = "VacantPropertiesPage" }

case object NoReferenceNumberPageId extends Identifier { override def toString: String = "NoReferenceNumberPage" }

case object NoReferenceNumberContactDetailsPageId extends Identifier {
  override def toString: String = "NoReferenceNumberContactDetailsPage"
}

case object CheckYourAnswersRequestReferenceNumberPageId extends Identifier {
  override def toString: String = "CheckYourAnswersRequestReferenceNumberPage"
}

case object PropertyBecomeVacantPageId extends Identifier { override def toString: String = "PropertyBecomeVacantPage" }

case object LettingIncomePageId extends Identifier { override def toString: String = "LettingIncomePage" }

case object TradingNameOperatingFromPropertyPageId extends Identifier {
  override def toString: String = "TradingNameOperatingFromProperty"
}

case object TradingNameOwnThePropertyPageId extends Identifier {
  override def toString: String = "TradingNameOwnTheProperty"
}

case object TradingNamePayingRentPageId extends Identifier {
  override def toString: String = "TradingNamePayingRentPage"
}

case object ProvideYourContactDetailsPageId extends Identifier {
  override def toString: String = "ProvideYourContactDetailsPage"
}

case object AreYouThirdPartyPageId extends Identifier { override def toString: String = "AreYouThirdParty" }

case object LettingPartOfPropertyDetailsPageId extends Identifier {
  override def toString: String = "LettingPartOfPropertyDetailsPage"
}

case object LettingPartOfPropertyRentDetailsPageId extends Identifier {
  override def toString: String = "LettingPartOfPropertyRentDetailsPage"
}

case object LettingPartOfPropertyItemsIncludedInRentPageId extends Identifier {
  override def toString: String = "LettingPartOfPropertyItemsIncludedInRentPage"
}

case object AddAnotherLettingPartOfPropertyPageId extends Identifier {
  override def toString: String = "AddAnotherLettingPartOfPropertyPage"
}

case object CheckYourAnswersConnectionToPropertyId extends Identifier {
  override def toString: String = "CheckYourAnswersConnectionToPropertyPage"
}

case object MaxOfLettingsReachedId extends Identifier {
  override def toString: String = "MaxOfLettingsReachedPage"
}
