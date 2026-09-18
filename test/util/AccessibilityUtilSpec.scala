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

package util

import test.MessagesApiSpec

import java.time.LocalDate

class AccessibilityUtilSpec extends MessagesApiSpec:

  private val accessibilityUtil = AccessibilityUtil(dateUtilLocalised)

  "AccessibilityUtil.ariaBuilder" should {
    "return formatted aria label message" in {
      val messageKey       = "test.msg.key"
      val financialYearEnd = LocalDate.of(2024, 3, 31)
      val expectedMessage  = "test.msg.key turnover.forYearEnding.aria 31 Mar 2024"

      accessibilityUtil.ariaBuilder(messageKey, financialYearEnd) shouldBe expectedMessage
    }
  }
