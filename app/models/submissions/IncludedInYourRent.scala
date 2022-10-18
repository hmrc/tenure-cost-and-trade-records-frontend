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

sealed trait CommenceWithinThreeYears extends NamedEnum{
  val key = "commenceWithinThreeYears"
}
object CommenceWithinThreeYearsYes extends CommenceWithinThreeYears {
  val name = "yes"
}
object CommenceWithinThreeYearsNo extends CommenceWithinThreeYears {
  val name = "no"
}

object CommenceWithinThreeYear extends NamedEnumSupport[CommenceWithinThreeYears] {
  val all = List(CommenceWithinThreeYearsYes,CommenceWithinThreeYearsNo)
}


sealed trait AgreedReviewedAlteredThreeYears extends NamedEnum{
  val key = "agreedReviewedAlteredThreeYears"
}
object AgreedReviewedAlteredThreeYearsYes extends AgreedReviewedAlteredThreeYears {
  val name = "yes"
}
object AgreedReviewedAlteredThreeYearsNo extends AgreedReviewedAlteredThreeYears {
  val name = "no"
}

object AgreedReviewedAlteredThreeYear extends NamedEnumSupport[AgreedReviewedAlteredThreeYears] {
  val all = List(AgreedReviewedAlteredThreeYearsYes,AgreedReviewedAlteredThreeYearsNo)
}


sealed trait RentUnderReviewNegotiated extends NamedEnum{
  val key = "rentUnderReviewNegotiated"
}
object RentUnderReviewNegotiatedYes extends RentUnderReviewNegotiated {
  val name = "yes"
}
object RentUnderReviewNegotiatedNo extends RentUnderReviewNegotiated {
  val name = "no"
}

object RentUnderReviewNegotiate extends NamedEnumSupport[RentUnderReviewNegotiated] {
  val all = List(RentUnderReviewNegotiatedYes,RentUnderReviewNegotiatedNo)
}
