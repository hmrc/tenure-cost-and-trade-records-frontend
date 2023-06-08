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

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

/**
  * @author Yuriy Tumakha
  */
class NumberUtilSpec extends AnyFlatSpec with should.Matchers {

  import NumberUtil._

  "NumberUtil.asMoney"     should "format numbers in UK money format" in {
    BigDecimal(1222000).asMoney shouldBe "£1,222,000"
    BigDecimal(333).asMoney     shouldBe "£333"
    BigDecimal(4444).asMoney    shouldBe "£4,444"
  }

  it                       should "show 2 digits after decimal point for money" in {
    BigDecimal(2777000.22).asMoney shouldBe "£2,777,000.22"
    BigDecimal(2777000.10).asMoney shouldBe "£2,777,000.10"
  }

  "NumberUtil.asMoneyFull" should "return pence amount even in case `.00`" in {
    BigDecimal(3222000.33).asMoneyFull shouldBe "£3,222,000.33"
    BigDecimal(777).asMoneyFull        shouldBe "£777.00"
    BigDecimal(8888).asMoneyFull       shouldBe "£8,888.00"
  }

}
