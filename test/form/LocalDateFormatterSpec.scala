/*
 * Copyright 2023 HM Revenue & Customs
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

import utils.TestBaseSpec

import java.time.LocalDate

class LocalDateFormatterSpec extends TestBaseSpec {

  def createLocalDateFormatter(
    fieldNameKey: String,
    allowPastDates: Boolean,
    allowFutureDates: Boolean
  ): LocalDateFormatter =
    new LocalDateFormatter(fieldNameKey, allowPastDates, allowFutureDates)(messages)

  "LocalDateFormatter" should {
    "validate a correct date without errors" in {
      val formData  = Map("date.day" -> "21", "date.month" -> "12", "date.year" -> "2020")
      val formatter = createLocalDateFormatter("date", allowPastDates = true, allowFutureDates = true)
      val result    = formatter.bind("date", formData)

      result.isRight shouldBe true
      result.foreach { date =>
        date shouldBe LocalDate.of(2020, 12, 21)
      }
    }

    "return error.date.day.invalid for an invalid day" in {
      val formData  = Map("date.day" -> "32", "date.month" -> "12", "date.year" -> "2020")
      val formatter = createLocalDateFormatter("date", allowPastDates = true, allowFutureDates = true)
      val result    = formatter.bind("date", formData)

      result.isLeft shouldBe true
      result.left.foreach { errors =>
        errors.exists(_.key == "date.day")                   shouldBe true
        errors.exists(_.message == "error.date.day.invalid") shouldBe true
      }
    }

    "return error.date.month.invalid for an invalid month" in {
      val formData  = Map("date.day" -> "22", "date.month" -> "13", "date.year" -> "2020")
      val formatter = createLocalDateFormatter("date", allowPastDates = true, allowFutureDates = true)
      val result    = formatter.bind("date", formData)

      result.isLeft shouldBe true
      result.left.foreach { errors =>
        errors.exists(_.key == "date.month")                   shouldBe true
        errors.exists(_.message == "error.date.month.invalid") shouldBe true
      }
    }

    "return error.date.year.invalid for an excessively long year" in {
      val formData  = Map("date.day" -> "20", "date.month" -> "12", "date.year" -> "20200")
      val formatter = createLocalDateFormatter("date", allowPastDates = true, allowFutureDates = true)
      val result    = formatter.bind("date", formData)

      result.isLeft shouldBe true
      result.left.foreach { errors =>
        errors.exists(_.key == "date.year")                   shouldBe true
        errors.exists(_.message == "error.date.year.invalid") shouldBe true
      }
    }

    "return error.date.before1900 for a date before 1900" in {
      val formData  = Map("date.day" -> "21", "date.month" -> "12", "date.year" -> "1788")
      val formatter = createLocalDateFormatter("date", allowPastDates = true, allowFutureDates = true)
      val result    = formatter.bind("date", formData)

      result.isLeft shouldBe true
      result.left.foreach { errors =>
        errors.exists(_.message == "error.date.before1900") shouldBe true
      }
    }

    "return error.date.mustInclude error for non-numeric day and missing month" in {
      val formData  = Map("date.day" -> "aaa", "date.year" -> "2000")
      val formatter = createLocalDateFormatter("date", allowPastDates = true, allowFutureDates = true)
      val result    = formatter.bind("date", formData)

      result.isLeft shouldBe true
      result.left.foreach { errors =>
        errors.exists(_.key == "date.month")                 shouldBe true
        errors.exists(_.message == "error.date.mustInclude") shouldBe true
      }
    }

    "return error.date.invalid error for non-numeric day and out of range month" in {
      val formData  = Map("date.day" -> "aaa", "date.month" -> "13", "date.year" -> "2000")
      val formatter = createLocalDateFormatter("date", allowPastDates = true, allowFutureDates = true)
      val result    = formatter.bind("date", formData)

      result.isLeft shouldBe true
      result.left.foreach { errors =>
        errors.exists(_.key == "date.day")               shouldBe true
        errors.exists(_.message == "error.date.invalid") shouldBe true
      }
    }
    "allow a past date when allowPastDates is true" in {
      val pastDate  = LocalDate.now.minusYears(1)
      val formData  = Map(
        "date.day"   -> pastDate.getDayOfMonth.toString,
        "date.month" -> pastDate.getMonthValue.toString,
        "date.year"  -> pastDate.getYear.toString
      )
      val formatter = createLocalDateFormatter("date", allowPastDates = true, allowFutureDates = true)

      val result = formatter.bind("date", formData)
      result.isRight shouldBe true
    }

    "not allow a past date when allowPastDates is false" in {
      val pastDate  = LocalDate.now.minusYears(1)
      val formData  = Map(
        "date.day"   -> pastDate.getDayOfMonth.toString,
        "date.month" -> pastDate.getMonthValue.toString,
        "date.year"  -> pastDate.getYear.toString
      )
      val formatter = createLocalDateFormatter("date", allowPastDates = false, allowFutureDates = true)

      val result = formatter.bind("date", formData)
      result.isLeft shouldBe true
      result.left.foreach { errors =>
        errors.exists(_.message == "error.date.beforeToday") shouldBe true
      }
    }

    "allow a future date when allowFutureDates is true" in {
      val futureDate = LocalDate.now.plusYears(1)
      val formData   = Map(
        "date.day"   -> futureDate.getDayOfMonth.toString,
        "date.month" -> futureDate.getMonthValue.toString,
        "date.year"  -> futureDate.getYear.toString
      )
      val formatter  = createLocalDateFormatter("date", allowPastDates = true, allowFutureDates = true)

      val result = formatter.bind("date", formData)
      result.isRight shouldBe true
    }

    "not allow a future date when allowFutureDates is false" in {
      val futureDate = LocalDate.now.plusYears(1)
      val formData   = Map(
        "date.day"   -> futureDate.getDayOfMonth.toString,
        "date.month" -> futureDate.getMonthValue.toString,
        "date.year"  -> futureDate.getYear.toString
      )
      val formatter  = createLocalDateFormatter("date", allowPastDates = true, allowFutureDates = false)

      val result = formatter.bind("date", formData)
      result.isLeft shouldBe true
      result.left.foreach { errors =>
        errors.exists(_.message == "error.date.mustBeInPast") shouldBe true
      }
    }
  }
}
