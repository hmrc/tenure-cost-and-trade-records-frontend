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

import form.requestReferenceNumber.OptionalCurrencyMapping
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{Messages, MessagesApi}
import play.api.test.Helpers.stubMessagesApi
import utils.TestBaseSpec

class OptionalCurrencyMappingSpec extends TestBaseSpec {

  override implicit val messagesApi: MessagesApi = stubMessagesApi()
  override implicit val messages: Messages       = messagesApi.preferred(Seq.empty)

  "currencyMappingOptional" should {

    val testMapping = OptionalCurrencyMapping.partOfAnnualRent("test", Some(BigDecimal(100)), 20)

    "evaluate empty input as valid" in {
      val form   = Form(single("amount" -> testMapping))
      val result = form.bind(Map("amount" -> ""))
      result.errors shouldBe empty
    }

    "evaluate numerical value as valid" in {
      val form   = Form(single("amount" -> testMapping))
      val result = form.bind(Map("amount" -> "50"))
      result.errors shouldBe empty
    }

    "evaluate an not numerical value as invalid" in {
      val form   = Form(single("amount" -> testMapping))
      val result = form.bind(Map("amount" -> "invalid"))
      result.errors                should not be empty
      result.errors.head.message shouldBe "error.optCurrency.invalid"
    }

    "evaluate a negative value as invalid" in {
      val form   = Form(single("amount" -> testMapping))
      val result = form.bind(Map("amount" -> "-50"))
      result.errors                should not be empty
      result.errors.head.message shouldBe "error.optCurrency.negative"
    }

    "invalidate an number greater than annualRent" in {
      val form   = Form(single("amount" -> testMapping))
      val result = form.bind(Map("amount" -> "150"))
      result.errors                should not be empty
      result.errors.head.message shouldBe "error.optCurrency.graterThanAnnualRent"
    }

    "invalidate case combined sum included to cover equipment and trade services greater than annualRent" in {
      val form   = Form(single("amount" -> testMapping))
      val result = form.bind(Map("amount" -> "81"))
      result.errors                should not be empty
      result.errors.head.message shouldBe "error.includedPartsSum.graterThanAnnualRent"
    }

  }

}
