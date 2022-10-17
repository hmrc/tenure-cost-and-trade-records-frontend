/*
 * Copyright 2022 HM Revenue & Customs
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

package models.submissions

import models.{NamedEnum, NamedEnumSupport}

sealed trait FormerLeaseSurrendered extends NamedEnum{
  val key = "formerLeaseSurrendered"
}
object FormerLeaseSurrenderedYes extends FormerLeaseSurrendered {
  val name = "yes"
}
object FormerLeaseSurrenderedNo extends FormerLeaseSurrendered {
  val name = "no"
}

object FormerLeaseSurrender extends NamedEnumSupport[FormerLeaseSurrendered] {
  val all = List(FormerLeaseSurrenderedYes,FormerLeaseSurrenderedNo)
}


sealed trait RentReducedOnReviews extends NamedEnum{
  val key = "rentReducedOnReviews"
}
object RentReducedOnReviewsYes extends RentReducedOnReviews {
  val name = "yes"
}
object RentReducedOnReviewsNo extends RentReducedOnReviews {
  val name = "no"
}

object RentReducedOnReview extends NamedEnumSupport[RentReducedOnReviews] {
  val all = List(RentReducedOnReviewsYes,RentReducedOnReviewsNo)
}


sealed trait CapitalSumOrPremiums extends NamedEnum{
  val key = "capitalSumOrPremium"
}
object CapitalSumOrPremiumsYes extends CapitalSumOrPremiums {
  val name = "yes"
}
object CapitalSumOrPremiumsNo extends CapitalSumOrPremiums {
  val name = "no"
}

object CapitalSumOrPremium extends NamedEnumSupport[CapitalSumOrPremiums] {
  val all = List(CapitalSumOrPremiumsYes,CapitalSumOrPremiumsNo)
}


sealed trait ReceivePaymentWhenLeaseGrants extends NamedEnum{
  val key = "receivePaymentWhenLeaseGranted"
}
object ReceivePaymentWhenLeaseGrantsYes extends ReceivePaymentWhenLeaseGrants {
  val name = "yes"
}
object ReceivePaymentWhenLeaseGrantsNo extends ReceivePaymentWhenLeaseGrants {
  val name = "no"
}

object ReceivePaymentWhenLeaseGranted extends NamedEnumSupport[ReceivePaymentWhenLeaseGrants] {
  val all = List(ReceivePaymentWhenLeaseGrantsYes,ReceivePaymentWhenLeaseGrantsNo)
}