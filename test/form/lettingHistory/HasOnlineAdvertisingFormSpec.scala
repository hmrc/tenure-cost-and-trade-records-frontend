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

package form.lettingHistory

import form.lettingHistory.HasOnlineAdvertisingForm.theForm
import models.submissions.common.AnswersYesNo.*

class HasOnlineAdvertisingFormSpec extends FormSpec:

  it should "bind data as expected" in {
    val data  = Map(
      "answer" -> "yes"
    )
    val bound = theForm.bind(data)
    bound.hasErrors mustBe false
    bound.data mustBe data
  }

  it should "unbind data as expected" in {
    val filled = theForm.fill(AnswerYes)
    filled.hasErrors mustBe false
    filled.data mustBe Map(
      "answer" -> "yes"
    )
  }

  it should "detect errors" in {
    // When the form gets submitted before being filled
    val bound = theForm.bind(
      Map(
        "answer" -> ""
      )
    )
    bound.hasErrors mustBe true
    bound.errors must have size 1
    bound
      .error("answer")
      .value
      .message mustBe "lettingHistory.hasOnlineAdvertising.required"
  }
