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

package form.aboutthetradinghistory

import models.submissions.aboutthetradinghistory.BunkeredFuelQuestion
import models.submissions.common.AnswersYesNo.*
import play.api.data.FormError
import utils.TestBaseSpec

class BunkeredFuelQuestionFormSpec extends TestBaseSpec {

  "BunkeredFuelQuestionForm" should {

    "bind valid data correctly" in {
      val data = Map(
        "bunkeredFuelQuestion" -> "yes"
      )

      val form = BunkeredFuelQuestionForm.bunkeredFuelQuestionForm.bind(data)

      form.errors shouldBe empty
      form.value  shouldBe Some(BunkeredFuelQuestion(AnswerYes))
    }

    "fail to bind when no data is provided" in {
      val data = Map.empty[String, String]

      val form = BunkeredFuelQuestionForm.bunkeredFuelQuestionForm.bind(data)

      form.errors  should contain(FormError("bunkeredFuelQuestion", "error.bunkeredFuelQuestion.required"))
      form.value shouldBe None
    }

    "fail to bind when invalid data is provided" in {
      val data = Map(
        "bunkeredFuelQuestion" -> "invalid"
      )

      val form = BunkeredFuelQuestionForm.bunkeredFuelQuestionForm.bind(data)

      form.errors  should contain(FormError("bunkeredFuelQuestion", "error.bunkeredFuelQuestion.required"))
      form.value shouldBe None
    }

    "unbind valid data correctly" in {
      val model = BunkeredFuelQuestion(AnswerNo)
      val form  = BunkeredFuelQuestionForm.bunkeredFuelQuestionForm.fill(model)

      form.data should contain("bunkeredFuelQuestion" -> "no")
    }
  }
}
