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

package form

import play.api.data.{FormError, Forms, Mapping}
import play.api.data.format.Formatter

object PostcodeMapping {

  def postcode(
    requiredError: String = Errors.postcodeRequired,
    maxLengthError: String = Errors.postcodeMaxLength,
    formatError: String = Errors.postcodeInvalid
  ): Mapping[String] =
    Forms.of[String](postcodeFormatter(requiredError, maxLengthError, formatError))

  def isValid(postcode: String): Boolean = {
    val cleanedPostcode = postcode.replaceAll("\\s", "").toUpperCase
    val postcodeRegex   =
      """^(GIR0AA|[A-Za-z][0-9]{1,2}|[A-Za-z][A-HJ-Y][0-9]{1,2}|[A-Za-z][0-9][A-Za-z]|[A-Za-z][A-HJ-Y][0-9][A-Za-z])[0-9][A-Za-z]{2}$"""
    cleanedPostcode.matches(postcodeRegex)
  }

  def postcodeFormatter(
                         requiredError: String = "",
                         maxLengthError: String = "",
                         formatError: String = ""
                       ): Formatter[String] = new Formatter[String] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] = {
      data.get(key).filter(_.trim.nonEmpty) match {
        case None => Left(Seq(FormError(key, requiredError)))
        case Some(rawPostcode) =>
          val cleanedPostcode = rawPostcode.replaceAll("[^A-Za-z0-9]", "").toUpperCase
          if (cleanedPostcode.length > 8) {
            Left(Seq(FormError(key, maxLengthError)))
          } else if (!isValid(cleanedPostcode)) {
            Left(Seq(FormError(key, formatError)))
          } else {
            Right(cleanedPostcode.substring(0, cleanedPostcode.length - 3) + " " + cleanedPostcode.takeRight(3))
          }
      }
    }

    override def unbind(key: String, value: String): Map[String, String] = Map(key -> value)
  }

  val customPostcodeMapping = PostcodeMapping.postcode(
    formatError = Errors.invalidPostcodeOnLetter
  )
}
