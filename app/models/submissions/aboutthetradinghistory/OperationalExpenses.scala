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

import play.api.libs.json.{Json, OFormat}

/**
  * 6076 Trading history - Operational and administrative expenses.
  *
  * @author Yuriy Tumakha
  */
case class OperationalExpenses(
  advertising: Option[BigDecimal],
  administration: Option[BigDecimal],
  insurance: Option[BigDecimal],
  legalFees: Option[BigDecimal],
  interest: Option[BigDecimal],
  other: Option[BigDecimal]
) {
  def total: BigDecimal = Seq(
    advertising,
    administration,
    insurance,
    legalFees,
    interest,
    other
  ).flatten.sum
}

object OperationalExpenses {
  implicit val format: OFormat[OperationalExpenses] = Json.format
}
