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

package form.aboutYourLeaseOrTenure

import form.MappingSupport.currencyMapping
import models.AnnualRent
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import util.NumberUtil._

object CurrentAnnualRentForm {

  val cdbMaxCurrencyAmount = 9999999.99

  def currentAnnualRentForm(includedPartsSum: BigDecimal = 0): Form[AnnualRent] = Form(
    mapping(
      "currentAnnualRent" -> currencyMapping()
        .verifying(
          Constraint[BigDecimal] { rent: BigDecimal =>
            if (rent < includedPartsSum)
              Invalid(
                ValidationError("error.currentAnnualRent.lessThanIncludedPartsSum", includedPartsSum.asMoney)
              )
            else Valid
          }
        )
    )(AnnualRent.apply)(AnnualRent.unapply)
  )

}
