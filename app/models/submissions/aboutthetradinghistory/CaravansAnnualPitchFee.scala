/*
 * Copyright 2024 HM Revenue & Customs
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

package models.submissions.aboutthetradinghistory

import models.submissions.aboutthetradinghistory.Caravans.CaravansPitchFeeServices
import play.api.libs.json.{Json, OFormat}
import util.NumberUtil.zeroBigDecimal

/**
  * Caravans annual pitch fee per provided service.
  *
  * @author Yuriy Tumakha
  */
case class CaravansAnnualPitchFee(
  totalPitchFee: BigDecimal = zeroBigDecimal,
  servicesIncludedInPitchFee: Seq[CaravansPitchFeeServices] = Seq.empty,
  rates: Option[BigDecimal] = None,
  waterAndDrainage: Option[BigDecimal] = None,
  gas: Option[BigDecimal] = None,
  electricity: Option[BigDecimal] = None,
  otherPitchFeeDetails: Option[String] = None
)

object CaravansAnnualPitchFee {
  implicit val format: OFormat[CaravansAnnualPitchFee] = Json.format
}
