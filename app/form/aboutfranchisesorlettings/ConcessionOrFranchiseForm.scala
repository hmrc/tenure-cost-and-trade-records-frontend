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

package form.aboutfranchisesorlettings

import form.MappingSupport.concessionOrFranchiseType
import models.submissions.aboutfranchisesorlettings.ConcessionOrFranchise
import play.api.data.Form
import play.api.data.Forms.mapping

object ConcessionOrFranchiseForm {
  lazy val baseConcessionOrFranchiseForm: Form[ConcessionOrFranchise] = Form(baseConcessionOrFranchiseMapping)

  val baseConcessionOrFranchiseMapping = mapping(
    "concessionOrFranchise" -> concessionOrFranchiseType
  )(x => x)(b => Some(b))

  val concessionOrFranchiseForm = Form(baseConcessionOrFranchiseMapping)
}
