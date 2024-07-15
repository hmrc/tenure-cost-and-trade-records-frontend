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

package form.aboutthetradinghistory

import models.submissions.aboutthetradinghistory.BunkerFuelCardDetails
import play.api.data.FormError
import utils.TestBaseSpec

class BunkerFuelCardDetailsFormSpec extends TestBaseSpec {
  "BunkerFuelCardDetailsForm" should {

    "bind valid data correctly" in {
      val data = Map(
        "name"        -> "Valid Name",
        "handlingFee" -> "100.50"
      )

      val form = BunkerFuelCardDetailsForm.bunkerFuelCardDetailsForm.bind(data)

      form.errors shouldBe empty
      form.value  shouldBe Some(BunkerFuelCardDetails("Valid Name", BigDecimal(100.50)))
    }

    "fail to bind when name is empty" in {
      val data = Map(
        "name"        -> "",
        "handlingFee" -> "100.50"
      )

      val form = BunkerFuelCardDetailsForm.bunkerFuelCardDetailsForm.bind(data)

      form.errors  should contain(FormError("name", "error.bunkerFuelCard.name.required"))
      form.value shouldBe None
    }

    "fail to bind when name exceeds maxLength" in {
      val data = Map(
        "name"        -> ("a" * 101),
        "handlingFee" -> "100.50"
      )

      val form = BunkerFuelCardDetailsForm.bunkerFuelCardDetailsForm.bind(data)

      form.errors  should contain(FormError("name", "error.bunkerFuelCardName.maxLength", Seq(100)))
      form.value shouldBe None
    }

    "fail to bind when handlingFee is not a valid number" in {
      val data = Map(
        "name"        -> "Valid Name",
        "handlingFee" -> "invalid"
      )

      val form = BunkerFuelCardDetailsForm.bunkerFuelCardDetailsForm.bind(data)

      form.errors  should contain(FormError("handlingFee", "error.bunkerFuelCard.handlingFee.invalidFormat"))
      form.value shouldBe None
    }

    "fail to bind when handlingFee is negative" in {
      val data = Map(
        "name"        -> "Valid Name",
        "handlingFee" -> "-100.50"
      )

      val form = BunkerFuelCardDetailsForm.bunkerFuelCardDetailsForm.bind(data)

      form.errors  should contain(FormError("handlingFee", "error.handlingFee.mustBeNonNegative"))
      form.value shouldBe None
    }

    "unbind valid data correctly" in {
      val model = BunkerFuelCardDetails("Valid Name", BigDecimal(100.50))
      val form  = BunkerFuelCardDetailsForm.bunkerFuelCardDetailsForm.fill(model)

      form.data should contain("name" -> "Valid Name")
      form.data should contain("handlingFee" -> "100.5")
    }
  }
}
