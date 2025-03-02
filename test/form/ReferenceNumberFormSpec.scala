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

package form

import ReferenceNumberForm.theForm as theForm
import models.submissions.ReferenceNumber
import org.scalatest.OptionValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

class ReferenceNumberFormSpec extends AnyFlatSpec with Matchers with OptionValues:

  it should "bind good data as expected" in {
    val data  = Map(
      "referenceNumber" -> "0123456789"
    )
    val bound = theForm.bind(data)
    bound.hasErrors mustBe false
    bound.data mustBe data
  }

  it should "unbind good data as expected" in {
    val referenceNumber = ReferenceNumber(
      value = "0123456789"
    )
    val filled          = theForm.fill(referenceNumber)
    filled.hasErrors mustBe false
    filled.data mustBe Map(
      "referenceNumber" -> "0123456789"
    )
  }

  it should "detect errors" in {
    val bound = theForm.bind(Map.empty)
    bound.hasErrors mustBe true
    bound.errors must have size 1
    bound.error("referenceNumber").value.message mustBe "error.referenceNumber.required"
  }
