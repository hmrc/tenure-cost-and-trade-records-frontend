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

package models.submissions.aboutYourLeaseOrTenure

import models.Scala3EnumJsonFormat
import play.api.libs.json.Format

/**
  * @author Yuriy Tumakha
  */
enum CurrentRentFixed(howIsRentFixed: String):
  override def toString: String = howIsRentFixed

  case CurrentRentFixedNewLeaseAgreement extends CurrentRentFixed("newLeaseAgreement")
  case CurrentRentFixedInterimRent extends CurrentRentFixed("interimRent")
  case CurrentRentFixedRentReview extends CurrentRentFixed("rentReview")
  case CurrentRentFixedRenewalLeaseTenancy extends CurrentRentFixed("renewalLeaseTenancy")
  case CurrentRentFixedSaleLeaseback extends CurrentRentFixed("saleLeaseback")
end CurrentRentFixed

object CurrentRentFixed:
  implicit val format: Format[CurrentRentFixed] = Scala3EnumJsonFormat.format
