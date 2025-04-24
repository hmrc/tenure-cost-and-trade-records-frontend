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

import play.api.i18n.{Lang, Messages, MessagesApi}
import utils.TestBaseSpec

import java.time.LocalDate
import java.util.Locale

/**
  * @author Yuriy Tumakha
  */
class DateUtilLocalisedSpec extends TestBaseSpec:

  def localeOf(lang: String) = new Locale(lang) // TODO: use Locale.of when JDK21 become available in sm2 on Jenkins

  val en: Locale                  = Locale.UK
  val cy: Locale                  = localeOf("cy")
  val uk: Locale                  = localeOf("uk")
  val unavailableLocale: Locale   = localeOf("xy")
  private val testDate: LocalDate = LocalDate.of(2025, 4, 17)
  private val dateEN              = "17 April 2025"
  private val dateCY              = "17 Ebrill 2025"
  private val dateMonthAbbrEN     = "17 Apr 2025"
  private val dateMonthAbbrCY     = "17 Ebr 2025"

  private val dateUtilLocalised = inject[DateUtilLocalised]
  private val messagesApi       = inject[MessagesApi]

  def messagesForLocale(locale: Locale): Messages = messagesApi.preferred(Seq(Lang(locale)))

  implicit val messagesEN: Messages = messagesForLocale(en)

  "DateUtilLocalised" should {
    "format LocalDate" in {
      dateUtilLocalised.formatDate(testDate)                                       shouldBe dateEN
      dateUtilLocalised.formatDate(testDate)(messagesForLocale(en))                shouldBe dateEN
      dateUtilLocalised.formatDate(testDate)(messagesForLocale(cy))                shouldBe dateCY
      dateUtilLocalised.formatDate(testDate)(messagesForLocale(uk))                shouldBe dateEN
      dateUtilLocalised.formatDate(testDate)(messagesForLocale(unavailableLocale)) shouldBe dateEN
    }

    "format LocalDate using month abbreviation by .formatDayMonthAbbrYear" in {
      dateUtilLocalised.formatDayMonthAbbrYear(testDate)                        shouldBe dateMonthAbbrEN
      dateUtilLocalised.formatDayMonthAbbrYear(testDate)(messagesForLocale(en)) shouldBe dateMonthAbbrEN
      dateUtilLocalised.formatDayMonthAbbrYear(testDate)(messagesForLocale(cy)) shouldBe dateMonthAbbrCY
      dateUtilLocalised.formatDayMonthAbbrYear(testDate)(messagesForLocale(uk)) shouldBe dateMonthAbbrEN
    }
  }
