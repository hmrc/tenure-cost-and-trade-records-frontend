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

package navigation.identifiers

case object FranchiseOrLettingsTiedToPropertyId extends Identifier {
  override def toString: String = "franchiseOrLettingsTiedToPropertyPage"
}

case object CateringOperationBusinessPageId extends Identifier {
  override def toString: String = "cateringOperationBusinessPage"
}

case object FeeReceivedPageId extends Identifier {
  override def toString: String = "feeReceivedPage"
}

case object FranchiseTypeDetailsId extends Identifier {
  override def toString: String = "franchiseTypeDetailsPage"
}

case object ConcessionTypeDetailsId extends Identifier {
  override def toString: String = "concessionTypeDetailsPage"
}

case object ConcessionTypeFeesId extends Identifier {
  override def toString: String = "concessionTypeFeesPage"
}

case object LettingTypeDetailsId extends Identifier {
  override def toString: String = "lettingTypeDetailsPage"
}

case object RentalIncomeRentId extends Identifier {
  override def toString: String = "rentalIncomePage"
}

case object RentalIncomeIncludedId extends Identifier {
  override def toString: String = "rentalIncomeIncludedPage"
}

case object RentReceivedFromPageId extends Identifier {
  override def toString: String = "rentReceivedFromPage"
}

case object CalculatingTheRentForPageId extends Identifier {
  override def toString: String = "calculatingTheRentForPage"
}

case object AddAnotherConcessionPageId extends Identifier {
  override def toString: String = "addAnotherConcessionRoutingPage"
}
case object MaxOfLettingsReachedCateringId extends Identifier {
  override def toString: String = "MaxOfLettingsReachedCateringPage"
}

case object MaxOfLettingsReachedCurrentId extends Identifier {
  override def toString: String = "MaxOfLettingsReachedCurrentPage"
}

case object CheckYourAnswersAboutFranchiseOrLettingsId extends Identifier {
  override def toString: String = "checkYourAnswersAboutFranchiseOrLettingsPage"
}
