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

sealed trait CommenceWithinThreeYears extends NamedEnum {
  override def key: String = "commenceWithinThreeYears"
}
object CommenceWithinThreeYearsYes extends CommenceWithinThreeYears {
  override def name: String = "yes"
}
object CommenceWithinThreeYearsNo extends CommenceWithinThreeYears {
  override def name: String = "no"
}

object CommenceWithinThreeYears extends NamedEnumSupport[CommenceWithinThreeYears] {
  implicit val format: Format[CommenceWithinThreeYears] = EnumFormat(CommenceWithinThreeYears)

  override def all = List(CommenceWithinThreeYearsYes, CommenceWithinThreeYearsNo)

  val key = all.head.key
}

sealed trait AgreedReviewedAlteredThreeYears extends NamedEnum {
  override def key: String = "agreedReviewedAlteredThreeYears"
}
object AgreedReviewedAlteredThreeYearsYes extends AgreedReviewedAlteredThreeYears {
  override def name: String = "yes"
}
object AgreedReviewedAlteredThreeYearsNo extends AgreedReviewedAlteredThreeYears {
  override def name: String = "no"
}

object AgreedReviewedAlteredThreeYears extends NamedEnumSupport[AgreedReviewedAlteredThreeYears] {
  implicit val format: Format[AgreedReviewedAlteredThreeYears] = EnumFormat(AgreedReviewedAlteredThreeYears)

  val all = List(AgreedReviewedAlteredThreeYearsYes, AgreedReviewedAlteredThreeYearsNo)

  val key = all.head.key
}

sealed trait RentUnderReviewNegotiated extends NamedEnum {
  override def key: String = "rentUnderReviewNegotiated"
}
object RentUnderReviewNegotiatedYes extends RentUnderReviewNegotiated {
  override def name: String = "yes"
}
object RentUnderReviewNegotiatedNo extends RentUnderReviewNegotiated {
  override def name: String = "no"
}

object RentUnderReviewNegotiated extends NamedEnumSupport[RentUnderReviewNegotiated] {
  implicit val format: Format[RentUnderReviewNegotiated] = EnumFormat(RentUnderReviewNegotiated)

  val all = List(RentUnderReviewNegotiatedYes, RentUnderReviewNegotiatedNo)

  val key = all.head.key

}
