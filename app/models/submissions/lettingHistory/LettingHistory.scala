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

import models.Session
import models.submissions.common.{AnswerNo, AnswerYes, AnswersYesNo}
import play.api.libs.json.{Format, Json}

case class LettingHistory(
  isPermanentResidence: AnswersYesNo,
  permanentResidents: List[ResidentDetail] = Nil
)

object LettingHistory:

  private def foldLettingHistory(ifEmpty: LettingHistory, copyFunc: LettingHistory => LettingHistory)(using
    session: Session
  ): Session =
    session.copy(lettingHistory =
      Some(
        session.lettingHistory.fold(ifEmpty)(copyFunc)
      )
    )

  def sessionWithPermanentResidents(isPermanentResidence: AnswersYesNo)(using session: Session): Session =
    this.foldLettingHistory(
      ifEmpty = LettingHistory(
        isPermanentResidence = isPermanentResidence
      ),
      copyFunc = lettingHistory =>
        isPermanentResidence match
          case AnswerYes =>
            lettingHistory.copy(isPermanentResidence = isPermanentResidence)
          case AnswerNo  =>
            lettingHistory.copy(isPermanentResidence = isPermanentResidence, permanentResidents = Nil)
    )

  def isPermanentResidence(session: Session): Option[AnswersYesNo] =
    for lettingHistory <- session.lettingHistory
    yield lettingHistory.isPermanentResidence

  def sessionByAddingPermanentResident(residentDetail: ResidentDetail)(using session: Session): Session =
    this.foldLettingHistory(
      ifEmpty = LettingHistory(
        isPermanentResidence = AnswerYes,
        permanentResidents = List(residentDetail)
      ),
      copyFunc =
        lettingHistory => lettingHistory.copy(permanentResidents = lettingHistory.permanentResidents.:+(residentDetail))
    )

  def residentDetail(session: Session): Option[ResidentDetail] =
    for
      lettingHistory <- session.lettingHistory
      firstResident  <- lettingHistory.permanentResidents.headOption
    yield firstResident

  given Format[LettingHistory] = Json.format