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

case object FranchiseOrLettingsTiedToPropertyId extends Identifier {
  override def toString: String = "franchiseOrLettingsTiedToPropertyPage"
}

case object CateringOperationPageId extends Identifier { // TODO remane CateringOrConcessionsDetailsPageId
  override def toString: String = "cateringOperationPage"
}
case object ConcessionOrFranchiseId extends Identifier { override def toString: String = "concessionOrFranchisePage" }

case object CateringOperationDetailsPageId extends Identifier {
  override def toString: String = "cateringOperationDetailsPage"
}

case object CateringOperationRentDetailsPageId extends Identifier {
  override def toString: String = "cateringOperationRentDetailsPage"
}

case object CateringOperationRentIncludesPageId extends Identifier {
  override def toString: String = "cateringOperationRentIncludesPage"
}

case object AddAnotherCateringOperationPageId extends Identifier {
  override def toString: String = "addAnotherCateringOperationPage"
}

case object LettingAccommodationPageId extends Identifier {
  override def toString: String = "lettingAccommodationPage"
}

case object LettingAccommodationDetailsPageId extends Identifier {
  override def toString: String = "lettingAccommodationDetailsPage"
}

case object LettingAccommodationRentDetailsPageId extends Identifier {
  override def toString: String = "lettingAccommodationRentDetailsPage"
}

case object LettingAccommodationRentIncludesPageId extends Identifier {
  override def toString: String = "lettingAccommodationRentIncludesPage"
}

case object AddAnotherLettingAccommodationPageId extends Identifier {
  override def toString: String = "addAnotherLettingAccommodationPage"
}
