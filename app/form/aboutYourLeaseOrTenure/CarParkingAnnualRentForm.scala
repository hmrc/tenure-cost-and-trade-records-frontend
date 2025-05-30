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

package form.aboutYourLeaseOrTenure

import form.DateMappings.requiredDateMapping
import form.MappingSupport.currencyMapping
import play.api.data.Form
import play.api.data.Forms.tuple
import play.api.i18n.Messages

import java.time.LocalDate

/**
  * @author Yuriy Tumakha
  */
object CarParkingAnnualRentForm {

  def carParkingAnnualRentForm(using messages: Messages): Form[(BigDecimal, LocalDate)] =
    Form(
      tuple(
        "annualRent"    -> currencyMapping(".annualRent"),
        "fixedRentFrom" -> requiredDateMapping("carParkingFixedRentDate", allowPastDates = true)
      )
    )

}
