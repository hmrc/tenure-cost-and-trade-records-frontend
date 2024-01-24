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

import play.api.data.Forms.text
import play.api.data.format.Formatter
import play.api.data.{FormError, Forms, Mapping}

object BuildingNameNumberMapping {

  def validateBuildingNameNumber(
                requiredError: String = Errors.addressBuildingNameNumberRequired,
                maxLengthError: String = Errors.buildingMaxLength,
                formatError: String = Errors.invalidCharAddress1
              ): Mapping[String] =
    Forms.of[String](buildingFormatter(requiredError, maxLengthError, formatError))

//  def isValid(postcode: String): Boolean = {
//    val cleanedPostcode = postcode.replaceAll("\\s", "").toUpperCase
//    val postcodeRegex =
//      """^(GIR0AA|[A-Za-z][0-9]{1,2}|[A-Za-z][A-HJ-Y][0-9]{1,2}|[A-Za-z][0-9][A-Za-z]|[A-Za-z][A-HJ-Y][0-9][A-Za-z])[0-9][A-Za-z]{2}$"""
//    cleanedPostcode.matches(postcodeRegex)
//  }

  def buildingFormatter(
                         requiredError: String = "",
                         maxLengthError: String = "",
                         formatError: String = ""
                       ): Formatter[String] = new Formatter[String] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] =
      data.get(key).filter(_.trim.nonEmpty) match {
        case None => Left(Seq(FormError(key, requiredError)))
        case Some(rawLength) =>
          val maxLength = rawLength.length
        if(maxLength > 50) {
          Left(Seq(FormError(key, maxLengthError)))
        } else {
          Right(maxLength == 0)
        }

      }

    override def unbind(key: String, value: String): Map[String, String] = Map(key -> value)
  }

  val customPostcodeMapping = PostcodeMapping.postcode(
    formatError = Errors.invalidPostcodeOnLetter
  )


  def validateBuildingNameNumber = {

    val invalidCharRegex = """^[0-9A-Za-z\s\-\,]+$"""

    def validBuildingNameNumberLength(bNN: String) = bNN.length <= 50

    text
      .verifying(Errors.addressBuildingNameNumberRequired, bNN => bNN.nonEmpty)
      .verifying(Errors.buildingMaxLength, bNN => if (bNN.nonEmpty) validBuildingNameNumberLength(bNN) else true)
      .verifying(
        Errors.invalidCharAddress1,
        bNN => if (bNN.nonEmpty && validBuildingNameNumberLength(bNN)) bNN.matches(invalidCharRegex) else true
      )
  }

}
