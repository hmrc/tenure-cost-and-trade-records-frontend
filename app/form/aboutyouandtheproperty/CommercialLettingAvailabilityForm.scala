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

package form.aboutyouandtheproperty

import play.api.data.Form
import play.api.data.Forms.{default, single, text}

object CommercialLettingAvailabilityForm {

  val commercialLettingAvailabilityForm: Form[Int] = Form(
    single(
      "commercialLettingAvailability" -> default(text, "")
        .verifying("error.commercialLettingAvailability.required", _.nonEmpty)
        .verifying("error.commercialLettingAvailability.range", s => s.isEmpty || s.matches("""\d+"""))
        .transform[Int](_.toInt, _.toString)
        .verifying("error.commercialLettingAvailability.range", n => n >= 0 && n <= 365)
    )
  )
}
