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

package uk.gov.voa.play.form

import play.api.data.validation.Constraint
import play.api.data.{FormError, Mapping}

// TODO: Remove package uk.gov.voa.play.form if library uk.gov.hmrc:play-conditional-form-mapping_2.13 for Scala 2.13 released
// https://artefacts.tax.service.gov.uk/ui/packages?name=%2Aplay-conditional-form-mapping%2A&type=packages

case class MandatoryOptionalMapping[T](wrapped: Mapping[T], constraints: Seq[Constraint[Option[T]]] = Nil)
  extends Mapping[Option[T]] {

  override val format: Option[(String, Seq[Any])] = wrapped.format

  val key = wrapped.key

  def verifying(addConstraints: Constraint[Option[T]]*): Mapping[Option[T]] = {
    this.copy(constraints = this.constraints ++ addConstraints.toSeq)
  }

  def bind(data: Map[String, String]): Either[Seq[FormError], Option[T]] =
    wrapped.bind(data).right.map(Some(_)).right.flatMap(applyConstraints)

  def unbind(value: Option[T]): Map[String, String] = {
    value.map(wrapped.unbind).getOrElse(Map.empty)
  }

  def unbindAndValidate(value: Option[T]): (Map[String, String], Seq[FormError]) = {
    val errors = collectErrors(value)
    value.map(wrapped.unbindAndValidate).map(r => r._1 -> (r._2 ++ errors)).getOrElse(Map.empty -> errors)
  }

  def withPrefix(prefix: String): Mapping[Option[T]] = {
    copy(wrapped = wrapped.withPrefix(prefix))
  }

  val mappings: Seq[Mapping[_]] = wrapped.mappings
}
