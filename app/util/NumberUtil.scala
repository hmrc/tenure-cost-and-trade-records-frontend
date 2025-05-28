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

package util

import uk.gov.hmrc.govukfrontend.views.html.components.Text

import java.text.NumberFormat
import java.util.Locale
import scala.language.implicitConversions
import scala.math.BigDecimal.RoundingMode.HALF_UP

/**
  * @author Yuriy Tumakha
  */
object NumberUtil:

  val zeroBigDecimal: BigDecimal = BigDecimal(0)

  implicit def bigDecimalToString(bigDecimal: BigDecimal): String =
    bigDecimal.asMoney

  implicit def bigDecimalOptToString(bigDecimalOpt: Option[BigDecimal]): String =
    bigDecimalOpt.getOrElse(zeroBigDecimal).asMoney

  implicit def intToString(number: Int): String =
    number.toString

  implicit def intOptToString(numberOpt: Option[Int]): String =
    numberOpt.getOrElse(0)

  implicit def stringOptToString(stringOpt: Option[String]): String =
    stringOpt.getOrElse("")

  implicit def seqBigDecimalToSeqString(values: Seq[BigDecimal]): Seq[String] =
    values.map(bigDecimalToString)

  implicit def seqBigDecimalOptToSeqString(values: Seq[Option[BigDecimal]]): Seq[String] =
    values.map(bigDecimalOptToString)

  implicit def functionBigDecimalToFunctionString[T](function: T => BigDecimal): T => String =
    function andThen bigDecimalToString

  implicit def functionBigDecimalOptToFunctionString[T](function: T => Option[BigDecimal]): T => String =
    function andThen bigDecimalOptToString

  implicit def functionStringOptToFunctionString[T](function: T => Option[String]): T => String =
    function andThen stringOptToString

  extension (str: String)
    def removeTrailingZeros: String =
      str.replace(".00", "")

    def escapedHtml: String =
      Text(str).asHtml.body

  extension (bigDecimal: BigDecimal)
    def asMoney: String =
      asMoneyFull.removeTrailingZeros

    def asMoneyFull: String =
      NumberFormat
        .getCurrencyInstance(Locale.UK)
        .format(bigDecimal)

    def withScale(scale: Int): String =
      withScaleFull(scale).removeTrailingZeros

    def withScaleFull(scale: Int): String =
      bigDecimal.setScale(scale, HALF_UP).toString
