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

package form.lettingHistory

import form.lettingHistory.AdvertisingDetailForm.theForm
import models.submissions.lettingHistory.AdvertisingDetail

class AdvertisingDetailFormSpec extends FormSpec:

  it should "bind good data as expected" in {
    val data  = Map(
      "websiteAddress"          -> "123.uk",
      "propertyReferenceNumber" -> "3456aaa"
    )
    val bound = theForm.bind(data)
    bound.hasErrors mustBe false
    bound.data mustBe data
  }

  it should "unbind good data as expected" in {
    val residentDetail = AdvertisingDetail(
      websiteAddress = "123.uk",
      propertyReferenceNumber = "3456aaa"
    )
    val filled         = theForm.fill(residentDetail)
    filled.hasErrors mustBe false
    filled.data mustBe Map(
      "websiteAddress"          -> "123.uk",
      "propertyReferenceNumber" -> "3456aaa"
    )
  }

  it should "detect errors" in {
    // When the form gets submitted before being filled
    val bound = theForm.bind(
      Map(
        "websiteAddress"          -> "",
        "propertyReferenceNumber" -> ""
      )
    )
    bound.hasErrors mustBe true
    bound.errors must have size 1
    bound.error("websiteAddress").value.message mustBe "error.websiteAddressForProperty.required"
  }
