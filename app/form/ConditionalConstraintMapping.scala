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

import play.api.data.validation.Constraint
import play.api.data.{FormError, Mapping}
import uk.gov.voa.play.form.Condition

/**
  * @author Yuriy Tumakha
  */
case class ConditionalConstraintMapping[T](
  condition: Condition,
  conditionalMapping: Mapping[T],
  defaultMapping: Mapping[T],
  constraints: Seq[Constraint[T]] = Seq.empty
) extends Mapping[T] {

  override val format: Option[(String, Seq[Any])] = defaultMapping.format

  val key: String = defaultMapping.key

  def verifying(addConstraints: Constraint[T]*): Mapping[T] =
    this.copy(constraints = constraints ++ addConstraints.toSeq)

  def bind(data: Map[String, String]): Either[Seq[FormError], T] =
    (
      if (condition(data)) {
        conditionalMapping.bind(data)
      } else {
        defaultMapping.bind(data)
      }
    ).flatMap(applyConstraints)

  def unbind(value: T): Map[String, String] = defaultMapping.unbind(value)

  def unbindAndValidate(value: T): (Map[String, String], Seq[FormError]) = defaultMapping.unbindAndValidate(value)

  def withPrefix(prefix: String): Mapping[T] =
    copy(conditionalMapping = conditionalMapping.withPrefix(prefix), defaultMapping = defaultMapping.withPrefix(prefix))

  val mappings: Seq[Mapping[_]] = (conditionalMapping.mappings ++ defaultMapping.mappings) :+ this

}
