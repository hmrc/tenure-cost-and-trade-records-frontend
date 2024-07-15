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

package form.aboutYourLeaseOrTenure

import models.AnnualRent
import play.api.data.FormError
import util.NumberUtil.bigDecimalHelpers
import utils.TestBaseSpec

class CurrentAnnualRentFormSpec extends TestBaseSpec {
  "CurrentAnnualRentForm" should {

    "bind valid data correctly" in {
      val data = Map(
        "currentAnnualRent" -> "5000.00"
      )

      val form = CurrentAnnualRentForm.currentAnnualRentForm().bind(data)

      form.errors shouldBe empty
      form.value  shouldBe Some(AnnualRent(BigDecimal(5000.00)))
    }

    "fail to bind when rent is less than includedPartsSum" in {
      val includedPartsSum = BigDecimal(6000.00)
      val data             = Map(
        "currentAnnualRent" -> "5000.00"
      )

      val form = CurrentAnnualRentForm.currentAnnualRentForm(includedPartsSum).bind(data)

      form.errors  should contain(
        FormError(
          "currentAnnualRent",
          "error.currentAnnualRent.lessThanIncludedPartsSum",
          Seq(includedPartsSum.asMoney)
        )
      )
      form.value shouldBe None
    }

    "fail to bind when rent is not a valid number" in {
      val data = Map(
        "currentAnnualRent" -> "invalid"
      )

      val form = CurrentAnnualRentForm.currentAnnualRentForm().bind(data)

      form.errors  should contain(FormError("currentAnnualRent", "error.invalid_currency.annualRent"))
      form.value shouldBe None
    }

    "unbind valid data correctly" in {
      val model = AnnualRent(BigDecimal(5000.00))
      val form  = CurrentAnnualRentForm.currentAnnualRentForm().fill(model)

      form.data should contain("currentAnnualRent" -> "5000.0")
    }

    "fail to bind when no data is provided" in {
      val data = Map.empty[String, String]

      val form = CurrentAnnualRentForm.currentAnnualRentForm().bind(data)

      form.errors  should contain(FormError("currentAnnualRent", "error.required.annualRentExcludingVat"))
      form.value shouldBe None
    }
  }
}
