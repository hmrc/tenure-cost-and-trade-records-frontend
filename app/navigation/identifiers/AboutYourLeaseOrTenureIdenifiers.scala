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

case object AboutTheLandlordPageId extends Identifier {
  override def toString: String = "aboutTheLandlordPage"
}

case object CurrentAnnualRentPageId extends Identifier {
  override def toString: String = "currentAnnualRentPage"
}

case object CurrentRentFirstPaidPageId extends Identifier {
  override def toString: String = "currentRentFirstPaidPage"
}

// 6011 page only
case object TenancyLeaseAgreementExpirePageId extends Identifier {
  override def toString: String = "tenancyLeaseAgreementExpirePage"
}

