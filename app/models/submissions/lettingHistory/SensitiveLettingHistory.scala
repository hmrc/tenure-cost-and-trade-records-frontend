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

package models.submissions.lettingHistory

import models.submissions.common.AnswersYesNo
import uk.gov.hmrc.crypto.Sensitive

case class SensitiveLettingHistory(
  hasPermanentResidents: AnswersYesNo,
  permanentResidents: List[SensitiveResidentDetail],
) extends Sensitive[LettingHistory]:

  override def decryptedValue: LettingHistory =
    LettingHistory(
      hasPermanentResidents,
      permanentResidents.map(_.decryptedValue),
    )

object SensitiveLettingHistory:
  import crypto.MongoCrypto
  import play.api.libs.json.{Format, Json}
  implicit def format(using crypto: MongoCrypto): Format[SensitiveLettingHistory] = Json.format

  // encryption method
  def apply(lettingHistory: LettingHistory): SensitiveLettingHistory =
    SensitiveLettingHistory(
      lettingHistory.hasPermanentResidents,
      lettingHistory.permanentResidents.map(SensitiveResidentDetail(_)),
    )
