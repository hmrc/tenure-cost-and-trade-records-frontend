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

import form.OptionalCurrencyMapping.partOfAnnualRent
import models.submissions.aboutYourLeaseOrTenure.RentIncludeTradeServicesInformationDetails
import play.api.data.Form
import play.api.data.Forms.{default, mapping, text}
import play.api.data.validation.Constraints.{maxLength, nonEmpty}
import play.api.i18n.Messages

object RentIncludeTradeServicesDetailsForm {

  def rentIncludeTradeServicesDetailsForm(annualRent: Option[BigDecimal] = None, otherIncludedPartsSum: BigDecimal = 0)(
    implicit messages: Messages
  ): Form[RentIncludeTradeServicesInformationDetails] = Form(
    mapping(
      "sumIncludedInRent" ->
        partOfAnnualRent(messages("error.rentIncludeTradeServicesDetails.title"), annualRent, otherIncludedPartsSum),
      "describeServices"  ->
        default(text, "").verifying(
          nonEmpty(errorMessage = "error.describeServices.required"),
          maxLength(500, "error.describeServices.maxLength")
        )
    )(RentIncludeTradeServicesInformationDetails.apply)(o => Some(Tuple.fromProductTyped(o)))
  )

}
