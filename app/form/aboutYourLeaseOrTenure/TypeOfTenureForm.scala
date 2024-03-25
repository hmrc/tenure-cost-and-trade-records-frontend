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

import form.MappingSupport.nonEmptyList
import models.submissions.aboutYourLeaseOrTenure.TypeOfTenure
import play.api.data.{Form, Mapping}
import play.api.data.Forms.{default, list, mapping, optional, text}
import play.api.data.validation.Constraints.maxLength
import uk.gov.voa.play.form.{Condition, ConditionalMapping, MandatoryOptionalMapping}

object TypeOfTenureForm {
  val typeOfTenureForm: Form[TypeOfTenure] = Form(
    mapping(
      "typeOfTenure"        -> list(text).verifying(
        nonEmptyList("error.typeOfTenure.required")
      ),
      "typeOfTenureDetails" ->

        default(text, "").verifying(
          maxLength(2000, "error.typeOfTenure.maxLength")
        )

//          mandatoryTest("typeOfTenure", validateTest)

    )(TypeOfTenure.apply)(TypeOfTenure.unapply)
  )

  def mandatoryTest[T](fieldName: String, mapping: Mapping[String]): Mapping[String] = {
    val condition: Condition = _.get(fieldName).map(_.size == 2).getOrElse(false)
    ConditionalMapping(condition, mapping, "", Seq.empty) // MandatoryOptionalMapping(mapping, Nil)
  }

  def validateTest:Mapping[String] = default(text, "").verifying(maxLength(2000, "error.typeOfTenure.maxLength"))


}
