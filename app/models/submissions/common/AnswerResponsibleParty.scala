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

package models.submissions.common

import models.{EnumFormat, NamedEnum, NamedEnumSupport}
import play.api.libs.json.Format

sealed trait AnswerResponsibleParty extends NamedEnum {
  override def key: String = "answerLandlordTenantBoth"
}
object AnswerResponsiblePartyLandlord extends AnswerResponsibleParty {
  override def name: String = "landlord"
}
object AnswerResponsiblePartyTenant extends AnswerResponsibleParty {
  override def name: String = "tenant"
}
object AnswerResponsiblePartyBoth extends AnswerResponsibleParty {
  override def name: String = "both"
}

object AnswerResponsibleParty extends NamedEnumSupport[AnswerResponsibleParty] {
  implicit val format: Format[AnswerResponsibleParty] = EnumFormat(AnswerResponsibleParty)

  val all = List(AnswerResponsiblePartyLandlord, AnswerResponsiblePartyTenant, AnswerResponsiblePartyBoth)

  val key = all.head.key
}