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

package form.lettingHistory

import form.lettingHistory.HasCompletedLettingsForm.theForm
import models.submissions.common.AnswersYesNo.*
import test.FormSpec

class HasCompletedLettingsFormSpec extends FormSpec:

  "HasCompletedLettingsForm" should {
    "bind data as expected" in {
      val data  = Map(
        "answer" -> "yes"
      )
      val bound = theForm.bind(data)

      bound.hasErrors shouldBe false
      bound.data      shouldBe data
    }

    "unbind data as expected" in {
      val filled = theForm.fill(AnswerYes)

      filled.hasErrors shouldBe false
      filled.data      shouldBe Map("answer" -> "yes")
    }

    "detect errors" in {
      val bound = theForm.bind(
        Map(
          "answer" -> ""
        )
      )

      bound.hasErrors                   shouldBe true
      bound.errors                        should have size 1
      bound.error("answer").get.message shouldBe "lettingHistory.hasCompletedLettings.required"
    }
  }
