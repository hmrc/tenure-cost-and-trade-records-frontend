/*
 * Copyright 2026 HM Revenue & Customs
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

import form.ReferenceNumberForm.theForm
import models.submissions.ReferenceNumber
import uk.gov.hmrc.vo.unit.test.BaseSpec

class ReferenceNumberFormSpec extends BaseSpec:

  "ReferenceNumberForm" should {
    "bind good data as expected" in {
      val data  = Map(
        "referenceNumber" -> "0123456789"
      )
      val bound = theForm.bind(data)

      bound.hasErrors shouldBe false
      bound.data      shouldBe data
    }

    "unbind good data as expected" in {
      val referenceNumber = ReferenceNumber("0123456789")
      val filled          = theForm.fill(referenceNumber)

      filled.hasErrors shouldBe false
      filled.data      shouldBe Map("referenceNumber" -> "0123456789")
    }

    "detect errors" in {
      val bound = theForm.bind(Map.empty)

      bound.hasErrors                            shouldBe true
      bound.errors                                 should have size 1
      bound.error("referenceNumber").get.message shouldBe "error.referenceNumber.required"
    }
  }
