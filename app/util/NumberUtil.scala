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

package util

import java.text.NumberFormat
import java.util.Locale
import scala.math.BigDecimal.RoundingMode.HALF_UP

/**
  * @author Yuriy Tumakha
  */
object NumberUtil {

  implicit class stringHelpers(str: String) {
    def removeTrailingZeros: String =
      str.replace(".00", "")
  }

  implicit class bigDecimalHelpers(bigDecimal: BigDecimal) {

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

  }

}
