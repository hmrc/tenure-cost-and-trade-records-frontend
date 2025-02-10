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

package util

import play.api.i18n.Messages
import utils.TestBaseSpec

import java.time.LocalDate

class AccessibilityUtilSpec extends TestBaseSpec {

  private val mockDateUtilLocalised       = mock[DateUtilLocalised]
  private val accessibilityUtil           = new AccessibilityUtil(mockDateUtilLocalised)
  private implicit val messages: Messages = mock[Messages]

  "ariaBuilder" should {
    "return formatted aria label message" in {
      val messageKey       = "test.key"
      val financialYearEnd = LocalDate.of(2024, 3, 31)
      val formattedDate    = "31 Mar 2024"
      val expectedMessage  = "Test message for year ending 31 Mar 2024"

      when(messages(messageKey)).thenReturn("Test message")
      when(messages("turnover.forYearEnding.aria")).thenReturn("for year ending")
      when(mockDateUtilLocalised.formatDayMonthAbbrYear(financialYearEnd)(messages)).thenReturn(formattedDate)

      accessibilityUtil.ariaBuilder(messageKey, financialYearEnd) shouldBe expectedMessage
    }
  }
}
