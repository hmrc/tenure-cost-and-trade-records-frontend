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

  given Conversion[Int, String] = _.toString

  given bigDecimalToString: Conversion[BigDecimal, String] = _.asMoney

  given bigDecimalOptToString: Conversion[Option[BigDecimal], String] = _.getOrElse(zeroBigDecimal).asMoney
  given intOptToString: Conversion[Option[Int], String]               = _.getOrElse(0)
  given stringOptToString: Conversion[Option[String], String]         = _.getOrElse("")

  given seqBigDecimalToSeqString: Conversion[Seq[BigDecimal], Seq[String]]            = _.map(bigDecimalToString)
  given seqBigDecimalOptToSeqString: Conversion[Seq[Option[BigDecimal]], Seq[String]] = _.map(bigDecimalOptToString)

  given functionStringOptToFunctionString[T]: Conversion[T => Option[String], T => String] = _ andThen stringOptToString
  given functionBigDecimalToFunctionString[T]: Conversion[T => BigDecimal, T => String]    = _ andThen bigDecimalToString
  given funcBigDecOptToFuncString[T]: Conversion[T => Option[BigDecimal], T => String]     = _ andThen bigDecimalOptToString

  extension (str: String)
    private def removedTrailingZeros: String =
      str.replace(".00", "")

    def escapedHtml: String =
      Text(str).asHtml.body

  extension (bigDecimal: BigDecimal)
    def asMoney: String =
      asMoneyFull.removedTrailingZeros

    def asMoneyFull: String =
      NumberFormat
        .getCurrencyInstance(Locale.UK)
        .format(bigDecimal)

    def withScale(scale: Int): String =
      withScaleFull(scale).removedTrailingZeros

    def withScaleFull(scale: Int): String =
      bigDecimal.setScale(scale, HALF_UP).toString
