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

package models.submissions.aboutYourLeaseOrTenure

import models.{EnumFormat, NamedEnum, NamedEnumSupport}
import play.api.libs.json.Format

sealed trait TenancyThreeYears extends NamedEnum {
  val key = "commenceWithinThreeYears"
}
object TenancyThreeYearsYes extends TenancyThreeYears {
  val name = "yes"
}
object TenancyThreeYearsNo extends TenancyThreeYears {
  val name = "no"
}

object TenancyThreeYears extends NamedEnumSupport[TenancyThreeYears] {

  implicit val format: Format[TenancyThreeYears] = EnumFormat(
    TenancyThreeYears
  )

  val all: Seq[TenancyThreeYears] = List(TenancyThreeYearsYes, TenancyThreeYearsNo)

  val key: String = all.head.key
}

sealed trait RentThreeYears extends NamedEnum {
  val key = "agreedReviewedAlteredThreeYears"
}
object RentThreeYearsYes extends RentThreeYears {
  val name = "yes"
}
object RentThreeYearsNo extends RentThreeYears {
  val name = "no"
}

object RentThreeYears extends NamedEnumSupport[RentThreeYears] {

  implicit val format: Format[RentThreeYears] = EnumFormat(
    RentThreeYears
  )

  val all: Seq[RentThreeYears] = List(RentThreeYearsYes, RentThreeYearsNo)

  val key: String = all.head.key
}

sealed trait UnderReview extends NamedEnum {
  val key = "rentUnderReviewNegotiated"
}
object UnderReviewYes extends UnderReview {
  val name = "yes"
}
object UnderReviewNo extends UnderReview {
  val name = "no"
}

object UnderReview extends NamedEnumSupport[UnderReview] {

  implicit val format: Format[UnderReview] = EnumFormat(
    UnderReview
  )

  val all: Seq[UnderReview] = List(UnderReviewYes, UnderReviewNo)

  val key: String = all.head.key
}
