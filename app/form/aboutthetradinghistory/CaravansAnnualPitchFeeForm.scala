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

package form.aboutthetradinghistory

import form.ConditionalConstraintMappings.mandatoryIfOneOfValuesIs
import form.MappingSupport.*
import models.submissions.aboutthetradinghistory.Caravans.CaravansPitchFeeServices
import models.submissions.aboutthetradinghistory.CaravansAnnualPitchFee
import play.api.data.Form
import play.api.data.Forms.*
import play.api.data.validation.Constraints.maxLength

/**
  * 6045/6046 Trading history - Caravans annual pitch fee per provided service.
  *
  * @author Yuriy Tumakha
  */
object CaravansAnnualPitchFeeForm {

  val caravansAnnualPitchFeeForm: Form[CaravansAnnualPitchFee] =
    Form(
      mapping(
        "totalPitchFee"              -> moneyMappingRequired("caravans.totalPitchFee"),
        "servicesIncludedInPitchFee" -> enumMappingSeq(CaravansPitchFeeServices.fromName, _.toString),
        "rates"                      -> mandatoryIfOneOfValuesIs(
          "servicesIncludedInPitchFee",
          "rates",
          moneyMappingOptional("caravans.fee.rates"),
          "error.caravans.fee.rates.required"
        ),
        "waterAndDrainage"           -> mandatoryIfOneOfValuesIs(
          "servicesIncludedInPitchFee",
          "waterAndDrainage",
          moneyMappingOptional("caravans.fee.waterAndDrainage"),
          "error.caravans.fee.waterAndDrainage.required"
        ),
        "gas"                        -> mandatoryIfOneOfValuesIs(
          "servicesIncludedInPitchFee",
          "gas",
          moneyMappingOptional("caravans.fee.gas"),
          "error.caravans.fee.gas.required"
        ),
        "electricity"                -> mandatoryIfOneOfValuesIs(
          "servicesIncludedInPitchFee",
          "electricity",
          moneyMappingOptional("caravans.fee.electricity"),
          "error.caravans.fee.electricity.required"
        ),
        "otherPitchFeeDetails"       -> mandatoryIfOneOfValuesIs(
          "servicesIncludedInPitchFee",
          "other",
          optional(text.verifying(maxLength(1000, "error.caravans.fee.otherPitchFeeDetails.maxLength"))),
          "error.caravans.fee.otherPitchFeeDetails.required"
        )
      )(CaravansAnnualPitchFee.apply)(o => Some(Tuple.fromProductTyped(o)))
    )

}
