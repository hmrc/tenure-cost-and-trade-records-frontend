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

case object PropertyBecomeVacantPageId extends Identifier { override def toString: String = "PropertyBecomeVacant" }

case object LettingIncomePageId extends Identifier { override def toString: String = "LettingIncome" }

case object TradingNameOperatingFromPropertyPageId extends Identifier {
  override def toString: String = "TradingNameOperatingFromProperty"
}

case object TradingNameOwnThePropertyPageId extends Identifier {
  override def toString: String = "TradingNameOwnTheProperty"
}

case object TradingNamePayingRentPageId extends Identifier { override def toString: String = "TradingNamePayingRent" }

case object AreYouThirdPartyPageId extends Identifier { override def toString: String = "AreYouThirdParty" }

case object NoReferenceNumberPageId extends Identifier { override def toString: String = "NoReferenceNumberPage" }

case object NoReferenceNumberContactDetailsPageId extends Identifier {
  override def toString: String = "NoReferenceNumberContactDetailsPage"
}

case object CheckYourAnswersRequestReferenceNumberPageId extends Identifier {
  override def toString: String = "CheckYourAnswersRequestReferenceNumberPage"
}

case object DownloadPDFReferenceNumberPageId extends Identifier {
  override def toString: String = "DownloadPDFReferenceNumberPage"
}
