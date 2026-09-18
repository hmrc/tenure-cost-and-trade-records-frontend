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

import form.lettingHistory.WhenWasLastLetForm.theForm
import play.api.data.Form
import test.FormSpec

import java.time.LocalDate

class WhenWasLastLetFormSpec extends FormSpec:

  "WhenWasLastLetForm" should {
    "bind good data as expected" in new SessionFixture {
      val data: Map[String, String] = Map(
        "date.day"   -> "1",
        "date.month" -> "4",
        "date.year"  -> "2024"
      )
      val bound: Form[LocalDate]    = theForm.bind(data)

      bound.hasErrors shouldBe false
      bound.data      shouldBe data
    }

    "unbind good data as expected" in new SessionFixture {
      val date: LocalDate         = LocalDate.of(2024, 8, 13)
      val filled: Form[LocalDate] = theForm.fill(date)

      filled.hasErrors shouldBe false
      filled.data      shouldBe Map(
        "date.day"   -> "13",
        "date.month" -> "8",
        "date.year"  -> "2024"
      )
    }

    "detect errors related to fields being required" in new SessionFixture {
      val bound: Form[LocalDate] = theForm.bind(
        Map(
          "date.day"   -> "",
          "date.month" -> "",
          "date.year"  -> ""
        )
      )

      bound.hasErrors                     shouldBe true
      bound.errors                          should have size 1
      bound.error("date.day").get.message shouldBe "error.date.required"
    }
  }
