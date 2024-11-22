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
  hasPermanentResidents: Option[AnswersYesNo] = None,
  permanentResidents: List[ResidentDetail] = Nil,
  hasCompletedLettings: Option[AnswersYesNo] = None,
  completedLettings: List[OccupierDetail] = Nil
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

  def withPermanentResidents(hasPermanentResidents: AnswersYesNo)(using session: Session): Session =
    this.foldLettingHistory(
      ifEmpty = LettingHistory(
        hasPermanentResidents = Some(hasPermanentResidents)
      ),
      copyFunc = lettingHistory =>
        hasPermanentResidents match
          case AnswerYes =>
            lettingHistory.copy(hasPermanentResidents = Some(AnswerYes))
          case AnswerNo  =>
            lettingHistory.copy(hasPermanentResidents = Some(AnswerNo), permanentResidents = Nil)
    )

  def hasPermanentResidents(session: Session): Option[AnswersYesNo] =
    for
      lettingHistory        <- session.lettingHistory
      hasPermanentResidents <- lettingHistory.hasPermanentResidents
    yield hasPermanentResidents

  def byAddingPermanentResident(residentDetail: ResidentDetail)(using session: Session): Session =
    this.foldLettingHistory(
      ifEmpty = LettingHistory(
        hasPermanentResidents = Some(AnswerYes),
        permanentResidents = List(residentDetail)
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

  def byRemovingPermanentResidentAt(index: Int)(using session: Session): Session =
    this.foldLettingHistory(
      ifEmpty = LettingHistory(
        hasPermanentResidents = Some(AnswerNo),
        permanentResidents = Nil
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

  def hasCompletedLettings(session: Session): Option[AnswersYesNo] =
    for
      lettingHistory       <- session.lettingHistory
      hasCompletedLettings <- lettingHistory.hasCompletedLettings
    yield hasCompletedLettings

  def withCompletedLettings(hasCompletedLettings: AnswersYesNo)(using session: Session): Session =
    this.foldLettingHistory(
      ifEmpty = LettingHistory(
        hasCompletedLettings = Some(hasCompletedLettings)
      ),
      copyFunc = lettingHistory =>
        hasCompletedLettings match
          case AnswerYes =>
            lettingHistory.copy(hasCompletedLettings = Some(AnswerYes))
          case AnswerNo  =>
            lettingHistory.copy(hasCompletedLettings = Some(AnswerNo), completedLettings = Nil)
    )

  def byAddingOccupierNameAndAddress(name: String, address: Address)(using session: Session): (Int, Session) =
    val occupierDetail                 = OccupierDetail(name, address, rental = None)
    val ifEmpty: (Int, LettingHistory) =
      (
        /* index = */ 0,
        LettingHistory(
          hasCompletedLettings = Some(AnswerYes),
          completedLettings = List(occupierDetail)
        )
      )

    val copyFunc: LettingHistory => (Int, LettingHistory) = { lettingHistory =>
      lettingHistory.completedLettings.zipWithIndex
        .find { (occupier, _) =>
          // find the eventually existing resident by name (and more importantly its index)
          occupier.name == name
        }
        .map { (_, foundIndex) =>
          // patch the occupier detail if it exists at foundIndex
          val patchedOccupier = lettingHistory
            .completedLettings(foundIndex)
            .copy(name = name, address = address)

          val patchedCompletedLettings = lettingHistory.completedLettings.patch(foundIndex, List(patchedOccupier), 1)
          val copiedLettingHistory     = lettingHistory.copy(completedLettings = patchedCompletedLettings)
          (foundIndex, copiedLettingHistory)
        }
        .getOrElse {
          // not found ... then append it to the list of completed lettings
          val lastIndex                 = lettingHistory.completedLettings.size
          val extendedCompletedLettings = lettingHistory.completedLettings.:+(occupierDetail)
          val copiedLettingHistory      = lettingHistory.copy(completedLettings = extendedCompletedLettings)
          (lastIndex, copiedLettingHistory)
        }
    }
    val (updatedIndex, updatedSession)                    = session.lettingHistory.fold(ifEmpty)(copyFunc)
    (updatedIndex, session.copy(lettingHistory = Some(updatedSession)))

  def byAddingOccupierRentalPeriod(index: Int, rentalPeriod: LocalPeriod)(using session: Session): Session =
    foldLettingHistory(
      ifEmpty = LettingHistory(
        hasCompletedLettings = Some(AnswerYes),
        completedLettings = Nil
      ),
      copyFunc = lettingHistory =>
        val maybeUpdatedOccupierDetails =
          for occupierDetail <- lettingHistory.completedLettings.lift(index)
          yield occupierDetail.copy(rental = Some(rentalPeriod))

        val patchedCompletedLettings =
          lettingHistory.completedLettings.patch(index, maybeUpdatedOccupierDetails.toList, 1)

        lettingHistory.copy(completedLettings = patchedCompletedLettings)
    )

  def completedLettings(session: Session): List[OccupierDetail] =
    for
      lettingHistory    <- session.lettingHistory.toList
      completedLettings <- lettingHistory.completedLettings
    yield completedLettings

  given Format[LettingHistory] = Json.format
