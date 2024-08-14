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
import play.api.data.validation.Constraints.nonEmpty
import play.api.data.validation.{Constraint, Invalid, Valid}
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
    ConditionalConstraintMapping(condition, mapping.verifying(conditionalConstraints*), mapping)

  def mandatoryStringOnCondition(condition: Condition, errorRequired: String): Mapping[String] =
    applyConstraintsOnCondition[String](
      condition,
      default(text, ""),
      nonEmpty(errorMessage = errorRequired)
    )

  /**
    * Makes text field mandatory if another field with name {@code fieldName} contains any value.
    *
    * @param fieldName another field name
    * @param errorRequired error message key
    */
  def mandatoryStringIfExists(fieldName: String, errorRequired: String): Mapping[String] =
    mandatoryStringOnCondition(_.contains(fieldName), errorRequired)

  /**
    * Makes text field mandatory if other fields selected by name suffix {@code fieldSuffix} give values sum more then zero.
    *
    * @param fieldSuffix other fields name suffix
    * @param errorRequired error message key
    */
  def mandatoryStringIfNonZeroSum(fieldSuffix: String, errorRequired: String): Mapping[String] =
    mandatoryStringOnCondition(
      _.filter(_._1.endsWith(fieldSuffix)).values.map(s => Try(BigDecimal(s)).getOrElse(zeroBigDecimal)).sum > 0,
      errorRequired
    )

  /**
    * Makes field mandatory if other field value equals param 'value'.
    *
    * @param fieldName other field name
    * @param value other field value
    * @param mapping the field mapping
    * @param errorRequired error message key
    */
  def mandatoryIfEquals[T](
    fieldName: String,
    value: String,
    mapping: Mapping[Option[T]],
    errorRequired: String
  ): Mapping[Option[T]] =
    applyConstraintsOnCondition[Option[T]](
      _.get(fieldName).contains(value),
      mapping,
      Constraint[Option[T]] {
        _.fold(Invalid(errorRequired))(_ => Valid)
      }
    )

  /**
    * Makes field mandatory if other multi value field contains 'oneOfValues'.
    *
    * @param fieldName other multi value field name
    * @param oneOfValues other field value
    * @param mapping the field mapping
    * @param errorRequired error message key
    */
  def mandatoryIfOneOfValuesIs[T](
    fieldName: String,
    oneOfValues: String,
    mapping: Mapping[Option[T]],
    errorRequired: String
  ): Mapping[Option[T]] =
    applyConstraintsOnCondition[Option[T]](
      _.filter(_._1.startsWith(fieldName)).values.toSet.contains(oneOfValues),
      mapping,
      Constraint[Option[T]] {
        _.fold(Invalid(errorRequired))(_ => Valid)
      }
    )

}
