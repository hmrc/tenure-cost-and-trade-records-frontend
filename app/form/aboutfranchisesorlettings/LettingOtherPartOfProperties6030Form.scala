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

import form.MappingSupport.createYesNoType
import models.submissions.common.AnswersYesNo
import play.api.data.Form
import play.api.data.Forms.mapping

object LettingOtherPartOfProperties6030Form {

  lazy val baseLettingOtherPartOfProperties6030Form: Form[AnswersYesNo] = Form(
    baseLettingOtherPartOfProperties6030Mapping
  )

  val baseLettingOtherPartOfProperties6030Mapping = mapping(
    "lettingOtherPartOfProperty6030" -> createYesNoType("error.lettingOtherPartOfProperty6030.missing")
  )(x => x)(b => Some(b))

  val lettingOtherPartOfProperties6030Form = Form(baseLettingOtherPartOfProperties6030Mapping)

}
