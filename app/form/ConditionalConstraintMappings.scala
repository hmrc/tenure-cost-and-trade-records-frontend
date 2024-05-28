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

package form

import play.api.data.Forms.{default, text}
import play.api.data.Mapping
import play.api.data.validation.Constraint
import play.api.data.validation.Constraints.nonEmpty
import uk.gov.voa.play.form.Condition
import util.NumberUtil.zeroBigDecimal

import scala.util.Try

/**
  * @author Yuriy Tumakha
  */
object ConditionalConstraintMappings {

  def applyConstraintsOnCondition[T](
    condition: Condition,
    mapping: Mapping[T],
    conditionalConstraints: Constraint[T]*
  ): Mapping[T] =
    ConditionalConstraintMapping(condition, mapping.verifying(conditionalConstraints: _*), mapping)

  def mandatoryStringOnCondition(condition: Condition, errorRequired: String): Mapping[String] =
    applyConstraintsOnCondition[String](
      condition,
      default(text, ""),
      nonEmpty(errorMessage = errorRequired)
    )

  def mandatoryStringIfExists(fieldName: String, errorRequired: String): Mapping[String] =
    mandatoryStringOnCondition(_.contains(fieldName), errorRequired)

  def mandatoryStringIfNonZeroSum(fieldSuffix: String, errorRequired: String): Mapping[String] =
    mandatoryStringOnCondition(
      _.filter(_._1.endsWith(fieldSuffix)).values.map(s => Try(BigDecimal(s)).getOrElse(zeroBigDecimal)).sum > 0,
      errorRequired
    )

}
