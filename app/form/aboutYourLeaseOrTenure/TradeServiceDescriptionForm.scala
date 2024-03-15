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

package form.aboutYourLeaseOrTenure
import models.submissions.aboutYourLeaseOrTenure.TradeServicesDetails
import play.api.data.Form
import play.api.data.Forms.{default, mapping, optional, text}
import play.api.data.validation.Constraints.{maxLength, nonEmpty}

object TradeServiceDescriptionForm {

  val tradeServicesDescriptionForm: Form[TradeServicesDetails] =
    Form(
      mapping(
        "sumExcludingVat" -> optional(
          text
            .verifying("error.tradeDescription.invalidCurrency", s => s.matches("-?\\d+(\\.\\d+)?"))
            .transform[BigDecimal](s => BigDecimal(s.replace(",", "")), v => v.toString)
        ),
        "description"     -> default(text, "")
          .verifying(
            nonEmpty(errorMessage = "tradeServiceDescription.describe.error"),
            maxLength(500, "error.tradeServiceDescription.maxLength")
          )
      )(TradeServicesDetails.apply)(TradeServicesDetails.unapply)
    )

}
