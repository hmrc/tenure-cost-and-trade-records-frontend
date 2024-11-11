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
  hasPermanentResidents: AnswersYesNo = AnswerNo,
  permanentResidents: List[ResidentDetail] = Nil,
)

object LettingHistory:

  val MaxNumberOfPermanentResidents = 5

  private def foldLettingHistory(ifEmpty: LettingHistory, copyFunc: LettingHistory => LettingHistory)(using
    session: Session
  ): Session =
    session.copy(lettingHistory =
      Some(
        session.lettingHistory.fold(ifEmpty)(copyFunc)
      )
    )

  def sessionWithPermanentResidents(hasPermanentResidents: AnswersYesNo)(using session: Session): Session =
    this.foldLettingHistory(
      ifEmpty = LettingHistory(
        hasPermanentResidents = hasPermanentResidents,
      ),
      copyFunc = lettingHistory =>
        hasPermanentResidents match
          case AnswerYes =>
            lettingHistory.copy(hasPermanentResidents = hasPermanentResidents)
          case AnswerNo  =>
            lettingHistory.copy(hasPermanentResidents = hasPermanentResidents, permanentResidents = Nil)
    )

  def hasPermanentResidents(session: Session): Option[AnswersYesNo] =
    for lettingHistory <- session.lettingHistory
    yield lettingHistory.hasPermanentResidents

  def sessionByAddingPermanentResident(residentDetail: ResidentDetail)(using session: Session): Session =
    this.foldLettingHistory(
      ifEmpty = LettingHistory(
        hasPermanentResidents = AnswerYes,
        permanentResidents = List(residentDetail),
      ),
      copyFunc = { lettingHistory =>
        lettingHistory.permanentResidents.zipWithIndex
          .find { (r, _) =>
            // find the eventually existing resident by name
            r.name == residentDetail.name
          }
          .map { (_, index) =>
            // patch the resident detail if found as existing
            lettingHistory
              .copy(permanentResidents = lettingHistory.permanentResidents.patch(index, List(residentDetail), 1))
          }
          .getOrElse {
            // not found ... then append it
            lettingHistory.copy(permanentResidents = lettingHistory.permanentResidents.:+(residentDetail))
          }
      }
    )

  def sessionByRemovingPermanentResidentAt(index: Int)(using session: Session): Session =
    this.foldLettingHistory(
      ifEmpty = LettingHistory(
        hasPermanentResidents = AnswerYes,
        permanentResidents = Nil,
      ),
      copyFunc = { lettingHistory =>
        lettingHistory.copy(permanentResidents = lettingHistory.permanentResidents.patch(index, Nil, 1))
      }
    )

  def permanentResidents(session: Session): List[ResidentDetail] =
    for
      lettingHistory     <- session.lettingHistory.toList
      permanentResidents <- lettingHistory.permanentResidents
    yield permanentResidents

  given Format[LettingHistory] = Json.format
